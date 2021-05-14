package com.hznc.datacenter.hbase;

import com.hznc.datacenter.bean.KData;
import java.io.IOException;

public interface HbaseClient {

  // 持久化缓存中的数据 TODO:应该指定表，防止多线程问题
  void clearRemainingPuts() throws IOException;
  // 批量插入
  void batchPut(String tablename, KData value, boolean isTick) throws IOException;
  // 单条插入
  void singlePut(String tablename, KData value, boolean isTick) throws IOException;
}
