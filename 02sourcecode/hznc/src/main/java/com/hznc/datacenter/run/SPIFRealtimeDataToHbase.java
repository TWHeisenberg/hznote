package com.hznc.datacenter.run;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hznc.datacenter.bean.CycleRange;
import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.bean.TDuration;
import com.hznc.datacenter.datasource.DataSource;
import com.hznc.datacenter.datasource.SPIFLocalFileDataSource;
import com.hznc.datacenter.datasource.SPIFRealtimeDataSource;
import com.hznc.datacenter.hbase.HbaseClient;
import com.hznc.datacenter.hbase.MockHbaseClient;
import com.hznc.datacenter.hbase.MyHbaseClient;
import com.hznc.datacenter.task.MergeAndStore;
import com.hznc.datacenter.task.StoreTick;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import static com.hznc.datacenter.constants.Constants.*;

/**
 * 股指期货实时数据入hbase
 *
 * @author TWang
 * @date 2021-04-19 10:30:00
 */
public class SPIFRealtimeDataToHbase extends DataCenterRunner {
  private static Logger logger = Logger.getLogger(SPIFRealtimeDataToHbase.class);

  // 操作hbase的客户端
  private static HbaseClient hbaseClient;
  // 配置的属性
  private static Properties prop;
  // 数据源
  private static DataSource dataSource;
  // 日期格式化工具
  public static DateFormat dateFormat;
  // 保存tick数据
  private StoreTick storeTickTask;
  // 合并k线的线程
  private List<MergeAndStore> mergeAndStores;
  // 和子进程通信，保证子进程正常退出
  private CountDownLatch statusReporter;
  // 保存今天的主连代码
  private static Set<String> zhuli;
  // 将不同周期k线分到不同线程中去处理:<周期， 周期的列表，如：秒级周期包括1s,3s,5s...>
  private Map<String, CycleRange> cycles = new HashMap() {
    {
      // 1s，2s,3s的数据呈2的倍数增长，这里合理分配下线程任务，1s单独线程做
      put(CYCEL_LEVEL_SECOND_ONE, new CycleRange(CYCEL_SECOND_ONE_LIST));
      put(CYCEL_LEVEL_SECONDS, new CycleRange(CYCEL_SECONDS_LIST));
      put(CYCEL_LEVEL_MINUTES, new CycleRange(CYCEL_MINUTES_LIST));
      put(CYCEL_LEVEL_DAY, new CycleRange(CYCEL_DAY_LIST));
    }
  };

  public void run(String... args) throws Exception {
    // 程序的运行时间
    // 9点-15点30分
    TDuration dayDuration = new TDuration(BUSINESS_T0900, BUSINESS_T1600);
    // 21点-24点
    TDuration nightDurationA = new TDuration(BUSINESS_T2100, BUSINESS_T2400);
    // 00点-3点30分
    TDuration nightDurationB = new TDuration(BUSINESS_T0000, BUSINESS_T0330);

    // 需要补k线的收盘时间：<收盘时间，是否补过k线>
    Map<Calendar, Boolean> packKlinePoints = Maps.newHashMap();
    packKlinePoints.put(BUSINESS_T1130, false);
    packKlinePoints.put(BUSINESS_T1500, false);
    boolean exit = false;
    /**
     * 1. 调用c++接口获取数据
     * 2. 合并线程启动
     * 3. 发送hbase
     */
    init(args);
    // 开始保存tick数据的线程
    startStoreTickTask();
    // 开始合并并存储k线任务的线程
    startMergeStoreTasks();
    // 开始接收数据的线程
    dataSource.startReceiveTickdata(prop);


    Calendar nowCalendar;
    Queue<KData> tickDataQueue = dataSource.getTickDataQueue();
    int sleepSeconds = 1; // 每次睡多久
    int sleepedSeconds = 0; // 已经睡了多久
    while (!exit) {
      // 是否在程序运行区间
      nowCalendar = new GregorianCalendar();
      if (!withinTDuration(nowCalendar, dayDuration, nightDurationA, nightDurationB)) {
        logger.info("It's time for sleep, exit.");
        exit = true;
      }

      if (exit) {
        continue; // 说明不在运行的时间段
      }

      if (tickDataQueue.isEmpty()) {

        Thread.sleep(sleepSeconds * 1000); // TODO(TWang): 夜盘？
        sleepedSeconds += sleepSeconds;
        if(sleepedSeconds % 3 == 0){
          // 连续3s没有数据才提示日志
          logger.info("day market: waiting for data.");

          // 如果是收盘了，补一下k线
          int deviationSecond = 6;  // 运行的时间范围，向后的误差
          Calendar closeTime = closedAndGet(packKlinePoints, deviationSecond);
          if(closeTime != null){
            logger.info("start pack close kline.");
            packCloseKline(closeTime);
          }
        }
        continue;
      }
      sleepedSeconds = 0;

      // 获取数据
      KData kdata = tickDataQueue.poll();
      // 异步保存tick
      storeTickdataAsync(kdata);
      // 发布到所有的合并线程内
      publishToMergeThread(kdata);
      // 生成主力数据
      copyToZhuli(kdata, zhuli);
    }

    // 通知并阻塞等待子线程退出
    beforeExit();
    logger.info(String.format("Program end. time: %s", dateFormat.format(new Date())));
    System.exit(0);
  }

