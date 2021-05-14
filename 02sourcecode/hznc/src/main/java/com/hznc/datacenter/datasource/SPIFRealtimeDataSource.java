package com.hznc.datacenter.datasource;

import static com.hznc.datacenter.constants.Constants.DATA_TYPE_FUTURES;
import static com.hznc.datacenter.constants.Constants.VAR_I;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.bean.cnative.CKData;
import com.sun.jna.*;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 调用c++接口获取实时股指期权数据
 *
 * @author TWang
 * @date 2021-04-19 10:30:00
 */
// 实现线程是为了可以等待异步的回调
public class SPIFRealtimeDataSource extends DataSource {
  private static Logger logger = Logger.getLogger(SPIFRealtimeDataSource.class);

  // 加载动态库名称
  public static final String NATIVE_DLL = "RabbitMQcMK";
  public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  //<symbol_timestamp, count> 记录不同code的tick数据出现次数，用来判断是一秒钟的第几条
  private static Map<String, Integer> recordRowkey = Maps.newHashMap();
  // 保存上一条的数据，用来计算差值
  static Map<String, KData> lastdata = new HashMap();



  // 用来统计接收，过滤和插入的数据量
  public static AtomicLong receivedCount = new AtomicLong(0);
  public static AtomicLong filtdeCount = new AtomicLong(0);

  // 调用c++接口获取数据，再回调函数中接收：receiveCallback
  @Override
  public void startReceiveTickdata(Properties prop) {
    String mqConsumerIp = prop.getProperty("mqConsumerIp");
    int mqConsumerPort = Integer.parseInt(prop.getProperty("mqConsumerPort", "5672"));
    String mqConsumerUser = prop.getProperty("mqConsumerUser", "admin");
    String mqConsumerPwd = prop.getProperty("mqConsumerPwd", "admin");
    // 加载mq队列信息
    String mqConsumerQueue = prop.getProperty("mqConsumerQueue");
    String mqGetInstrumentInfo = prop.getProperty("mqGetInstrumentInfo");
    notifyCallBack = new NotifyCallback();
    receiveCallback = new DataReceiveCallback();
    loginCallback = new LoginCallback();

    // 初始化mq
    Pointer ctp = Function.instance.MQInit
        (mqConsumerIp, mqConsumerPort, mqConsumerUser, mqConsumerPwd, DATA_TYPE_FUTURES);
    // 提醒回调
    Function.instance.MQSetNotify(ctp, notifyCallBack, VAR_I);
    // 设置消费队列和回调函数
    Function.instance
        .MQConsumer(ctp, receiveCallback, VAR_I, mqConsumerQueue);
    Function.instance
        .MQGetInstrumentInfo(ctp, loginCallback, VAR_I, mqGetInstrumentInfo);
  }

  // 回调函数， 必须是成员变量，生命周期随本类保证一直回调
  private NotifyCallback notifyCallBack;
  private DataReceiveCallback receiveCallback;
  private LoginCallback loginCallback;

  //调用c++接口，实现回调函数
  public interface Function extends Library {

    //加载本地动态库
    Function instance = (Function) Native.loadLibrary(NATIVE_DLL, Function.class);

    // native 方法接口
    int MQConsumer(Pointer m, DataReceiveCallback pcallback, int u, String queue);

    int MQGetInstrumentInfo(Pointer m, LoginCallback pcallback, int u, String queue);

    Pointer MQInit(String ip, int port, String user, String passwd, int i);

    void MQSetNotify(Pointer obj, NotifyCallback ntfy, int u);
  }

  // 不同方法回调实现对象
  public static class NotifyCallback implements Callback {

    public void invoke(int dtype, int ncode, String nstr, Pointer u) {
      // pass
    }
  }

  public static class LoginCallback implements Callback {

    public void invoke(int type, Pointer bytes, int len, Pointer u, Pointer m) {
      // pass
    }
  }

  // 数据解析的回调函数
  public static class DataReceiveCallback implements Callback {

