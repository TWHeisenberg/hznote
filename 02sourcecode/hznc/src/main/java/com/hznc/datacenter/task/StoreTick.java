package com.hznc.datacenter.task;

import static com.hznc.datacenter.constants.Constants.BUSINESS_T1500;
import static com.hznc.datacenter.constants.Constants.TABLE_TICK;

import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.hbase.HbaseClient;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

/**
 * 单独线程来存储tick
 *
 * @author TWang
 * @date 2021-04-30 18:00:00
 */
public class StoreTick implements Runnable {

  private static Logger logger = Logger.getLogger(StoreTick.class);

  private HbaseClient hbaseClient;
  // 缓存，接收tick数据
  private LinkedBlockingQueue<KData> dataQueue;
  // 统计插入的tick数量
  private static AtomicLong insertedTickCount = new AtomicLong(0);

  // 是否到程序退出的时间
  private volatile boolean timeToExit = false;
  // 接收收盘的时间
  private volatile Calendar closeTime;
  // 向主线程汇报状态
  private CountDownLatch finishReporter;

  public StoreTick(HbaseClient client, CountDownLatch finishReporter) {
    this.dataQueue = new LinkedBlockingQueue();
    this.hbaseClient = client;
    this.finishReporter = finishReporter;
  }

  public boolean add(KData kdata) {
    return dataQueue.add(kdata);
  }

  boolean exit = false;
  public void run() {
    while (!exit) {
      try {
        // 到达收盘时间，持久化内存中的数据
        if(closeTime != null){
          hbaseClient.clearRemainingPuts();
          closeTime = null;
          logger.info("after close: store tick data ok.");
        }

        // 到达程序退出时间
        if (timeToExit) {
          hbaseClient.clearRemainingPuts();
          logger.info("before exit: store tick data ok.");
          exit = true;
        }

        // 没有数据就等
        KData tickData = dataQueue.poll(10, TimeUnit.SECONDS);
        if (tickData == null) {
          logger.info("no tick data.");
          continue;
        }

        // 收盘后的tick过滤掉
        if(tickData.getUtime(false) > BUSINESS_T1500.getTimeInMillis()/1000){
          logger.info("filter tick, closed:" + tickData.getUtime(true));
          continue;
        }

        // 批量保存
        hbaseClient.batchPut(TABLE_TICK, tickData, true);

        String rowkey = tickData.getRowKey(true);
        if (rowkey.startsWith("IC05")) { // 日志太多了，这里只记录ic05的
          logger.info("store tick: "+rowkey+" ok, count: " + insertedTickCount.incrementAndGet());
        }
      } catch (Exception e) {
        logger.error(e);
        exit = true;
      }
    }
    // 向主线程汇报退出
    finishReporter.countDown();
    logger.info("Thread exit.");
  }

  public AtomicLong getInsertedTickCount() {
    return this.insertedTickCount;
  }

  public void setTimeToExit(boolean timeToExit) {
    this.timeToExit = timeToExit;
  }

  public void setCloseTime(Calendar closeTime) {
    this.closeTime = closeTime;
  }
}
