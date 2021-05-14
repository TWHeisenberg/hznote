package com.hznc.datacenter.bean;

import java.io.Serializable;

/**
 * tick数据和k线数据的封装
 * @author TWang
 * @date 2021-04-21 10:00
 */
public class KData implements Cloneable, Serializable {
  protected String symbol;					//4位合约代码
  protected long utime;						//时间戳
  protected long volume_diff;				// 09uVolume 成交量，累加的
  protected double turnover_diff;			//成交金额差额（当前成交额）
  protected String tradingDay;				//交易日
  protected String instrumentID;			//合约代码
  protected double lastPrice;				//最新价(当前收盘价)
  protected double	PreSettlementPrice;		//上次结算价
  // protected double preClosePrice;			//上周期收盘价
  protected double ytClosePrice; // 昨日收盘价
  protected double	PreOpenInterest;		//昨日持仓量
  protected double openPrice;				//今开盘价
  protected double highestPrice;			//最高价(Tick数据中该值为当日最高价)
  protected double lowestPrice;				//最低价(Tick数据中该值为当日最低价)
  protected long volume;						// 10uVolume_Sell 成交量，最新的
  protected double turnover;				// 成交额（当日成交额）
  protected double openInterest;			//持仓量
  protected double openInterest_diff;		//当前持仓量（自己增加）
  protected double closePirce;				//今收盘
  protected double openPrice_diff;			//当前开盘价（自己增加）
  protected double closePrice_diff;			//当前收盘价（自己增加）
  protected double highestPrice_diff;		//当前最高价（自己增加）
  protected double lowestPrice_diff;		//当前最低价（自己增加）
  protected double	SettlementPrice;		//本次结算价
  protected double	UpperLimitPrice;		//涨停板价
  protected double	LowerLimitPrice;		//跌停板价
  protected double	PreDelta;				//昨虚实度
  protected double	currDelta;				//今虚实度
  protected String 	updateTime;				//最后修改时间
  protected int millisec;					//最后修改毫秒
  protected double	bidPrice1;				///申买价一
  protected int	bidVolume1;					///申买量一
  protected double	askPrice1;				///申卖价一
  protected int	askVolume1;					///申卖量一
  protected double	bidPrice2;				///申买价二
  protected int	bidVolume2;					///申买量二
  protected double	askPrice2;				///申卖价二
  protected int	askVolume2;					///申卖量二
  protected double	bidPrice3;				///申买价三
  protected int	bidVolume3;					///申买量三06dbSum
  protected double	askPrice3;				///申卖价三
  protected int	askVolume3;					///申卖量三
  protected double	bidPrice4;				///申买价四
  protected int	bidVolume4;					///申买量四
  protected double	askPrice4;				///申卖价四
  protected int	askVolume4;					///申卖量四
  protected double	bidPrice5;				///申买价五
  protected int	bidVolume5;					///申买量五
  protected double	askPrice5;				///申卖价五
  protected int	askVolume5;					///申卖量五

  protected double 	averagePrice;			//当日均价
  protected String actionDay;				//业务日期
  protected int sort;

  public KData(){}





