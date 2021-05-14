package com.hznc.datacenter.datasource;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.bean.cnative.CKData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;

/**
 * 读取本地文件接口获取历史股指期权数据
 *
 * @author TWang
 * @date 2021-04-19 10:30:00
 */
// 实现线程是为了可以等待异步的回调
public class SPIFLocalFileDataSource extends DataSource {

  private static Logger logger = Logger.getLogger(SPIFLocalFileDataSource.class);

  private Map<String, KData> lastRecord = Maps.newHashMap();

  public Queue<KData> getDataFromText(File file, int start, int limit){
    {
      Queue<KData> dataList = new ConcurrentLinkedQueue();
      int offset = 0;
      try (FileReader fr = new FileReader(file);
          BufferedReader br = new BufferedReader((fr))) {
        String line;
        boolean skipedHead = false;
        while ((line = br.readLine()) != null) {

          offset++;
          if(!skipedHead){
            skipedHead = true;
            continue; // 跳过第一行的头信息
          }

          if((start != -1) && offset < start){
            continue;
          }

          // 返回指定多少行
          if(limit > 0 && dataList.size() >= limit){
            break;
          }

          KData kdata = parseKdataFromLine(line);
          dataList.add(kdata);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return dataList;
    }
  }

  public Queue<KData> getDataFromText(File file, int limitLine) {
    return getDataFromText(file, -1, limitLine);
  }

  private KData parseKdataFromLine(String line) throws ParseException {
    String[] splits = line.split(",");
    DateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss.SSS");
    String date = splits[1], time = splits[2];
    String dateTime = date + time;
    long uTime = sdf.parse(dateTime).getTime();

    KData kdata = new CKData();

    kdata.setSymbol(splits[0].replace("21", "")); // IC2105 > IC05
    kdata.setUtime(uTime);              //时间
    KData lastData = lastRecord.get(kdata.getSymbol());
    if(lastData == null){
      lastData = new CKData(); // 如果为空就初始化一个为0的
    }

    kdata.setVolume(getLongSafely(splits[8]));            // 成交量，最新
    // 计算累积的成交量
    if (lastData.getUtime(true) == 0) {
      kdata.setVolume_diff(0);
    } else {
      kdata.setVolume_diff(kdata.getVolume() - lastData.getVolume());
    }

    double accumulateTurnover = getDoubleSafely(splits[9]);
    kdata.setTurnover(accumulateTurnover); // 累积成交额
    kdata.setTurnover_diff(accumulateTurnover - lastData.getTurnover());      //当前成交额

    kdata.setTradingDay(splits[1]);        //交易日期
    kdata.setInstrumentID(splits[0]);      //合约代码
    kdata.setLastPrice(getDoubleSafely(splits[3]));  //最新价

    kdata.setOpenPrice_diff(kdata.getLastPrice());      // 当前开盘价 (最新价)
    kdata.setClosePrice_diff(kdata.getLastPrice());      // 当前收盘价（最新价）
    kdata.setYtClosePrice(getDoubleSafely(splits[15]));    // 昨日收盘价
    kdata.setPreOpenInterest(getDoubleSafely(splits[16]));  // 昨日持仓量
    kdata.setOpenPrice(getDoubleSafely(splits[13]));        // 当日开盘价

    // 最新价，开盘价取最高
    kdata.setHighestPrice(Math.max(kdata.getHighestPrice_diff(),lastData.getHighestPrice_diff()));      // 当日最高价 好像就是当前价格
    kdata.setHighestPrice_diff(kdata.getLastPrice()); // 取最新价格

    kdata.setLowestPrice(Math.min(kdata.getLowestPrice_diff(),lastData.getLowestPrice_diff()));      // 当日最低价
    kdata.setLowestPrice_diff(kdata.getLastPrice());    // 取最新价格

    kdata.setOpenInterest(getDoubleSafely(splits[10]));      // 持仓量, 最新


    kdata.setClosePrice(kdata.getLastPrice());        // 今收盘
    kdata.setAveragePrice(getDoubleSafely(splits[3]));          //当日均价
    kdata.setActionDay(splits[1]);          //业务日期
    kdata.setMillisec(Integer.parseInt(time.substring(time.lastIndexOf(".") +1)));      //最后修改毫秒
    kdata.setUpperLimitPrice(getDoubleSafely(splits[11]));  //涨停板价
    kdata.setLowerLimitPrice(getDoubleSafely(splits[12]));  //跌停板价
    kdata.setBidPrice1(getDoubleSafely(splits[4]));        //申买价一
    kdata.setBidVolume1(Integer.parseInt(splits[5]));        //申买量一
    kdata.setAskPrice1(getDoubleSafely(splits[6]));        //申卖价一
    kdata.setAskVolume1(Integer.parseInt(splits[7]));          //申卖量一

    // 计算持仓量
    String symbol = kdata.getSymbol();
    if (lastData.getUtime(true) == 0) {
      kdata.setOpenInterest_diff(0); // 如果是第一根tick,这里应该0
    } else {
      kdata.setOpenInterest_diff(kdata.getOpenInterest() - lastData.getOpenInterest());
    }

    lastRecord.put(symbol, kdata);
    return kdata;
  }

  public static void getFile(String path , List<File> files ){
    File file = new File(path);
    if(file.isDirectory()){
      File[] filef = file.listFiles();
      for(File fileIndex:filef){
        if(fileIndex.isDirectory()){
          getFile(fileIndex.getPath() , files);
        }else{
          files.add(fileIndex);
        }
      }

    }
  }

  private double getDoubleSafely(String text){
    if(Strings.isNullOrEmpty(text)){
      return 0;
    }
    return Double.parseDouble(text);
  }

  private long getLongSafely(String text){
    if(Strings.isNullOrEmpty(text)){
      return 0;
    }
    return new BigDecimal(text).setScale(2).longValue();
  }

  @Override
  public void startReceiveTickdata(Properties prop) {
    String inputDir = prop.getProperty("historyInputDir");
    List<File> files = Lists.newArrayList();
    // 扫描历史文件
    getFile(inputDir, files);

    for(File file : files){
      Queue<KData> kdatas = getDataFromText(file, -1);
      tickDataQueue.addAll(kdatas);
    }
  }
}
