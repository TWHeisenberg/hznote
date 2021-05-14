package com.hznc.datacenter.hbase;

import com.google.common.collect.Maps;
import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.hbase.HbaseClient;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

/**
 * 伪造一个假的客户端，测试用
 */
public class MockHbaseClient implements HbaseClient {
  private static Logger logger = Logger.getLogger(MockHbaseClient.class);

  public MockHbaseClient(){
    super();
    logger.info("It's a Fake hbase client for testing.");
  }

  private Map<String, KData> datas = Maps.newConcurrentMap();


  @Override
  public void singlePut(String tablename, KData value, boolean isTick){
    put(tablename, value, isTick);
  }

  @Override
  public void clearRemainingPuts() throws IOException {
    // do nothing
    logger.info("clearRemainingPuts...");
  }

  @Override
  public void batchPut(String tablename, KData value, boolean isTick){
    put(tablename, value, isTick);
  }

  public void put(String tablename, KData value, boolean isTick){
    // 保存数据，测试
    datas.put(tablename+"_"+value.getRowKey(isTick), value);
    // test, do nothing
    logger.debug(String.format("!!!test: put %s to %s ok.", value.getRowKey(isTick), tablename));
  }

  public Map<String, KData> getDatas() {
    return datas;
  }
}
