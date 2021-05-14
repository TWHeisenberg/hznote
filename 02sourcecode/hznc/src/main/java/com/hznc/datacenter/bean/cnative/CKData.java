package com.hznc.datacenter.bean.cnative;

import com.hznc.datacenter.bean.KData;
import java.util.Arrays;

/**
 * 封装对应c++结构体的属性，父类中属性是java bean的属性
 * 无奈原本没有定好规范，c++跟java的属性不一致，只能这样兼容两者的属性
 * 作为接口参数传递
 * *********************************************
 * // K线数据
 * typedef  struct KLine {
 * int Msec;           // 毫秒
 * int64 Amount;       // 成交量
 * int64 PreAmount;    // 前Tick总成交量
 * int64 Timestamp;    // 时间戳
 * double Volume;      // 成交金额
 * double PreVolume;   // 前Tick总成交金额
 * double OpenPrice;   // 开盘价
 * double HighPrice;   // 最高价
 * double LowPrice;    // 最低价
 * double ClosePrice;  //  收盘价
 * double PreClosePrice;// 前周期收盘价
 * double SettlePrice; // 结算价
 * double Inc;         // 涨幅[%]
 * double Imp;         // 振幅[%]
 * char dt[20]; }
 * KLine;
 * *********************************************
 *
 * @author TWang
 * @date 2021-04-19 16:30:00
 */
public class CKData extends KData {
  public int Msec;
  public long Amount;
  public long PreAmount;
  public long TimeStamp;
  public double Volume;
  public double PreVolume;
  public double OpenPrice;
  public double HighPrice;
  public double LowPrice;
  public double ClosePrice;
  public double PreClosePrice;
  public double SettlePrice;
  public double Inc;
  public double Imp;
  public byte[] Date = new byte[20];

  // 下面属性是结构体中漏掉的，这里加一下
  public double OpenInterest; // 持仓量，最新
  public double OpenInterest_diff; // 持仓量，周期内累加
  public long Volume_Sell; // 成交量， 最新
  public double LatestPrice;
  public double YtClosePrice; // 昨日收盘价
  public double AvgPrice;// 当日的均价


  // 从KData 装配一个CKData用来传参
  public static CKData extendCKData(KData kdata) throws CloneNotSupportedException {
    CKData cKData;
    if(kdata == null){
      return new CKData();
    }

    cKData = (CKData) kdata.clone();
    cKData.Msec = kdata.getMillisec();
    cKData.Amount = kdata.getVolume_diff(); // TODO: kdata的注释是成交量？
    cKData.TimeStamp = kdata.getUtime(false);
    cKData.Volume = kdata.getTurnover_diff(); // TODO: 当前成交金额 还是累计成交金额?
    cKData.OpenPrice = kdata.getOpenPrice_diff();
    cKData.HighPrice = kdata.getHighestPrice_diff();
    cKData.LowPrice = kdata.getLowestPrice_diff();
    cKData.ClosePrice = kdata.getClosePrice_diff();// 当前的收盘价
    cKData.YtClosePrice = kdata.getYtClosePrice();// 昨日收盘价
    cKData.SettlePrice = kdata.getSettlementPrice();

    cKData.Date = Arrays.copyOf(kdata.getTradingDay().getBytes(), 20);
    cKData.LatestPrice = kdata.getLastPrice();
    // TODO(TWang): 这几个字段不确定的
    cKData.PreVolume = kdata.getTurnover(); // 前tick累积的成交额
    // cKData.PreAmount = kdata.getVolume(); // 前tick总成交量??
    /*cKData.Inc = 0;
    cKData.Imp = 0;*/

    // 后来增加的
    cKData.OpenInterest_diff = kdata.getOpenInterest_diff(); // 持仓量， 周期相假
    cKData.OpenInterest = kdata.getOpenInterest();  // 持仓量， 最新
    cKData.Volume_Sell = kdata.getVolume();
    cKData.AvgPrice = kdata.getAveragePrice();
    return cKData;
  }

  // 将kbuff 的信息更新到kLine中
  public static KData updateKline(KData updateKline, CKData kBuff)
      throws CloneNotSupportedException {
    KData newKline = updateKline.clone();
    newKline.setMillisec(kBuff.Msec);
    newKline.setVolume_diff(kBuff.Amount);
    newKline.setUtime(kBuff.TimeStamp * 1000 + kBuff.Msec); // 转换成毫秒级
    newKline.setTurnover_diff(kBuff.Volume); // volume属性， 在kdata中表示数量（当日成交量）， c++结构体中表示成交金额
    newKline.setOpenPrice_diff(kBuff.OpenPrice);
    newKline.setHighestPrice_diff(kBuff.HighPrice);
    newKline.setLowestPrice_diff(kBuff.LowPrice);
    newKline.setClosePrice_diff(kBuff.ClosePrice);
    newKline.setYtClosePrice(kBuff.YtClosePrice); // 昨日收盘价
    newKline.setSettlementPrice(kBuff.SettlePrice);
    // TODO(TWang): 这几个字段不确定的
    newKline.setTurnover(kBuff.PreVolume); // 前Tick总成交金额
    // newKline.setVolume(kBuff.PreAmount);
   /* kBuff.Inc = 0;
    kBuff.Imp = 0;*/

    // 后来增加的
    newKline.setOpenInterest(kBuff.OpenInterest);
    newKline.setOpenInterest_diff(kBuff.OpenInterest_diff);
    newKline.setVolume(kBuff.Volume_Sell);
    newKline.setAveragePrice(kBuff.AvgPrice);
    return newKline;
  }
}