  public KData(String symbol, int utime, int volume_diff,
      double turnover_diff, String tradingDay, String instrumentID,
      double lastPrice, double preSettlementPrice, double ytClosePrice,
      double preOpenInterest, double openPrice, double highestPrice,
      double lowestPrice, int volume, double turnover,
      double openInterest, double closePirce, double openPrice_diff,
      double settlementPrice, double upperLimitPrice,
      double lowerLimitPrice, double preDelta, double currDelta,
      String updateTime, int millisec, double bidPrice1, int bidVolume1,
      double askPrice1, int askVolume1, double bidPrice2, int bidVolume2,
      double askPrice2, int askVolume2, double bidPrice3, int bidVolume3,
      double askPrice3, int askVolume3, double bidPrice4, int bidVolume4,
      double askPrice4, int askVolume4, double bidPrice5, int bidVolume5,
      double askPrice5, int askVolume5, double averagePrice,
      String actionDay) {
    super();
    this.symbol = symbol;		this.utime = utime;			this.volume_diff = volume_diff;
    this.turnover_diff = turnover_diff;						this.tradingDay = tradingDay;
    this.instrumentID = instrumentID;						this.lastPrice = lastPrice;
    PreSettlementPrice = preSettlementPrice;				this.ytClosePrice = ytClosePrice;
    PreOpenInterest = preOpenInterest;						this.openPrice = openPrice;
    this.highestPrice = highestPrice;						this.lowestPrice = lowestPrice;
    this.volume = volume;									this.turnover = turnover;
    this.openInterest = openInterest;						this.closePirce = closePirce;
    this.openPrice_diff = openPrice_diff;					SettlementPrice = settlementPrice;
    UpperLimitPrice = upperLimitPrice;						LowerLimitPrice = lowerLimitPrice;
    PreDelta = preDelta;		this.currDelta = currDelta;		this.updateTime = updateTime;
    this.millisec = millisec;	this.bidPrice1 = bidPrice1;		this.bidVolume1 = bidVolume1;
    this.askPrice1 = askPrice1;		this.askVolume1 = askVolume1;	this.bidPrice2 = bidPrice2;
    this.bidVolume2 = bidVolume2;	this.askPrice2 = askPrice2;		this.askVolume2 = askVolume2;
    this.bidPrice3 = bidPrice3;		this.bidVolume3 = bidVolume3;	this.askPrice3 = askPrice3;
    this.askVolume3 = askVolume3;	this.bidPrice4 = bidPrice4;		this.bidVolume4 = bidVolume4;
    this.askPrice4 = askPrice4;		this.askVolume4 = askVolume4;	this.bidPrice5 = bidPrice5;
    this.bidVolume5 = bidVolume5;	this.askPrice5 = askPrice5;		this.askVolume5 = askVolume5;
    this.averagePrice = averagePrice;							this.actionDay = actionDay;
  }





  public String getSymbol() {return symbol;}
  public long getUtime(boolean isTick) {
    if(isTick){
      return utime; // tick数据要精确到毫秒
    }else{
      return utime / 1000; // k数据精确到秒
    }
  }
  public long getVolume_diff() {return volume_diff;}
  public double getTurnover_diff() {return turnover_diff;}
  public double getHighestPrice() {return highestPrice;}
  public double getLowestPrice() {return lowestPrice;}
  public double getLastPrice() {return lastPrice;}
  public long getVolume() {return volume;}
  public double getTurnover() {return turnover;}
  public double getOpenInterest() {return openInterest;}
  public double getOpenPrice() {return openPrice;}
  public double getClosePirce() {return closePirce;}
  public double getYtClosePrice() {return ytClosePrice;}
  public String getTradingDay() {return tradingDay;}
  public String getInstrumentID() {return instrumentID;}
  public int getMillisec() {return millisec;}
  public double getAveragePrice() {return averagePrice;}
  public String getActionDay() {return actionDay;}
  public double getOpenPrice_diff(){return openPrice_diff;}
  public double getPreSettlementPrice() {	return PreSettlementPrice;	}

  public double getPreOpenInterest() {	return PreOpenInterest;	}
  public void setPreOpenInterest(double preOpenInterest) {	PreOpenInterest = preOpenInterest;}

  public double getSettlementPrice() {	return SettlementPrice;	}
  public void setSettlementPrice(double settlementPrice) {SettlementPrice = settlementPrice;	}

  public double getUpperLimitPrice() {		return UpperLimitPrice;	}
  public void setUpperLimitPrice(double upperLimitPrice) {UpperLimitPrice = upperLimitPrice;	}

  public double getLowerLimitPrice() {	return LowerLimitPrice;	}
  public void setLowerLimitPrice(double lowerLimitPrice) {	LowerLimitPrice = lowerLimitPrice;}

  public double getPreDelta() {	return PreDelta;}
  public void setPreDelta(double preDelta) {PreDelta = preDelta;	}

  public double getCurrDelta() {	return currDelta;}
  public void setCurrDelta(double currDelta) {	this.currDelta = currDelta;}

  public String getUpdateTime() {	return updateTime;	}
  public void setUpdateTime(String updateTime) {this.updateTime = updateTime;	}

  public double getBidPrice1() {	return bidPrice1;	}
  public void setBidPrice1(double bidPrice1) {	this.bidPrice1 = bidPrice1;	}

  public int getBidVolume1() {	return bidVolume1;}
  public void setBidVolume1(int bidVolume1) {	this.bidVolume1 = bidVolume1;}

  public double getAskPrice1() {	return askPrice1;}
  public void setAskPrice1(double askPrice1) {	this.askPrice1 = askPrice1;	}