    public void callback(int dtype, Pointer data, int dlen, Pointer u, int m) throws Exception {

      int count = dlen / 480;
      // 判断 如果等于3 则为分钟线
      if (dtype == 1) {

        for (int i = 0; i < count; i++) {
          long startOffset = i * 480; // 一个结构体的大小是480
          KData kdata = parseDataFromPoiner(data, startOffset, m);
          String symbol = kdata.getSymbol();

          // 只处理股指数据
          if (!symbol.startsWith("IC") && !symbol.startsWith("IF") &&
              !symbol.startsWith("IH")) {
            continue;
          }

          logger.debug(
              "Received tick,  rowKey: " + kdata.getRowKey(false) + " count: " + receivedCount
                  .incrementAndGet());

          if (filterOut(kdata)) {
            logger.info(String
                .format("Filted tickdata, rowkey: %s count: %s", kdata.getRowKey(true),
                    filtdeCount.incrementAndGet()));
            continue;
          }

          // TODO(TWang): 1秒内会不会有四条或者更多的tick数据?
          // rowKey打上标记, 0：前500ms， 1：后500ms, 2: 后800ms
          // 因为要判断是否在同1s,这里时间戳以秒级的为key
          String symbolWithUtime = kdata.getSymbol() + "_" + kdata.getUtime(false);
          int sort = sortTickWithinSecond(symbolWithUtime, recordRowkey);
          kdata.setSort(sort);

          KData tickdata = kdata.clone();
          tickDataQueue.add(tickdata); // k线数据的时间戳精确到秒（10位）
        }
      }
    }

    // 给tick数据排序，判断是1s内的第几条tick
    public static int sortTickWithinSecond(String symbolWithUtime, Map<String, Integer> recordRowkey) {
      Integer insertedCount = recordRowkey.get(symbolWithUtime);
      int sort = -1;
      if (insertedCount == null) {
        insertedCount = 0;
        sort = 0;
      } else if (insertedCount == 1) {
        sort = 1;
      } else if (insertedCount == 2) {
        sort = 2;
      }
      recordRowkey.put(symbolWithUtime, ++insertedCount);
      return sort;

    }

    // 过滤掉一些不在时间端和无效的数据
    @VisibleForTesting
    boolean filterOut(KData mdata) {
      //过滤时间为0 的无效数据
      if (mdata.getUtime(false) == 0) {
        System.out.println("时间为0");
        return true;
      }
      // TODO:只要四个时间段的数据
      // 日盘：9:00-11:30, 13:00-15:00,夜盘：21:00-23:30, 01:00-03:00
      return false;
    }
  }

