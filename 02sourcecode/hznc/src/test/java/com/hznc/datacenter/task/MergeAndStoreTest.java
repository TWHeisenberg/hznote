package com.hznc.datacenter.task;

import static com.hznc.datacenter.constants.Constants.BUSINESS_T1500;
import static com.hznc.datacenter.constants.Constants.CYCEL_HOUR_ONE;
import static com.hznc.datacenter.constants.Constants.CYCEL_MINUTE_THIRTY;

import com.google.common.collect.Lists;
import com.hznc.datacenter.bean.CycleRange;
import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.datasource.SPIFLocalFileDataSource;
import com.hznc.datacenter.hbase.HbaseClient;
import com.hznc.datacenter.hbase.MockHbaseClient;
import com.hznc.datacenter.hbase.MyHbaseClient;
import com.hznc.datacenter.run.SPIFRealtimeDataToHbase;
import com.hznc.datacenter.run.SPIFRealtimeDataToHbaseTest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MergeAndStoreTest {

  private MergeAndStore mergeAndStore;
  private SPIFLocalFileDataSource fileDataSource;
  private SPIFRealtimeDataToHbase realTimeRunner;
  private File IC05TestFile;

  @Before
  public void setUp() throws Exception {
    HbaseClient hbaseClient = new MockHbaseClient();
    int[] range = {}; // useless
    mergeAndStore = new MergeAndStore(hbaseClient, new CycleRange(range), new CountDownLatch(1));
    fileDataSource = new SPIFLocalFileDataSource();
    IC05TestFile = new File(SPIFRealtimeDataToHbaseTest.class.getResource("/test-data/SPIF/fullDay/IC2105.txt")
        .getFile());

    realTimeRunner = new SPIFRealtimeDataToHbase();
    String configFile = MergeAndStoreTest.class.getResource("/config.properties")
        .getFile();
    realTimeRunner.init(configFile);
  }

  @Test
  public void dateTime2tsTest(){
    Calendar calendar = Calendar.getInstance();
    calendar.set(2021, 4 - 1, 27, 14, 59, 59);
    long ts = mergeAndStore.dateTime2ts(calendar);
    Assert.assertEquals(1619506799, ts);
  }

  @Test
  public void mergeAndStoreKlineTest_1s() throws Exception {
    Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, 2);

    int cycle = 1;
    for(KData kdata : kdatas){
      mergeAndStore.mergeAndStoreKline(cycle, kdata);

    }
  }

  @Test
  public void mergeAndStoreTest_1s_09uVolume() throws Exception {
    int cycle = 1;
    List<KData> datas = Lists.newArrayList();
    datas.add(readLocalTick("/test-data/SPIF/tick/IC05_1620365491_0"));
    datas.add(readLocalTick("/test-data/SPIF/tick/IC05_1620365491_1"));

    for(KData kdata : datas){
      mergeAndStore.mergeAndStoreKline(cycle, kdata);
    }
    Map<String, KData> mergedKlineSet = mergeAndStore.getMergedKlineSet();
    KData kData = mergedKlineSet.get("IC05_1");
    Assert.assertEquals(10L, kData.getVolume_diff());
    Assert.assertEquals(66129.0, kData.getOpenInterest(), 0);
    Assert.assertEquals(5.0, kData.getOpenInterest_diff(), 0);
  }

  @Test // 测一下几个主要的价格属性
  public void mergeAndStoreTest_1s_Price() throws Exception {
    int cycle = 1;
    List<KData> datas = Lists.newArrayList();
    datas.add(readLocalTick("/test-data/SPIF/tick/IC05_1620365491_0"));
    datas.add(readLocalTick("/test-data/SPIF/tick/IC05_1620365491_1"));

    for(KData kdata : datas){
      mergeAndStore.mergeAndStoreKline(cycle, kdata);
    }

    Map<String, KData> mergedKlineSet = mergeAndStore.getMergedKlineSet();
    KData kdata = mergedKlineSet.get("IC05_1");

    Assert.assertEquals(6477.2, kdata.getHighestPrice_diff(), 0);// 03dbHeightPrice
    Assert.assertEquals(66129.0, kdata.getOpenInterest(), 0); // 11zpos：持仓量， 取最新的
    Assert.assertEquals(6477.0, kdata.getLowestPrice_diff(), 0);// 03dbHeightPrice
    Assert.assertEquals(6477.2, kdata.getClosePrice_diff(), 0);// 02dbClosePrice
    Assert.assertEquals(5, kdata.getOpenInterest_diff(), 0); // 12zpos_diff, 周期内tick相加的
    Assert.assertEquals(10, kdata.getVolume_diff());// 09uVolume: 周期内相加的
    Assert.assertEquals(6446.2, kdata.getYtClosePrice(), 0);
  }

  @Test
  public void mergeAndStoreTest_1s_II() throws Exception {
    int cycle = 1;
    List<KData> datas = Lists.newArrayList();
    datas.add(readLocalTick("/test-data/SPIF/tick/IC05_1620624910_0"));
    datas.add(readLocalTick("/test-data/SPIF/tick/IC05_1620624910_1"));

    for(KData kdata : datas){
      mergeAndStore.mergeAndStoreKline(cycle, kdata);
    }
    Map<String, KData> mergedKlineSet = mergeAndStore.getMergedKlineSet();
    KData kdata = mergedKlineSet.get("IC05_1");

    Assert.assertEquals(6432.2, kdata.getHighestPrice_diff(), 0);// 03dbHeightPrice
    Assert.assertEquals(6432.0, kdata.getOpenPrice_diff(), 0);// 05dbOpenPrice
    Assert.assertEquals(6432.0, kdata.getLowestPrice_diff(), 0);// 04dbLowPrice
    Assert.assertEquals(6432.2, kdata.getClosePrice_diff(), 0);// 02dbClosePrice
    Assert.assertEquals(8, kdata.getVolume_diff());// 09uVolume: 周期内相加的
    Assert.assertEquals(65542.0, kdata.getOpenInterest(), 0); // 11zpos：持仓量， 取最新的
    // 新增几个属性检查
    Assert.assertEquals(6430.2, kdata.getYtClosePrice(), 0);
    Assert.assertEquals(40201, kdata.getVolume(), 0);
    Assert.assertEquals(4.0, kdata.getOpenInterest_diff(), 0);
  }

  @Test
  public void mergeAndStoreKlineTest_5s() {

  }

  @Test
  public void mergeAndStoreKlineTest_1min(){

  }

  @Test
  public void mergeAndStoreKlineTest_5min(){

  }

  @Test
  public void mergeAndStoreKlineTest_30min() throws Exception {
    // 从文件构造tick测试数据
    Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, 2651); // 以下周期的第一个周期为k线开始
    // 设置要合并的周期
    int cycle = CYCEL_MINUTE_THIRTY;
    for(KData kdata : kdatas){
      kdata.setLastPrice(kdata.getOpenPrice_diff()); // 就是当前开盘价
      mergeAndStore.mergeAndStoreKline(cycle, kdata); // 要变成当日的
    }
    Map<String, KData> mergedKlineSet = mergeAndStore.getMergedKlineSet();
    KData kline_30min = mergedKlineSet.get("IC05_1800");
    Assert.assertEquals("IC05_1617240600", kline_30min.getRowKey(false));
    Assert.assertEquals(6193.4, kline_30min.getHighestPrice_diff(), 0);
    Assert.assertEquals(6182.8, kline_30min.getOpenPrice_diff(), 0);
    Assert.assertEquals(6155.0, kline_30min.getLowestPrice_diff(), 0);
    Assert.assertEquals(6178.6, kline_30min.getClosePrice_diff(), 0);
  }

  @Test
  public void mergeAndStoreKlineTest_60min() throws Exception {
    // 从文件构造tick测试数据
    Queue<KData> kdatas = fileDataSource.getDataFromText(IC05TestFile, 2651); // 以下周期的第一个周期为k线开始
    // 设置要合并的周期
    int cycle = CYCEL_HOUR_ONE;
    for(KData kdata : kdatas){
      kdata.setLastPrice(kdata.getOpenPrice_diff()); // 就是当前开盘价
      mergeAndStore.mergeAndStoreKline(cycle, kdata); // 要变成当日的
    }
    Map<String, KData> mergedKlineSet = mergeAndStore.getMergedKlineSet();
    KData kline_30min = mergedKlineSet.get("IC05_3600");
    Assert.assertEquals("IC05_1617240600", kline_30min.getRowKey(false));
    Assert.assertEquals(6193.4, kline_30min.getHighestPrice_diff(), 0);
    Assert.assertEquals(6182.8, kline_30min.getOpenPrice_diff(), 0);
    Assert.assertEquals(6155.0, kline_30min.getLowestPrice_diff(), 0);
    // TODO: Assert.assertEquals(6192.2, kline_30min.getClosePrice_diff(), 0); // 9:30 - 10:30之前为一小时
  }

  @Test
  public void mergeAndStoreKlineTest_60min_2() {
    // TODO
  }

  @Test
  public void mergeAndStoreKlineTest_1d(){

  }

  @Test // 测试合并后各个周期的k线条数都对的
  public void mergeAndStoreKlineTest_DataVolume(){

  }


  @Test // 收盘补k线
  public void packingKlineAndStore_test()
      throws Exception {
    // TODO: 15:00的k线也并入前一个周期的，如1s并入14:59周期的

  }

  @Test
  public void mergeKline_test_13avgPrice() throws Exception {
    int cycle = 1;
    List<KData> datas = Lists.newArrayList();
    datas.add(readLocalTick("/test-data/SPIF/tick/IH06_1620802797_0"));
    datas.add(readLocalTick("/test-data/SPIF/tick/IH06_1620802797_1"));

    for(KData kdata : datas){
      kdata.setLastPrice(kdata.getOpenPrice_diff()); // 就是当前开盘价
      mergeAndStore.mergeAndStoreKline(cycle, kdata); // 要变成当日的
    }

    Map<String, KData> mergedKlineSet = mergeAndStore.getMergedKlineSet();
    KData kline_1s = mergedKlineSet.get("IH06_1");
    Assert.assertEquals(3417.8, kline_1s.getAveragePrice(), 0);
  }


  @Test
  public void thatDayTimeStamp(){
    KData kline = new KData();
    kline.setUtime(1617260399000L); // 2021-4-1 14:59:59
    long endts = mergeAndStore.thatDayTimeStamp(BUSINESS_T1500, kline);
    Assert.assertEquals(1617260400L, endts); // 2021-4-1 15:00:00
  }

  private KData readLocalTick(String path) throws IOException, ClassNotFoundException {
    String file = MergeAndStoreTest.class.getResource(path).getFile();
    KData kdata;
    try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
      kdata = (KData)in.readObject();
    }
    return kdata;
  }

  private void writeLocalTick(KData kdata, String dstFile){
    try(ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(dstFile))){
      oout.writeObject(kdata);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // @Test
  public void writeToLocal() throws IOException {
    // 要把测试的配置文件中test改为false
    // IC05_1620365491_0, IC05_1620624910_0
    String rowkey = "IH06_1620802797_1";
    String tableName = "hznc_mkdata:tickdata";
    String dir = "/test-data/SPIF/tick";
    dir = MergeAndStoreTest.class.getResource(dir).getFile();
    String filePath = dir + File.separator + rowkey;

    // 从hbase读取对象
    MyHbaseClient hbaseClient = (MyHbaseClient)realTimeRunner.getHbaseClient();
    KData tick = hbaseClient.getKdataByRowkey(tableName, rowkey);

    tick.setActionDay("20210510");
    tick.setTradingDay("20210510");
    tick.setLastPrice(tick.getOpenPrice_diff());
    writeLocalTick(tick, filePath);
  }
}