  public int getAskVolume1() {	return askVolume1;}
  public void setAskVolume1(int askVolume1) {	this.askVolume1 = askVolume1;}

  public double getBidPrice2() {		return bidPrice2;	}
  public void setBidPrice2(double bidPrice2) {this.bidPrice2 = bidPrice2;	}

  public int getBidVolume2() {	return bidVolume2;	}
  public void setBidVolume2(int bidVolume2) {	this.bidVolume2 = bidVolume2;	}

  public double getAskPrice2() {	return askPrice2;}
  public void setAskPrice2(double askPrice2) {this.askPrice2 = askPrice2;}

  public int getAskVolume2() {return askVolume2;	}
  public void setAskVolume2(int askVolume2) {this.askVolume2 = askVolume2;}

  public double getBidPrice3() {	return bidPrice3;}
  public void setBidPrice3(double bidPrice3) {	this.bidPrice3 = bidPrice3;}

  public int getBidVolume3() {return bidVolume3;}
  public void setBidVolume3(int bidVolume3) {this.bidVolume3 = bidVolume3;}

  public double getAskPrice3() {return askPrice3;	}
  public void setAskPrice3(double askPrice3) {	this.askPrice3 = askPrice3;}

  public int getAskVolume3() {	return askVolume3;	}
  public void setAskVolume3(int askVolume3) {	this.askVolume3 = askVolume3;	}

  public double getBidPrice4() {	return bidPrice4;}
  public void setBidPrice4(double bidPrice4) {	this.bidPrice4 = bidPrice4;	}

  public int getBidVolume4() {	return bidVolume4;}
  public void setBidVolume4(int bidVolume4) {	this.bidVolume4 = bidVolume4;	}

  public double getAskPrice4() {	return askPrice4;}
  public void setAskPrice4(double askPrice4) {this.askPrice4 = askPrice4;}

  public int getAskVolume4() {	return askVolume4;}
  public void setAskVolume4(int askVolume4) {this.askVolume4 = askVolume4;}

  public double getBidPrice5() {return bidPrice5;}
  public void setBidPrice5(double bidPrice5) {	this.bidPrice5 = bidPrice5;}

  public int getBidVolume5() {return bidVolume5;}
  public void setBidVolume5(int bidVolume5) {	this.bidVolume5 = bidVolume5;}

  public double getAskPrice5() {	return askPrice5;}
  public void setAskPrice5(double askPrice5) {this.askPrice5 = askPrice5;}

  public int getAskVolume5() {return askVolume5;}
  public void setAskVolume5(int askVolume5) {	this.askVolume5 = askVolume5;}

  public double getClosePrice_diff() {return closePrice_diff;	}
  public void setClosePrice_diff(double closePrice_diff) {this.closePrice_diff = closePrice_diff;}

  public double getOpenInterest_diff() {	return openInterest_diff;}
  public void setOpenInterest_diff(double openInterest_diff) {this.openInterest_diff = openInterest_diff;}



  public void setClosePirce(double closePirce) {	this.closePirce = closePirce;}
  public void setOpenPrice_diff(double openPrice_diff) {this.openPrice_diff = openPrice_diff;}
  public double getHighestPrice_diff() {return highestPrice_diff;}
  public void setHighestPrice_diff(double highestPrice_diff){this.highestPrice_diff = highestPrice_diff;}
  public double getLowestPrice_diff() {return lowestPrice_diff;}
  public void setLowestPrice_diff(double lowestPrice_diff) {this.lowestPrice_diff = lowestPrice_diff;}
  public int getSort() {return sort;}
  public void setSort(int sort) {this.sort = sort;}

  public void setSymbol(String symbol) {this.symbol = symbol;}
  public void setUtime(long utime) {this.utime = utime;}
  public void setVolume_diff(long volume_diff) {this.volume_diff = volume_diff;}
  public void setTurnover_diff(double turnover_diff) {this.turnover_diff = turnover_diff;}
  public void setHighestPrice(double highestPrice) {this.highestPrice = highestPrice;}
  public void setLowestPrice(double lowestPrice) {this.lowestPrice = lowestPrice;}
  public void setLastPrice(double lastPrice) {this.lastPrice = lastPrice;}
  public void setVolume(long volume) {this.volume = volume;}
  public void setTurnover(double turnover) {this.turnover = turnover;}
  public void setOpenInterest(double openInterest) {this.openInterest = openInterest;}
  public void setOpenPrice(double openPrice) {this.openPrice = openPrice;}
  public void setClosePrice(double closePirce) {this.closePirce = closePirce;}
  public void setYtClosePrice(double ytClosePrice) {this.ytClosePrice = ytClosePrice;}
  public void setTradingDay(String tradingDay) {this.tradingDay = tradingDay;}
  public void setInstrumentID(String instrumentID) {this.instrumentID = instrumentID;}
  public void setMillisec(int millisec) {this.millisec = millisec;}
  public void setAveragePrice(double AveragePrice) {this.averagePrice = AveragePrice;}
  public void setActionDay(String actionDay) {this.actionDay = actionDay;}
  public void setPreSettlementPrice(double preSettlementPrice) {PreSettlementPrice=preSettlementPrice;}

