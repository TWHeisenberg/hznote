package com.hznc.datacenter.bean;

import com.hznc.datacenter.bean.cnative.CKData;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * 封装tickdata的结构体，作调用c++的参数:
 ***************************************
 *     typedef struct TickData {
 *         int Msec;           // 毫秒
 *         int64 Amount;       // 交易量
 *         int64 Timestamp;    // 时间戳
 *         double LatestPrice; // 最新价
 *         double SettlePrice; // 结算价
 *         double Volume;      // 交易额
 *         char date[20];
 *     } TickData;
 ***************************************
 * @author TWang
 * @date 2021-04-19 16:30
 */
public class TickDataStructBak extends Structure {
    public int Msec;
    public long Amount;
    public long TimeStamp;
    public double LatestPrice;
    public double SettlePrice;
    public double Volume;
    public byte[] date = new byte[20];

    // 结构体中原来没有，后来增加的
    public double openInterest; // 持仓量，最新
    public double openInterest_diff; // 持仓量，周期内累加
    public long volume_Sell; // 成交量，最新

  public static CKData fromTickData(KData tickData) {
    CKData cTick = new CKData();
    cTick.Amount = tickData.getVolume_diff();
    cTick.Msec = tickData.getMillisec();
    cTick.Date = Arrays.copyOf(tickData.getTradingDay().getBytes(), 20);
    cTick.LatestPrice = tickData.getLastPrice();
    cTick.SettlePrice = tickData.getSettlementPrice();
    cTick.Volume = tickData.getTurnover_diff();
    cTick.TimeStamp = tickData.getUtime(false); // 因为是用来合并成k线，所以精确到秒

    cTick.openInterest = tickData.getOpenInterest();
    cTick.openInterest_diff = tickData.getOpenInterest_diff();
    cTick.Volume_Sell = tickData.getVolume();
    return cTick;
  }

  // 作为结构体的标识
    public static class ByReference extends TickDataStructBak implements Structure.ByReference{}
    public static class ByValue extends TickDataStructBak implements Structure.ByValue{}

    @Override
    public List getFieldOrder(){
        return Arrays.asList(new String[]{"Msec", "Amount", "TimeStamp"
        , "LatestPrice", "SettlePrice", "Volume", "date"});
    }
}
