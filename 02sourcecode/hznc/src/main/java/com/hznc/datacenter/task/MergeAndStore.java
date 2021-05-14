package com.hznc.datacenter.task;

import static com.hznc.datacenter.run.SPIFRealtimeDataToHbase.dateFormat;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.hznc.datacenter.bean.CycleRange;
import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.bean.KParamStruct;
import com.hznc.datacenter.bean.cnative.CKData;
import com.hznc.datacenter.constants.Constants;
import com.hznc.datacenter.hbase.HbaseClient;
import com.hznc.datacenter.utils.CreateKLineUtil;
import com.hznc.datacenter.utils.CreateKLineUtil.Function;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 * 合并保存k线的线程
 * @author TWang
 * @date 2021-5-13
 */
public class MergeAndStore extends Thread{
  private static Logger logger = Logger.getLogger(MergeAndStore.class);

  private HbaseClient hbaseClient;
  // <rowkey, mergedKline>
  private Map<String, KData> mergedKlineSet;
  // 合并k线的工具
  private CreateKLineUtil createKLineUtil;
  // 负责合并的周期
  private CycleRange cycleUnit;
  // 接收收盘的时间
  private volatile Calendar closeTime;
  // 是否到程序退出时间
  private volatile boolean timeToExit = false;
  // 等待状态，说明没有数据
  private volatile boolean wait = false;
  // 向主线程汇报状态
  private CountDownLatch statusReporter;


  public MergeAndStore(HbaseClient hbaseClient, CycleRange cycleUnit, CountDownLatch statusReporter){
    this.hbaseClient = hbaseClient;
    this.cycleUnit = cycleUnit;
    mergedKlineSet = Maps.newHashMap();
    createKLineUtil = new CreateKLineUtil(mergedKlineSet, hbaseClient);
    this.statusReporter = statusReporter;
  }

  boolean exit = false;
  public void run() {
    while(!exit){
      try {

        if(closeTime != null){
          // 补一下k线,天以下的周期
          packAndStore(closeTime, mergedKlineSet, false);
          closeTime = null;
          logger.info("after close: pack and store kline ok.");
        }

        if(timeToExit){
          // 补k线，包括天周期
          packAndStore(Constants.BUSINESS_T1500, mergedKlineSet, true);
          logger.info("before exit: pack and store kline ok.");
          exit = true;
        }

        KData kdata = cycleUnit.getDataset().poll(10, TimeUnit.SECONDS);
        if(kdata == null){
          wait = true;
          logger.info("no kline data.");
          continue;
        }

        wait = false;
        // 合并k线
        for(int i = 0;i<cycleUnit.getCycles().length;i++){
          KData localKData = kdata.clone(); // 防止多线程问题
          mergeAndStoreKline(cycleUnit.getCycles()[i], localKData);
        }

      } catch (Exception e) {
        logger.error("Thread error exit.", e);
        exit = true;
      }
    }
    statusReporter.countDown();
    logger.info("Thread normal exit.");
  }

  @VisibleForTesting
  void mergeAndStoreKline(int cycle, KData tickData)
      throws Exception {
    String symbolWithCycle = tickData.getSymbol() + "_" + cycle;
    CreateKLineUtil.KLineCallback callback = new CreateKLineUtil.KLineCallback();
    KData lastKline = mergedKlineSet.get(symbolWithCycle);
    // 是否夜盘，是否差值， 效交易时间点数组大小，法定假日数组大小
    int isNight = 0, isTotalVol = 0, dSize = 2, hSize = 0;

    // 封装请求native接口的结构体
    CKData cTick = CKData.extendCKData(tickData);

    CKData CKBuff;
    if(lastKline == null){
      CKBuff = (CKData) tickData;
    }else{
      CKBuff = CKData.extendCKData(lastKline);
    }

    KParamStruct kParamStruct = KParamStruct
        .newInstance(cycle, isTotalVol, isNight, dSize, hSize);
    // 调native接口合并成k线
    // 第四个参数应该是保存合并k线的数组，但为了简化接口调用,使用本类中静态变量mergedKlineSet来保存了
    // 取而代之这个参数成了从mergedKlineSet取值的key(symbolWithCycle)
    createKLineUtil.createKLine(cTick, CKBuff, kParamStruct,
        symbolWithCycle, callback, null);
  }

  @VisibleForTesting
  void packAndStore(Calendar endCalendar,
      Map<String, KData> mergedKlineSet, boolean packDayKline)
      throws Exception {
    if (mergedKlineSet.isEmpty()) {
      return;
    }

    CreateKLineUtil.KLineCallback callback = new CreateKLineUtil.KLineCallback();
    // 是否夜盘，是否差值， 效交易时间点数组大小，法定假日数组大小
    int isNight = 0, isTotalVol = 0, dSize = 2, hSize = 0;

    // 当天收盘时间戳
    Long endTs = null;
    for (String symbolWithCycle : mergedKlineSet.keySet()) {
      KData lastKline = mergedKlineSet.get(symbolWithCycle);
      if(endTs == null){
        endTs = thatDayTimeStamp(endCalendar, lastKline);
      }

      // 获取周期
      int cycle = Integer.parseInt(symbolWithCycle.split("_")[1]);
      if(!packDayKline && cycle == Constants.CYCEL_DAY_ONE){
        logger.info("skip pack day kline.");
        return;
      }
      // 封装k线算法参数
      KParamStruct kParamStruct = KParamStruct
          .newInstance(cycle, isTotalVol, isNight, dSize, hSize);
      CKData kBuff = CKData.extendCKData(lastKline);
      // 调用c++接口进行补k线
      createKLineUtil.packKLine(endTs, kBuff, kParamStruct, symbolWithCycle, callback, null);

      // 最后保存到hbase
      KData newKline = mergedKlineSet.get(symbolWithCycle);
      String table = Constants.cycleTableMap.get(cycle);
      // store
      hbaseClient.singlePut(table, newKline, false);
      logger.info("store packed kline "+ newKline.getRowKey(false) +" ok.");
    }
  }

  // 获取当天时间点的时间戳
  @VisibleForTesting
  Long thatDayTimeStamp(Calendar endCalendar, KData kline) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date(kline.getUtime(true)));
    calendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY));
    calendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE));
    calendar.set(Calendar.SECOND, endCalendar.get(Calendar.SECOND));
    return calendar.getTimeInMillis() / 1000;
  }


  @VisibleForTesting // 转时间戳，测试下native接口
  static long dateTime2ts(Calendar calendar) {
    String date = dateFormat.format(calendar.getTime());
    return Function.instance._dateTime2ts(Arrays.copyOf(date.getBytes(), 20));
  }

  public Map<String, KData> getMergedKlineSet() {
    return mergedKlineSet;
  }

  public void setTimeToExit(boolean timeToExit) {
    this.timeToExit = timeToExit;
  }

  public void setCloseTime(Calendar closeTime) {
    this.closeTime = closeTime;
  }

  public boolean isWait(){
    return this.wait;
  }
}
