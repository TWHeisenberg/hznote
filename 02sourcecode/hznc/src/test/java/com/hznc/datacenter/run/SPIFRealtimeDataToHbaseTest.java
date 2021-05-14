package com.hznc.datacenter.run;


import static com.hznc.datacenter.constants.Constants.*;
import static com.hznc.datacenter.run.SPIFRealtimeDataToHbase.withinTDuration;
import static com.hznc.datacenter.utils.CreateKLineUtil.normtime;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hznc.datacenter.bean.CycleRange;
import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.bean.TDuration;
import com.hznc.datacenter.bean.cnative.CKData;
import com.hznc.datacenter.datasource.SPIFLocalFileDataSource;
import com.hznc.datacenter.datasource.SPIFRealtimeDataSource.DataReceiveCallback;
import com.hznc.datacenter.hbase.HbaseClient;
import com.hznc.datacenter.hbase.MockHbaseClient;
import com.hznc.datacenter.task.MergeAndStore;
import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SPIFRealtimeDataToHbaseTest {

  private SPIFRealtimeDataToHbase runner;
  private File IC05TestFile;
  private SPIFLocalFileDataSource fileDataSource;

  @Before
  public void setup() throws Exception {
    fileDataSource = new SPIFLocalFileDataSource();
    String proptieFile = SPIFRealtimeDataToHbaseTest.class.getResource("/config.properties")
        .getFile();
    IC05TestFile = new File(SPIFRealtimeDataToHbaseTest.class.getResource("/test-data/SPIF/fullDay/IC2105.txt").getFile());
    runner = new SPIFRealtimeDataToHbase();
    runner.setTest(false);
    runner.init(proptieFile);
  }

  @Test // 测试插入的数据数量正确
  public void storeTickdataTest() throws Exception {
    // 从文件构造tick测试数据
    int lineCount = 99; // total:6854
    Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, lineCount);
    Map<String, Integer> recordSet = Maps.newHashMap();
    int expectCount = kdatas.size();
    // 保证key没有覆盖的情况
    Set<String> rowkeys = Sets.newHashSet();
    // 设置一个线程，保证可以退出
    runner.setStatusReporter(new CountDownLatch(1));
    // 开启保存tick数据的线程
    runner.startStoreTickTask();

    for(KData kdata : kdatas){
      String symbolWithTime = kdata.getSymbol() + "_" + kdata.getUtime(false);
      int sort = DataReceiveCallback.sortTickWithinSecond(symbolWithTime, recordSet);
      kdata.setSort(sort);

      runner.storeTickdataAsync(kdata);
      rowkeys.add(kdata.getRowKey(true));
    }

    // 等待发送完毕
    AtomicLong insertCount = runner.getStoreTickTask().getInsertedTickCount();

    // 等异步经过发送完毕，但要设置超时时间
    long start = System.currentTimeMillis() / 1000;
    while(insertCount.longValue() < lineCount){ // 行数固定
      long duration = System.currentTimeMillis()/1000 - start; // s
      if(duration > 1000){  // 10s超时了
        Assert.assertTrue(false);
      }
    }
    runner.beforeExit();
    Assert.assertEquals(expectCount, insertCount.longValue());
    Assert.assertEquals(expectCount, rowkeys.size());
  }



  @Test
  public void withinTimeTest(){
    Calendar calendar = new GregorianCalendar();
    // 当月份为
    calendar.set(Calendar.HOUR_OF_DAY, 14);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    TDuration dayDuration = new TDuration(BUSINESS_T0900, BUSINESS_T1500);
    TDuration nightDurationA = new TDuration(BUSINESS_T2100, BUSINESS_T2400);
    TDuration nightDurationB = new TDuration(BUSINESS_T0000, BUSINESS_T0300);

    Assert.assertEquals(true, withinTDuration(calendar,dayDuration));
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 00);
    calendar.set(Calendar.SECOND, 00);
    Assert.assertEquals(true, withinTDuration(calendar,nightDurationA));
    calendar.set(Calendar.HOUR_OF_DAY, 2);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    Assert.assertEquals(true, withinTDuration(calendar,nightDurationB));
    calendar.set(Calendar.HOUR_OF_DAY, 15);
    calendar.set(Calendar.MINUTE, 30);
    calendar.set(Calendar.SECOND, 01);
    Assert.assertEquals(false, withinTDuration(calendar,dayDuration));
  }

  @Test
  public void mergeToKlineAndRecordTest_1s() throws Exception {
    // 从文件构造tick测试数据
    Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, 4);
    // 设置要合并的周期
    Map<String, CycleRange> cycles = Maps.newHashMap();
    cycles.put(CYCEL_LEVEL_SECOND_ONE, new CycleRange(CYCEL_SECOND_ONE_LIST));// 1s
    runner.setCycles(cycles);
    // 开始合并线程
    runner.startMergeStoreTasks();
    // 合并1s的tick
    for(KData kdata : kdatas){
      runner.publishToMergeThread(kdata);
    }

    Thread.sleep(1*1000); // 等合并完成
    MergeAndStore result = runner.getMergeAndStores().get(0);
    Map<String, KData> mergedKlineSet = result.getMergedKlineSet();
    // 获取周期对应处理线程的结果
    KData kline_1s = mergedKlineSet.get("IC05_1");
    System.out.println(kline_1s); //  TODO: assert
  }

  @Test // 确定合并后的k线是否正确
  public void mergeToKLinTest_5s() throws InterruptedException {
    // 从文件构造tick测试数据
    Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, 6849, 6);
    // 设置要合并的周期
    Map<String, CycleRange> cycles = Maps.newHashMap();
    int[] cycelList = {CYCEL_SECOND_FIVE}; // 5s
    cycles.put(CYCEL_LEVEL_SECONDS, new CycleRange(cycelList));
    runner.setCycles(cycles);
    // 开始合并线程
    runner.startMergeStoreTasks();
    // 合并5s的tick
    for(KData kdata : kdatas){
      runner.publishToMergeThread(kdata);
    }

    Thread.sleep(5*1000 ); // 等合并完成
    MergeAndStore result = runner.getMergeAndStores().get(0);
    Map<String, KData> mergedKlineSet = result.getMergedKlineSet();
    // 获取周期对应处理线程的结果
    KData kline_5s = mergedKlineSet.get("IC05_5");
    Assert.assertEquals("IC05_1617246050", kline_5s.getRowKey(false)); // 取最高tick
    Assert.assertEquals(6184, kline_5s.getHighestPrice_diff(), 0); // 取最高tick
    Assert.assertEquals(6184, kline_5s.getOpenPrice_diff(), 0); // 取第一条tick
    Assert.assertEquals(6183.2, kline_5s.getLowestPrice_diff(), 0); // 取最低tick
    Assert.assertEquals(6183.2, kline_5s.getClosePrice_diff(), 0); // 取最后tick
  }

  @Test // 不完整的k线， 从10:22:46只有一个tick数据，但时间会并入10:22:45周期
  public void mergeToKLinTest_5s_cycle() throws InterruptedException {
    // 从文件构造tick测试数据
    Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, 4334, 5);
    // 设置要合并的周期
    Map<String, CycleRange> cycles = Maps.newHashMap();
    int[] cycelList = {CYCEL_SECOND_FIVE}; // 5s
    cycles.put(CYCEL_LEVEL_SECONDS, new CycleRange(cycelList));
    runner.setCycles(cycles);
    // 开始合并线程
    runner.startMergeStoreTasks();
    // 合并5s的tick
    for(KData kdata : kdatas){
      runner.publishToMergeThread(kdata);
    }

    Thread.sleep(3*1000 ); // 等合并完成
    MergeAndStore result = runner.getMergeAndStores().get(0);
    Map<String, KData> mergedKlineSet = result.getMergedKlineSet();
    // 获取周期对应处理线程的结果
    KData kline_5s = mergedKlineSet.get("IC05_5");
    // 缺少10:22:45的tick数据，直接从10:22:46开始的，但是要归到10:22:45周期的k线中取
    Assert.assertEquals(1617243765, kline_5s.getUtime(false));
    Assert.assertEquals(6202.2, kline_5s.getHighestPrice_diff(), 0);
    Assert.assertEquals(6202.2, kline_5s.getOpenPrice_diff(), 0);
    Assert.assertEquals(6202.2, kline_5s.getLowestPrice_diff(), 0);
    Assert.assertEquals(6202.2, kline_5s.getClosePrice_diff(), 0);
  }

  @Test
  public void normtimeTest() throws ParseException, CloneNotSupportedException {
    KData kdata = new CKData();
    kdata.setActionDay("20210507");
    kdata.setUtime(1620354567200L); // 2021-05-07 10:29:27
    KData secondKline = normtime(10, kdata);
    KData hourKline = normtime(60 * 60, kdata);
    KData dayKline = normtime(8 * 60 * 60, kdata);

    Assert.assertEquals(1620354560, secondKline.getUtime(false)); // 2021-05-07 10:29:20
    Assert.assertEquals(1620351000, hourKline.getUtime(false)); // 2021-05-07 09:30:00
    Assert.assertEquals(1620316800, dayKline.getUtime(false)); // 2021-05-07 00:00:00
  }
  @Test
  public void mergeKlineTest_5s() throws InterruptedException {
    {
      // 从文件构造tick测试数据，先测试一上午的数据
      Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, 6855);
      // 设置要合并的周期
      Map<String, CycleRange> cycles = Maps.newHashMap();
      int[] cycelList = {CYCEL_SECOND_FIVE}; // 5s
      cycles.put(CYCEL_LEVEL_SECONDS, new CycleRange(cycelList));
      runner.setCycles(cycles);
      // 开始合并线程
      runner.startMergeStoreTasks();
      // 开始合并
      for(KData kdata : kdatas){
        runner.publishToMergeThread(kdata);
      }

      while(!allTaskDone(runner.getMergeAndStores())){
        Thread.sleep(1 * 1000);// 等合并完成
        System.out.println("waiting for mergeAndStore...");
      }
      HbaseClient hbaseClient = runner.getHbaseClient();
      Map<String, KData> klines = ((MockHbaseClient) hbaseClient).getDatas();
      Assert.assertEquals(1091, klines.size()); // 09:30:00  - 11:00:50
    }
  }

  @Test // 获取指定时间范围内的时间
  public void closedAndGetTest() throws InterruptedException {
    long timeStamp = System.currentTimeMillis();
    Calendar endCalendar = Calendar.getInstance();
    endCalendar.setTime(new Date(timeStamp));

    Map<Calendar, Boolean>  points = Maps.newHashMap();
    points.put(endCalendar, false);

    Calendar calendar = runner.closedAndGet(points, 2);
    Assert.assertEquals(timeStamp, calendar.getTimeInMillis());

    calendar = runner.closedAndGet(points, 6);
    Assert.assertEquals(timeStamp, calendar.getTimeInMillis());

    Thread.sleep(1500); // 1.5s
    calendar = runner.closedAndGet(points, 1);
    Assert.assertEquals(null, calendar);
  }

  @Test // 集成性测试, 重要！
  public void mergeAndPackTest() throws InterruptedException {
    {
      // 从文件构造tick测试数据
      Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, Integer.MAX_VALUE);
      Assert.assertEquals(15006, kdatas.size());
      // 保存tick线程
      runner.startStoreTickTask();
      // 开始合并线程
      runner.startMergeStoreTasks();
      // 开始合并
      for(KData kdata : kdatas){
        runner.publishToMergeThread(kdata);
      }

      while(!allTaskDone(runner.getMergeAndStores())){
        Thread.sleep(1 * 1000);// 等合并完成
        System.out.println("waiting for mergeAndStore...");
      }
      // 补一下k线
      runner.beforeExit();

      HbaseClient hbaseClient = runner.getHbaseClient();
      Map<String, KData> klines = ((MockHbaseClient) hbaseClient).getDatas();

      Assert.assertEquals(14400, statisticalKline(klines, "hznc_mkdata:1sdata"));
      Assert.assertEquals(7200, statisticalKline(klines, "hznc_mkdata:2sdata"));
      Assert.assertEquals(4800, statisticalKline(klines, "hznc_mkdata:3sdata"));
      Assert.assertEquals(2880, statisticalKline(klines, "hznc_mkdata:5sdata"));
      Assert.assertEquals(960, statisticalKline(klines, "hznc_mkdata:15sdata"));
      Assert.assertEquals(480, statisticalKline(klines, "hznc_mkdata:30sdata"));
      Assert.assertEquals(240, statisticalKline(klines, "hznc_mkdata:1mindata"));
      Assert.assertEquals(48, statisticalKline(klines, "hznc_mkdata:5mindata"));
      Assert.assertEquals(16, statisticalKline(klines, "hznc_mkdata:15mindata"));
      Assert.assertEquals(8, statisticalKline(klines, "hznc_mkdata:30mindata"));
      Assert.assertEquals(4, statisticalKline(klines, "hznc_mkdata:60mindata"));
      Assert.assertEquals(1, statisticalKline(klines, "hznc_mkdata:ddata"));
    }
  }

  // 判断任务是否都完成了
  private boolean allTaskDone(List<MergeAndStore> mergeAndStores) {
    boolean isDone = true;
    for(MergeAndStore mergeAndStore : mergeAndStores){
      if(!mergeAndStore.isWait()){
        isDone = false;
        break;
      }
    }
    return isDone;
  }

  private int statisticalKline(Map<String, KData> datas, String preFix){
    int count = 0;
    Set<Entry<String, KData>> entries = datas.entrySet();
    for(Entry entry : entries){
      if(entry.getKey().toString().startsWith(preFix)){
        count ++;
      }
    }
    return count;
  }
}
