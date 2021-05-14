package com.hznc.datacenter.datasource;

import com.hznc.datacenter.bean.KData;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 数据源
 * @author TWang
 * @date 2021-05-08
 */
public abstract class DataSource {

  // 接收tickdata的队列
  protected static Queue<KData> tickDataQueue = new ConcurrentLinkedQueue();

  public static Queue<KData> getTickDataQueue() {
    return tickDataQueue;
  }

  // 开始接收数据
  public abstract  void startReceiveTickdata(Properties prop);
}