  // 通知子线程补k线， tick数据也要更新
  private void packCloseKline(Calendar closeTime) {
    // 保存tick的线程
    storeTickTask.setCloseTime(closeTime);
    // 合并k线的线程
    for(MergeAndStore mergeAndStore : mergeAndStores){
      mergeAndStore.setCloseTime(closeTime);
    }

  }

  /**
   * 如果需要补收盘k线，返回收盘的时间
   * @param packKlinePoints 需要补k线收盘时间
   * @param deviation 向后误差秒数
   */
  @VisibleForTesting
  Calendar closedAndGet(Map<Calendar, Boolean> packKlinePoints, int deviation) {
    // 现在的时间
    long nowTimeStamp = System.currentTimeMillis() / 1000;
    for(Map.Entry<Calendar, Boolean> point : packKlinePoints.entrySet()){
      long endTimeStamp = point.getKey().getTimeInMillis() / 1000;
      if(!point.getValue() && (nowTimeStamp >= endTimeStamp && nowTimeStamp <= endTimeStamp + deviation)){
        // 当前时间没有补过k线并且在收盘时间向后偏差内
        return point.getKey();
      }
    }
    return null;
  }

  // 通知到子线程线退出
  @VisibleForTesting
  void beforeExit() throws InterruptedException {
    // 保存tick的线程
    storeTickTask.setTimeToExit(true);
    // 合并k线的线程
    for(MergeAndStore mergeAndStore : mergeAndStores){
      mergeAndStore.setTimeToExit(true);
    }
    // TODO(TWang): timeout
    statusReporter.await();
  }

  @VisibleForTesting
  void startStoreTickTask() {
    storeTickTask = new StoreTick(hbaseClient, statusReporter);
    // 构造一个单线程池，自定义线程名称
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("store-tick").build();
    ExecutorService storeTickService = new ThreadPoolExecutor(1, 1,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue(), threadFactory);

    storeTickService.execute(storeTickTask);
  }

  // 保存到主力数据
  private void copyToZhuli(KData kdata, Set<String> zhuli) throws CloneNotSupportedException {
    if(!zhuli.contains(kdata.getSymbol())){
      return;
    }
    KData zhulian = kdata.clone();
    zhulian.setSymbol(zhulian.getSymbol().replaceAll("\\d+", "9999"));
    // 保存到tick数据
    storeTickdataAsync(zhulian);
    // 生成k线数据
    publishToMergeThread(zhulian);
  }

  // 启动合并的线程
  @VisibleForTesting
  void startMergeStoreTasks() {
    // 构造一个定长，固定名称的线程池
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("mergeAndstore-kline-%s").build();
    ExecutorService executorService = new ThreadPoolExecutor(cycles.size(), cycles.size(),
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue(), threadFactory);

    for (Entry<String, CycleRange> entry : cycles.entrySet()) {
      CycleRange cycleRange = entry.getValue();
      // TODO(TWang):hbaseClient是否线程安全
      MergeAndStore mergeAndStore = new MergeAndStore(hbaseClient, cycleRange, statusReporter);
      executorService.execute(mergeAndStore);
      mergeAndStores.add(mergeAndStore);
    }
    logger.info("start mergeAndStore kline thread, size:" + mergeAndStores.size());
  }

  // 判断是否在指定的时间区间
  @VisibleForTesting
  static boolean withinTDuration(Calendar nowCalendar, TDuration... durations) {
    boolean withinTime = false;
    for (TDuration duration : durations) {
      // 在区间内
      if (nowCalendar.compareTo(duration.getBegin()) >= 0
          && nowCalendar.compareTo(duration.getEnd()) <= 0) {
        withinTime = true;
      }
    }
    return withinTime;
  }

  @VisibleForTesting
  void storeTickdataAsync(KData kdata) {
    if(!storeTickTask.add(kdata)){
      logger.error("add tick fail." + kdata.getRowKey(true));
    }
  }

