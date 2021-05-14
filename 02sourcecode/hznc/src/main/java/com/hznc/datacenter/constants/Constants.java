package com.hznc.datacenter.constants;

import java.lang.reflect.GenericArrayType;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 保存一些静态常量
 *
 */
public class Constants {

  // k线合并的周期
  public static final int CYCEL_SECOND_ONE = 1; // 1s
  public static final int CYCEL_SECOND_TWO = 2; // 2s
  public static final int CYCEL_SECOND_THREE = 3; // 3s
  public static final int CYCEL_SECOND_FIVE = 5; // 5s
  public static final int CYCEL_SECOND_TEN = 10; // 10s
  public static final int CYCEL_SECOND_FIFTEEN = 15; // 15s
  public static final int CYCEL_SECOND_THIRTY = 30; // 30s
  public static final int CYCEL_MINUTE_ONE = 60; // 1min
  public static final int CYCEL_MINUTE_FIVE = 5 * 60; // 5 min
  public static final int CYCEL_MINUTE_FIFTEEN = 15 * 60; // 15 min
  public static final int CYCEL_MINUTE_THIRTY = 30 * 60; // 30 min
  public static final int CYCEL_HOUR_ONE = 60 * 60; // 1hour
  public static final int CYCEL_DAY_ONE = 60 * 60 * 8; // 1day

  // 周期的等级
  public static final String CYCEL_LEVEL_SECOND_ONE = "oneSecond"; // 1s 单独线程执行
  public static final String CYCEL_LEVEL_SECONDS = "seconds";
  public static final String CYCEL_LEVEL_MINUTES = "minutes";
  public static final String CYCEL_LEVEL_DAY = "day";
  public static final String CYCEL_LEVEL_WEEK = "week";
  public static final String CYCEL_LEVEL_MONTH = "month";
  // 每个周期包含的k线  TODO: 月线， 秒周期的其他k线和分钟周期的其他k线
  public static final int[] CYCEL_SECOND_ONE_LIST =  {CYCEL_SECOND_ONE};
  public static final int[] CYCEL_SECONDS_LIST =  {CYCEL_SECOND_TWO,CYCEL_SECOND_THREE, CYCEL_SECOND_FIVE, CYCEL_SECOND_TEN,CYCEL_SECOND_FIFTEEN
      , CYCEL_SECOND_THIRTY};
  public static final int[] CYCEL_MINUTES_LIST =  {CYCEL_MINUTE_ONE, CYCEL_MINUTE_FIVE,
      CYCEL_MINUTE_FIFTEEN,CYCEL_MINUTE_THIRTY,CYCEL_HOUR_ONE};
  public static final int[] CYCEL_DAY_LIST =  {CYCEL_DAY_ONE};

  // k线不同周期对应的表名
  public static final String TABLE_SECOND_ONE = "hznc_mkdata:1sdata";
  public static final String TABLE_SECOND_TWO = "hznc_mkdata:2sdata";
  public static final String TABLE_SECOND_THREE = "hznc_mkdata:3sdata";
  public static final String TABLE_SECOND_FIVE = "hznc_mkdata:5sdata";
  public static final String TABLE_SECOND_TEN = "hznc_mkdata:10sdata";
  public static final String TABLE_SECOND_FIFTEEN = "hznc_mkdata:15sdata";
  public static final String TABLE_SECOND_THIRTY = "hznc_mkdata:30sdata";
  public static final String TABLE_MINUTE_ONE = "hznc_mkdata:1mindata";
  public static final String TABLE_MINUTE_FIVE = "hznc_mkdata:5mindata";
  public static final String TABLE_MINUTE_FIFTEEN = "hznc_mkdata:15mindata";
  public static final String TABLE_MINUTE_THIRTY = "hznc_mkdata:30mindata";
  public static final String TABLE_HOUR_ONE = "hznc_mkdata:60mindata";
  public static final String TABLE_DAY_ONE = "hznc_mkdata:ddata";
  public static final String TABLE_TICK = "hznc_mkdata:tickdata"; // 特殊，tick表
  // 数据类型
  public static final int DATA_TYPE_FUTURES = 1;  // 股票0，期货1
  public static final int VAR_I = 0; // TODO(TWang): 这个变量有什么意义？

  // kParam中的时间区间
  public static final int KPARAM_TIME_T2100 = -10800; // 昨日21:00
  public static final int KPARAM_TIME_T2300 = -3600; // 昨日23:00
  public static final int KPARAM_TIME_T2330 = -1800; // 昨日23:30
  public static final int KPARAM_TIME_T0300 = 10800; // 03:00:00
  public static final int KPARAM_TIME_T0900 = 32400; // 09:00:00
  public static final int KPARAM_TIME_T0930 = 34200; // 09:30:00
  public static final int KPARAM_TIME_T1130 = 41400; // 11:30
  public static final int KPARAM_TIME_T1300 = 46800; // 13:00
  public static final int KPARAM_TIME_T1500 = 54000; // 15:00

  // 日盘和夜盘的时间区间，小时
  public final static Calendar BUSINESS_T0900; // 9:00
  public final static Calendar BUSINESS_T0930; // 9:30
  public final static Calendar BUSINESS_T1130; // 11:30
  public final static Calendar BUSINESS_T1500; // 15:00
  public final static Calendar BUSINESS_T1530;  // 15点30
  public final static Calendar BUSINESS_T1600;  // 16点
  public final static Calendar BUSINESS_T2100;
  public final static Calendar BUSINESS_T2400;
  public final static Calendar BUSINESS_T0000;
  public final static Calendar BUSINESS_T0300;
  public final static Calendar BUSINESS_T0330;
  public final static Calendar BUSINESS_T0400;