  // 根据是tick数据还是kLine数据返回不同格式的rowkey
  public String getRowKey(boolean isTick) {
    String rowKey;
    if(isTick){
      // rowkey中的时间戳无论是tick还是kline都精确到秒
      rowKey = this.getSymbol() + "_" + this.getUtime(false) + "_" + this.getSort();
    }else{
      rowKey = this.getSymbol() + "_" + this.getUtime(false);
    }
    return rowKey;
  }

  public KData clone() throws CloneNotSupportedException {
    KData mdata = (KData)super.clone();
    return mdata;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("{\"");
    if (symbol != null) {
      builder.append("symbol\":\"");
      builder.append(symbol);
      builder.append("\",\"");
    }
    builder.append("utime:");				builder.append(utime);
    builder.append(",当前成交量:");	builder.append(volume_diff);
    builder.append(",当前成交额:");	builder.append(turnover_diff);
    builder.append(",高:");	builder.append(highestPrice_diff);
    builder.append(",低:");	builder.append(lowestPrice_diff);
    builder.append(",当前开:");	builder.append(openPrice_diff);
    builder.append(",当前收:");	builder.append(closePrice_diff);
    builder.append(",今量:");			builder.append(volume);
    builder.append(",今额:");		builder.append(turnover);
    builder.append(",今持仓:");	builder.append(openInterest);
    builder.append(",今开:");		builder.append(openPrice);
    builder.append(",今收:");		builder.append(closePirce);
    builder.append(",今均:");	builder.append(averagePrice);
    builder.append(",");
    if (tradingDay != null) {
      builder.append("交易日:");
      builder.append(tradingDay);
      builder.append(",");
    }
    builder.append("}");
    return builder.toString();
  }

  public String printStr(){
    StringBuffer sbuf = new StringBuffer();
    append(sbuf, String.valueOf("01cSymbol"),
        String.valueOf(String.valueOf(this.getSymbol())));
    append(sbuf, String.valueOf("02dbClosePrice"),
        String.valueOf(String.valueOf(this.getClosePrice_diff())));
    append(sbuf, String.valueOf("03dbHeightPrice"),
        String.valueOf(String.valueOf(this.getHighestPrice_diff())));
    append(sbuf, String.valueOf("04dbLowPrice"),
        String.valueOf(String.valueOf(this.getLowestPrice_diff())));
    append(sbuf, String.valueOf("05dbOpenPrice"),
        String.valueOf(String.valueOf(this.getOpenPrice_diff())));
    append(sbuf, String.valueOf("06dbSum"),
        String.valueOf(String.valueOf(this.getTurnover_diff())));
    append(sbuf, String.valueOf("07dbYTClosePrice"),
        String.valueOf(String.valueOf(this.getYtClosePrice())));
    append(sbuf, String.valueOf("08uTime"),
        String.valueOf(String.valueOf(this.getUtime(false))));
    append(sbuf, String.valueOf("09uVolume"),
        String.valueOf(String.valueOf(this.getVolume_diff())));
    append(sbuf, String.valueOf("10uVolume_Sell"),
        String.valueOf(String.valueOf(this.getVolume())));
    append(sbuf, String.valueOf("11zpos"),
        String.valueOf(String.valueOf(this.getOpenInterest())));
    append(sbuf, String.valueOf("12zpos_diff"),
        String.valueOf(String.valueOf(this.getOpenInterest_diff())));
    append(sbuf, String.valueOf("13avgPrice"),
        String.valueOf(String.valueOf(this.getAveragePrice())));

    return sbuf.toString();
  }

protected void append(StringBuffer sbuf, String key, String value){
    sbuf.append(key).append(":").append(value).append("\n");
}
}













