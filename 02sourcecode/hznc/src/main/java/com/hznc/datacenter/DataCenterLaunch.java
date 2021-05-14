package com.hznc.datacenter;

import com.hznc.datacenter.run.DataCenterRunner;
import org.apache.log4j.Logger;

/**
 * 启动类,根据不同的指令运行对应程序处理数据
 * @author TWang
 * @date 2021-04-28 16:00:00
 */
public class DataCenterLaunch {
  private static Logger logger = Logger.getLogger(DataCenterLaunch.class);

  public static void main(String[] args) throws Exception {
    if(args == null || args.length < 2){
      String errorMsg = "args must contain cmd and config-file.";
      throw new IllegalArgumentException(errorMsg);
    }

    String cmd = args[0], configFile = args[1];
    // 启动对应的程序
    DataCenterRunner runner = DataCenterRunner.getRunner(cmd);
    if(runner == null){
      throw new IllegalArgumentException("Incorrect command: " + cmd);
    }

    logger.info("Program is starting. use config:" + configFile);
    runner.run(configFile);
  }
}
