package com.hznc.datacenter.bean;

public class TickData implements Cloneable{
	
	
	

		private String    cSymbol;
		private long	uTime;
		private int		buysellflag;  //买卖标志
		private int  beatcnt;

		private double	volume; //一天交易总量
		private double	sum;//一天交易金额
		private double	open_day; //当天开盘价
		private double	high_day;//当天最高价
		private double	low_day;//当天最低价
		private double	close_yt; //昨日收盘价
		private double	pos_yt;//持仓金额  0
		private double	pos;//金额      0
		private double	settle;//金额  0
		private double	uplimit;//涨停价 
		private double	dnlimit;//跌停价
		private double	avg;// 均价
		private double	volume_diff;//当前tick成交量
		private double	sum_diff;//当前成交金额
		private double high;    //当前时间段最高价
		private double low ;    //当前时间段最低价
		private double	open;//当前tick开盘价
		private double	close;//收盘  最新价
		private double zpos_diff ;//当前持仓量
		private int z ; //标志tick数据是前0.5s还是后0.5s
		
		private int m ;
		
		public TickData(){
			
		}
		
		public TickData(String  cSymbol , int	uTime, int	buysellflag , double   close_yt,
				double	open_day, double	low_day, double	 high_day,  double	sum,
				double	pos_yt, double	pos,double	settle ,double	uplimit, double	dnlimit , 
				 double	avg, double	volume_diff, double	sum_diff,  
				double	open, double  close, double zpos_diff , double  volume , int z){
			this.cSymbol = cSymbol ;			this.uTime = uTime ;
			this.buysellflag = buysellflag ;	this.close_yt = close_yt ;
			this.open_day = open_day ;			this.low_day = low_day ;
			this.high_day = high_day ;			this.sum = sum ;
			this.pos_yt = pos_yt ;				this.pos = pos ;
			this.settle = settle ;				this.uplimit = uplimit ;
			this.dnlimit = dnlimit ;			this.avg = avg ;
			this.volume_diff = volume_diff ;	this.sum_diff = sum_diff ;
			this.open = open ;					this.close = close ;
			this.volume = volume ;				this.z = z ;
			this.zpos_diff = zpos_diff ;
			
		}

		public String getcSymbol() {
			return cSymbol;
		}

		public void setcSymbol(String cSymbol) {
			this.cSymbol = cSymbol;
		}

		public long getuTime() {
			return uTime;
		}

		public void setuTime(long uTime) {
			this.uTime = uTime;
		}

		public int getBuysellflag() {
			return buysellflag;
		}

		public void setBuysellflag(int buysellflag) {
			this.buysellflag = buysellflag;
		}
		
		public int getBeatcnt() {
			return beatcnt;
		}

		public void setBeatcnt(int beatcnt) {
			this.beatcnt = beatcnt;
		}


		public double getClose_yt() {
			return close_yt;
		}

		public void setClose_yt(double close_yt) {
			this.close_yt = close_yt;
		}

		public double getOpen_day() {
			return open_day;
		}

		public void setOpen_day(double open_day) {
			this.open_day = open_day;
		}

		public double getLow_day() {
			return low_day;
		}

		public void setLow_day(double low_day) {
			this.low_day = low_day;
		}

		public double getHigh_day() {
			return high_day;
		}

		public void setHigh_day(double high_day) {
			this.high_day = high_day;
		}

		public double getSum() {
			return sum;
		}

		public void setSum(double sum) {
			this.sum = sum;
		}

		public double getPos_yt() {
			return pos_yt;
		}

		public void setPos_yt(double pos_yt) {
			this.pos_yt = pos_yt;
		}

		public double getPos() {
			return pos;
		}

		public void setPos(double pos) {
			this.pos = pos;
		}

		public double getsettle() {
			return settle;
		}

		public void setSettle(double settle) {
			this.settle = settle;
		}

		public double getUplimit() {
			return uplimit;
		}

		public void setUplimit(double uplimit) {
			this.uplimit = uplimit;
		}

		public double getDnlimit() {
			return dnlimit;
		}

		public void setDnlimit(double dnlimit) {
			this.dnlimit = dnlimit;
		}

		public double getAvg() {
			return avg;
		}

		public void setAvg(double avg) {
			this.avg = avg;
		}

		public double getVolume_diff() {
			return volume_diff;
		}

		public void setVolume_diff(double volume_diff) {
			this.volume_diff = volume_diff;
		}

		public double getSum_diff() {
			return sum_diff;
		}

		public void setSum_diff(double sum_diff) {
			this.sum_diff = sum_diff;
		}
		
		public double getHigh() {
			return high;
		}

		public void setHigh(double high) {
			this.high = high;
		}
		
		public double getLow() {
			return low;
		}

		public void setLow(double low) {
			this.low = low;
		}

		public double getOpen() {
			return open;
		}

		public void setOpen(double open) {
			this.open = open;
		}

		public double getClose() {
			return close;
		}

		public void setClose(double close) {
			this.close = close;
		}

		public double getVolume() {
			return volume;
		}

		public void setVolume(double volume) {
			this.volume = volume;
		}

		public int getM() {
			return m;
		}

		public void setM(int m) {
			this.m = m;
		}
		
		public double getzpos_diff() {
			return zpos_diff;
		}

		public void setzpos_diff(double zpos_diff) {
			this.zpos_diff = zpos_diff;
		}
		
		public int getz(){
			return z;
		}
		public void setz(int z){
			this.z= z;
		}
		
		public TickData clone() throws CloneNotSupportedException {
			TickData tdata = (TickData)super.clone();
			return tdata;
		}

		@Override
		public String toString() {
//			return "TickData [cSymbol=" + cSymbol + ", uTime=" + uTime
//					+ ", buysellflag=" + buysellflag + ", close_yt=" + close_yt
//					+ ", open_day=" + open_day + ", low_day=" + low_day
//					+ ", high_day=" + high_day + ", sum=" + sum + ", pos_yt="
//					+ pos_yt + ", pos=" + pos +", zpos_diff="+zpos_diff+ ", settle=" + settle
//					+ ", uplimit=" + uplimit + ", dnlimit=" + dnlimit + ", avg="
//					+ avg + ", volume_diff=" + volume_diff + ", sum_diff="
//					+ sum_diff + ", open=" + open + ", close=" + close
//					+ ", volume=" + volume + ", m=" + m +", z=" +z+"]";
			
			return "TickData [cSymbol=" + cSymbol + ", uTime=" + uTime
					+  ", zpos_diff="+zpos_diff+ 
					", volume_diff=" + volume_diff + ", sum_diff="+sum_diff+", hight="+high+", low="+low
					 + ", open=" + open + ", close=" + close
					+ ", volume=" + volume + ", m=" + m +", z=" +z+"]";
			
//			return cSymbol +"," + uTime+ "," + buysellflag + ","+ volume + "," + sum + ","
//					+ open_day + ","+ high_day + ","+ low_day+ ","  + close_yt+","
//					+ pos_yt + "," + pos + "," + settle+ "," + uplimit + "," + dnlimit + ","
//					+ avg + "," + volume_diff + ","+ sum_diff + "," + open + "," + close
//					+ ","  + m ;
		}


		

}
