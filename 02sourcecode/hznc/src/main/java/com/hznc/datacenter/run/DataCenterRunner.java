package com.hznc.datacenter.run;

public abstract class DataCenterRunner {

  private static final String REALTIME_SPIF_HBASE = "realtime_spif_hbase"; // 实时数据到hbase,类型股指期货数据
  private static final String HISTORY_SPIF_HBASE = "HISTORY_spif_hbase"; // 历史数据到hbase,类型股指期货数据

  public abstract void run(String... args) throws Exception;

  // 根据不同指令返回对应运行类
  public static DataCenterRunner getRunner(String cmd){
    DataCenterRunner runner = null;
    if(cmd == null){
      return runner;
    }
    switch (cmd){
      case (REALTIME_SPIF_HBASE):
        runner = new SPIFRealtimeDataToHbase();
        break;
      case (HISTORY_SPIF_HBASE):
        runner = new SPIFHistoryDataToHbase();
        break;
    }

    return runner;
  }
}