  // 初始化配置和hbase客户端
  public void init(String... args) throws Exception {
    if (args.length == 0 || Strings.isNullOrEmpty(args[0])) {
      String errorMsg = "args must not be empty.";
      System.out.println(errorMsg);
      throw new IllegalArgumentException(errorMsg);
    }
    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    this.prop = new Properties();

    try (InputStream in = new FileInputStream(args[0]);
        InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
      prop.load(reader);
    }

    // 校验一下必须的配置
    if (Strings.isNullOrEmpty(prop.getProperty("zkServerIp")) || Strings
        .isNullOrEmpty("zkServerPort")) {
      throw new IllegalArgumentException("Incomplete configuration...");
    }

    // 初始化hbase客户端
    Map<String, String> hbaseConf = Maps.newHashMap();
    hbaseConf.put("zkServerIp", prop.getProperty("zkServerIp"));
    hbaseConf.put("zkServerPort", prop.getProperty("zkServerPort"));
    // 测试用的
    setTest(Boolean.parseBoolean(prop.getProperty("test", "false")));

    if (!getTest()) {
      hbaseClient = MyHbaseClient.getInstance(hbaseConf);
    } else {
      hbaseClient = new MockHbaseClient();
    }

    mergeAndStores = Lists.newArrayList();
    // 从数据库查今天的主力代码
    this.zhuli = queryZhuliCode(prop);
    logger.info("Today's zhuli code:" + zhuli);
    loadLogConfig();
    dataSource = new SPIFRealtimeDataSource();
    // mergeAndStore的线程数+storeTick(单线程)
    statusReporter = new CountDownLatch(cycles.size() + 1);
    logger.info("Init done.");
  }

  private Set<String> queryZhuliCode(Properties prop) throws SQLException, ClassNotFoundException {
    Set<String> zhuli = Sets.newHashSet();
    String sqlserverIp = prop.getProperty("sqlserverIp");
    String sqlserverPort = prop.getProperty("sqlserverPort");
    String sqlserverUser = prop.getProperty("sqlserverUser");
    String sqlserverPwd = prop.getProperty("sqlserverPwd");
    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    Statement sta = getSqlConn(sqlserverIp, sqlserverPort, sqlserverUser, sqlserverPwd).createStatement();
    String sql = "SELECT  [日期] ,[代码]  FROM [Public1].[dbo].[T150_主力合约] "+
        "where 日期=\'"+today+"\';";
    ResultSet result = sta.executeQuery(sql);
    while(result.next()){
      StringBuilder cysm =new StringBuilder(result.getString("代码")).delete(2, 4);
      zhuli.add(cysm+"");
    }
    return zhuli;
  }

  // 创建数据库连接
  private Connection getSqlConn(String ip, String port, String user, String pwd) throws SQLException, ClassNotFoundException {
    Properties properties = new Properties();
    properties.setProperty("user", user);
    properties.setProperty("password", pwd);
    java.sql.Connection conn = getConnection(
        "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        String.format("jdbc:sqlserver://%s:%s;DatabaseName=Public1",ip,port),
        properties);
    return conn;
  }

  public static java.sql.Connection getConnection(String driver, String url,
      Properties properties) throws ClassNotFoundException, SQLException {
    Class.forName(driver);
    return DriverManager.getConnection(url, properties);
  }

  // 将tick数据发布给到所有的周期合并线程内
  public void publishToMergeThread(KData kdata) {
    for (CycleRange cycleRange : cycles.values()) {
      cycleRange.addData(kdata); // TODO(TWang): 是否要clone?
    }

  }

  private void loadLogConfig() {
    Path path = Paths.get("etc/log4j.properties");
    String logConfig = path.toFile().getAbsolutePath();
    if(!Files.exists(path)){
      logConfig = this.getClass().getResource("/log4j.properties").getFile();
    }
    PropertyConfigurator.configure(logConfig);
  }

  public StoreTick getStoreTickTask(){
    return this.storeTickTask;
  }

  // 测试用的，为true时:不连接hbase
  private boolean test;

  @VisibleForTesting
  void setTest(boolean test) {
    this.test = test;
  }

  @VisibleForTesting
  boolean getTest() {
    return this.test;
  }

  public HbaseClient getHbaseClient() {
    return hbaseClient;
  }

  @VisibleForTesting
  List<MergeAndStore> getMergeAndStores() {
    return mergeAndStores;
  }

  @VisibleForTesting
  void setCycles(Map<String, CycleRange> cycles) {
    this.cycles = cycles;
  }

  @VisibleForTesting
  void setStatusReporter(CountDownLatch latch){
    this.statusReporter = latch;
  }

  public static void main(String[] args) throws Exception {
    SPIFRealtimeDataToHbase runner = new SPIFRealtimeDataToHbase();
    runner.run(args);
  }
}