  /**
   * 从c++接口返回的指针对象解析出数据
   *
   * @param data 包含数据的指针
   * @param startOffset 指针中对象的坐标
   * @return KData 期货数据
   */
  public static KData parseDataFromPoiner(Pointer data, long startOffset, int m)
      throws ParseException {

    // TODO(TWang): 变量命名不规范
    byte[] cSymbolbuf = new byte[4];
    int[] uTimebuf = new int[1];
    int[] volume_diff = new int[1];
    double[] sum_diff = new double[1];
    byte[] TradingDay = new byte[8];  //交易日    工作日
    byte[] InstrumentID = new byte[10]; //合约代码

    double[] lastPrice = new double[1];//最新价
    double[] PreSettlementPrice = new double[1]; //上次结算价
    double[] ytClosePrice = new double[1];  //昨收盘
    double[] PreOpenInterest = new double[1];  //昨日持仓量
    double[] openPrice = new double[1];  //今日开盘价
    double[] highestPrice = new double[1]; //最高价
    double[] lowestPrice = new double[1];   //最低价
    int[] Volume = new int[1];   //数量
    double[] Turnover = new double[1];   //成交金额

    int[] OpenInterest = new int[1];  //持仓量
    double[] ClosePrice = new double[1];   //今日收盘
    double[] avg = new double[1];
    byte[] ActionDay = new byte[8]; //业务日期    实际日期
    int[] UpdateMillisec = new int[1];
    double[] UpperLimitPrice = new double[1];  //涨停板价
    double[] LowerLimitPrice = new double[1];  //跌停板价
    double[] BidPrice1 = new double[1];    //申买价一
    int[] BidVolume1 = new int[1];      //申买量一
    double[] AskPrice1 = new double[1];    //申卖价一
    int[] AskVolume1 = new int[1];      //申卖量一

    data.read(0 + startOffset, cSymbolbuf, 0, 4);
    data.read(32 + startOffset, uTimebuf, 0, 1);
    data.read(36 + startOffset, volume_diff, 0, 1);
    data.read(40 + startOffset, sum_diff, 0, 1);
    data.read(48 + startOffset, TradingDay, 0, 8);
    data.read(80 + startOffset, InstrumentID, 0, 8);
    data.read(112 + startOffset, lastPrice, 0, 1);
    data.read(120 + startOffset, PreSettlementPrice, 0, 1);
    data.read(128 + startOffset, ytClosePrice, 0, 1);
    data.read(136 + startOffset, PreOpenInterest, 0, 1);
    data.read(144 + startOffset, openPrice, 0, 1);
    data.read(152 + startOffset, highestPrice, 0, 1);
    data.read(160 + startOffset, lowestPrice, 0, 1);
    data.read(168 + startOffset, Volume, 0, 1);
    data.read(176 + startOffset, Turnover, 0, 1);
    data.read(184 + startOffset, OpenInterest, 0, 1);
    data.read(208 + startOffset, UpperLimitPrice, 0, 1);
    data.read(216 + startOffset, LowerLimitPrice, 0, 1);
    data.read(192 + startOffset, ClosePrice, 0, 1);
    data.read(272 + startOffset, UpdateMillisec, 0, 1);
    data.read(280 + startOffset, BidPrice1, 0, 1);
    data.read(288 + startOffset, BidVolume1, 0, 1);
    data.read(296 + startOffset, AskPrice1, 0, 1);
    data.read(304 + startOffset, AskVolume1, 0, 1);
    data.read(440 + startOffset, avg, 0, 1);
    data.read(448 + startOffset, ActionDay, 0, 8);

    KData kdata = new CKData();
    String tradingDay = new String(TradingDay);
    String actionDay = new String(ActionDay);
    StringBuilder sbad = new StringBuilder(actionDay);
    sbad.insert(4, "-");
    sbad.insert(7, "-");
    // long timestamp = uTimebuf[0] * 1000 + UpdateMillisec[0]; // 统一精确到毫秒

    Date date = new Date(uTimebuf[0]*1000L);
    String formatDate = sbad+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    long timestamp = (df.parse(formatDate).getTime()) + UpdateMillisec[0]; // 统一精确到毫秒

    String instrumentID = new String(InstrumentID);

    String cSym;
    // 如果商品代码最后一位是0，说明这个代码只有三位，舍弃最后一位
    if (cSymbolbuf[3] == 0) {
      cSym = new String(cSymbolbuf, 0, 3);
    } else {
      cSym = new String(cSymbolbuf);
    }
    int i = 0;
    for (byte d : InstrumentID) {
      if (d == 0) {
        instrumentID = new String(InstrumentID, 0, i);
        continue;
      }
      i++;
    }

    kdata.setSymbol(cSym);              //期货代码
    kdata.setUtime(timestamp);              // 时间，统一精确到毫秒
    kdata.setVolume_diff(volume_diff[0]);      //当前成交量
    kdata.setTurnover_diff(sum_diff[0]);      //当前成交额 TODO: 确定是当前还是累积的
    kdata.setTradingDay(tradingDay);        //交易日期
    kdata.setInstrumentID(instrumentID);      //合约代码
    kdata.setLastPrice(lastPrice[0]);        //最新价
    kdata.setOpenPrice_diff(lastPrice[0]);      //当前开盘价
    kdata.setClosePrice_diff(lastPrice[0]);      //当前收盘价（最新价）
    kdata.setYtClosePrice(ytClosePrice[0]);    //昨日收盘价
    kdata.setPreOpenInterest(PreOpenInterest[0]);  //昨日持仓量
    kdata.setOpenPrice(openPrice[0]);        //当日开盘价
    kdata.setHighestPrice(highestPrice[0]);      //当日最高价
    kdata.setLowestPrice(lowestPrice[0]);      //当日最低价
    kdata.setHighestPrice_diff(lastPrice[0]);    //当前最低价
    kdata.setLowestPrice_diff(lastPrice[0]);    //当前最低价
    kdata.setVolume(Volume[0]);            //数量（当日成交量）
    kdata.setTurnover(Turnover[0]);          //成交金额（当日成交）
    kdata.setOpenInterest(OpenInterest[0]);      //今日持仓量
    kdata.setClosePrice(lastPrice[0]);        //今收盘
    kdata.setAveragePrice(avg[0]);          //当日均价
    kdata.setActionDay(actionDay);          //业务日期
    kdata.setMillisec(UpdateMillisec[0]);      //最后修改毫秒
    kdata.setUpperLimitPrice(UpperLimitPrice[0]);  //涨停板价
    kdata.setLowerLimitPrice(LowerLimitPrice[0]);  //跌停板价
    kdata.setBidPrice1(BidPrice1[0]);        //申买价一
    kdata.setBidVolume1(BidVolume1[0]);        //申买量一
    kdata.setAskPrice1(AskPrice1[0]);        //申卖价一
    kdata.setAskVolume1(AskVolume1[0]);          //申卖量一

    //计算当前持仓量
    if (!lastdata.containsKey(cSym)) {
      kdata.setOpenInterest_diff(0); // 如果是第一根tick,这里应该0
      lastdata.put(cSym, kdata);
    } else {
      kdata.setOpenInterest_diff(OpenInterest[0] - lastdata.get(cSym).getOpenInterest());
      lastdata.put(cSym, kdata);
    }
    return kdata;
  }

  public void run() {
    // do nothing
  }
}