  static {
    BUSINESS_T0900 = new GregorianCalendar();
    BUSINESS_T0900.set(Calendar.HOUR_OF_DAY, 9);
    BUSINESS_T0900.set(Calendar.MINUTE, 0);
    BUSINESS_T0900.set(Calendar.SECOND, 0);

    BUSINESS_T0930 = new GregorianCalendar();
    BUSINESS_T0930.set(Calendar.HOUR_OF_DAY, 9);
    BUSINESS_T0930.set(Calendar.MINUTE, 30);
    BUSINESS_T0930.set(Calendar.SECOND, 0);

    BUSINESS_T1130 = new GregorianCalendar();
    BUSINESS_T1130.set(Calendar.HOUR_OF_DAY, 11);
    BUSINESS_T1130.set(Calendar.MINUTE, 30);
    BUSINESS_T1130.set(Calendar.SECOND, 0);

    BUSINESS_T1500 = new GregorianCalendar();
    BUSINESS_T1500.set(Calendar.HOUR_OF_DAY, 15);
    BUSINESS_T1500.set(Calendar.MINUTE, 0);
    BUSINESS_T1500.set(Calendar.SECOND, 0);

    BUSINESS_T1530 = new GregorianCalendar();
    BUSINESS_T1530.set(Calendar.HOUR_OF_DAY, 15);
    BUSINESS_T1530.set(Calendar.MINUTE, 30);
    BUSINESS_T1530.set(Calendar.SECOND, 0);

    BUSINESS_T1600 = new GregorianCalendar();
    BUSINESS_T1600.set(Calendar.HOUR_OF_DAY, 16);
    BUSINESS_T1600.set(Calendar.MINUTE, 00);
    BUSINESS_T1600.set(Calendar.SECOND, 0);


    BUSINESS_T2100 = new GregorianCalendar();
    BUSINESS_T2100.set(Calendar.HOUR_OF_DAY, 21);
    BUSINESS_T2100.set(Calendar.MINUTE, 0);
    BUSINESS_T2100.set(Calendar.SECOND, 0);

    BUSINESS_T2400 = new GregorianCalendar();
    BUSINESS_T2400.set(Calendar.HOUR_OF_DAY, 24);
    BUSINESS_T2400.set(Calendar.MINUTE, 0);
    BUSINESS_T2400.set(Calendar.SECOND, 0);

    BUSINESS_T0000 = new GregorianCalendar();
    BUSINESS_T0000.set(Calendar.HOUR_OF_DAY, 0);
    BUSINESS_T0000.set(Calendar.MINUTE, 0);
    BUSINESS_T0000.set(Calendar.SECOND, 0);

    BUSINESS_T0300 = new GregorianCalendar();
    BUSINESS_T0300.set(Calendar.HOUR_OF_DAY, 3);
    BUSINESS_T0300.set(Calendar.MINUTE, 0);
    BUSINESS_T0300.set(Calendar.SECOND, 0);

    BUSINESS_T0330 = new GregorianCalendar();
    BUSINESS_T0330.set(Calendar.HOUR_OF_DAY, 3);
    BUSINESS_T0330.set(Calendar.MINUTE, 30);
    BUSINESS_T0330.set(Calendar.SECOND, 0);


    BUSINESS_T0400 = new GregorianCalendar();
    BUSINESS_T0400.set(Calendar.HOUR_OF_DAY, 4);
    BUSINESS_T0400.set(Calendar.MINUTE, 0);
    BUSINESS_T0400.set(Calendar.SECOND, 0);
  }

  // 周期和对应hbase表名的映射:<周期， 周期表>
  public static Map<Integer, String> cycleTableMap = new HashMap() {
    {
      put(CYCEL_SECOND_ONE, TABLE_SECOND_ONE); // 1S
      put(CYCEL_SECOND_TWO, TABLE_SECOND_TWO); // 2S
      put(CYCEL_SECOND_THREE, TABLE_SECOND_THREE); // 3S
      put(CYCEL_SECOND_FIVE, TABLE_SECOND_FIVE); // 5s
      put(CYCEL_SECOND_TEN, TABLE_SECOND_TEN); // 5s
      put(CYCEL_SECOND_FIFTEEN, TABLE_SECOND_FIFTEEN); // 15s
      put(CYCEL_SECOND_THIRTY, TABLE_SECOND_THIRTY); // 30s
      put(CYCEL_MINUTE_ONE, TABLE_MINUTE_ONE); // 1min
      put(CYCEL_MINUTE_FIVE, TABLE_MINUTE_FIVE); // 5min
      put(CYCEL_MINUTE_FIFTEEN, TABLE_MINUTE_FIFTEEN); // 15min
      put(CYCEL_MINUTE_THIRTY, TABLE_MINUTE_THIRTY); // 30min
      put(CYCEL_HOUR_ONE, TABLE_HOUR_ONE); // 1hour
      put(CYCEL_DAY_ONE, TABLE_DAY_ONE); // 1d
    }
  };
}
