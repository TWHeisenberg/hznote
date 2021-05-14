package com.hznc.datacenter.utils;
import static com.hznc.datacenter.constants.Constants.*;
import com.google.common.annotations.VisibleForTesting;
import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.bean.KParamStruct;
import com.hznc.datacenter.bean.TDurationStruct;
import com.hznc.datacenter.bean.TickDataStructBak;
import com.hznc.datacenter.bean.cnative.CKData;
import com.hznc.datacenter.hbase.HbaseClient;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * 调用c++接口生成k线数据
 *
 * @author TWang
 * @date 2021-04-19 16:30:00
 */
public class CreateKLineUtil {

  private static Logger logger = Logger.getLogger(CreateKLineUtil.class);

  private static final String NATIVE_DLL = "NCAlgo";

  private Map<String, KData> mergedKlineSet;
  private HbaseClient hbaseClient;

  private static final int _1day = 86400; // 1天的秒数, 24*60*60
  private static final int _1week = 604800; // 一周的秒数, 7*24*60*60
  private int _60min = 3600;
  private int ZONE_SECS = 86400;
  private int ZONE = 8;
  private int _0sec = 0;
  private Calendar calendar;


  public CreateKLineUtil(Map<String, KData> mergedKlineSet, HbaseClient hbaseClient) {
    this.mergedKlineSet = mergedKlineSet;
    this.hbaseClient = hbaseClient;
    this.calendar = Calendar.getInstance();
  }

  // k线算法，生成k线
  public void createKLine(CKData tickData, CKData kBuff,
      KParamStruct param, String info, KLineCallback callback, Pointer any)
      throws CloneNotSupportedException {
    if (tickData.Volume < 0 || tickData.Amount < 0) {
      return;
    }

    if (param.cycle < CYCEL_HOUR_ONE) {
      if (param.isNight == 0) { // 不是夜盘
        getKLineDayHour(tickData, kBuff, param, info, callback, any);
      } else {
        getKLineNightHour(tickData, kBuff, param, info, callback, any);
      }
    } else {
      getKLineDay(tickData, kBuff, param, info, callback, any);
    }
  }

  // 补k线
  public void packKLine(long endTs, CKData kBuff, KParamStruct param,
      String symbolWithCycle, KLineCallback callback, Pointer p)
      throws CloneNotSupportedException {
    if (param.cycle == 0 || kBuff.TimeStamp == 0) {
      return;
    }

    if (param.cycle <= _60min) {
      if (!(param.isNight == 1)) {
        _packKLineDayHour(endTs, kBuff, param, symbolWithCycle, callback, p);
      } else {
        _packKLineNightHour(endTs, kBuff, param, symbolWithCycle, callback, p);
      }
    } else {
      _packKLineDay(endTs, kBuff, param, symbolWithCycle, callback, p);
    }

  }

  // 保存到对应的周期表
  private void storeToCycleTable(String symbolWithCycle, KData kline) throws IOException {
    // 保存到对应的周期表
    int cycle = Integer.parseInt(symbolWithCycle.split("_")[1]);
    String rowkey = kline.getRowKey(false);

    // 根据type值判断是秒级别的数据还是分钟级别的数据，用于更改表名
    String tablename = null;
    if (cycle >= CYCEL_MINUTE_ONE && cycle < CYCEL_DAY_ONE) {
      tablename = "hznc_mkdata:" + cycle / 60 + "mindata";

    } else if (cycle < CYCEL_MINUTE_ONE) {
      tablename = "hznc_mkdata:" + cycle + "sdata";
    } else if (cycle == CYCEL_DAY_ONE){
      tablename = "hznc_mkdata:ddata";
    }

    if (tablename == null) {
      throw new IllegalArgumentException("tablename is null, cycle is " + cycle);
    }

    hbaseClient.singlePut(tablename, kline, false);
    logger.info("store kline "+rowkey+" to "+tablename+" ok");
  }

  /**
   * 方法名: normtime 方法说明: 将数据中的时间戳格式化，统一为各周期的标准时间 参数说明:
   *
   * @param cycle 周期
   * @param kdata 当前数据
   */
  @SuppressWarnings("deprecation")
  @VisibleForTesting
  public static KData normtime(int cycle, KData kdata)
      throws CloneNotSupportedException, ParseException {
    KData tdata_type = kdata.clone();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    Date date = new Date(kdata.getUtime(true));
    Date shidianban = df.parse(
        (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " 10:30");
    Date shierdian = df.parse(
        (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " 12:00");
    Date badian = df.parse(
        (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " 8:00");
    String actionDay = new String(kdata.getActionDay());
    StringBuilder sbad = new StringBuilder(actionDay);
    sbad.insert(4, "-");
    sbad.insert(7, "-");
    Date zeroday = df.parse(sbad + " 00:00");
    if (cycle == 60 * 60) {
      if (date.compareTo(shidianban) < 0 && date.compareTo(badian) > 0) {//时间相比较   小于0说明是十点半之前的
        String normhours =
            (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " 9:30";
        tdata_type.setUtime(df.parse(normhours).getTime());
      } else if (date.compareTo(shidianban) >= 0 && date.compareTo(shierdian) < 0) {//十点半之后 十二点之前
        String normhours =
            (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " 10:30";
        tdata_type.setUtime(df.parse(normhours).getTime());
      } else {
        tdata_type.setUtime(kdata.getUtime(true));
      }
    } else if (cycle == 60 * 60 * 8) {
      tdata_type.setUtime((zeroday.getTime()));
    } else {
      // 为了将时间对周期取整，归并到周期内的k线中，如10:00:06归并到10:00:05周期的k线
      tdata_type.setUtime((kdata.getUtime(false) / cycle) * cycle * 1000);
    }
    return tdata_type;
  }

  // TODO(TWang):完善单元测试
  /********************************下面方法从c++重构过来的代码******************************************************/
  private long _packKLineDay(long endTime, CKData kBuff, KParamStruct param,
      String symbolWithCycle, KLineCallback callback, Object p)
      throws CloneNotSupportedException {

    long tickCycle, kCycle, oldCycle;
    int beginOffset = 0;

    // 开盘时间偏移量
    if (param.isNight == 1) {
      beginOffset = _60min * 4;
    }

    switch (param.cycle) {
      case (_1day): {
        tickCycle = _daySinceEpoch(endTime + beginOffset, ZONE);
        kCycle = _daySinceEpoch(kBuff.TimeStamp + beginOffset, ZONE);
      }
      break;
      case (_1week): {
        tickCycle = _weekSinceEpoch(endTime + beginOffset, ZONE);
        kCycle = _weekSinceEpoch(kBuff.TimeStamp + beginOffset, ZONE);
      }
      break;
      default: {
        tickCycle = _monthSinceEpoch(endTime + beginOffset);
        kCycle = _monthSinceEpoch(kBuff.TimeStamp + beginOffset);
      }
    }

    while (tickCycle - kCycle > 1) {
      oldCycle = kCycle;
      /* 判断补 K 是否周六, 周日, 或法定假日 */
      long nextDayTs = nextDayTimestamp(kBuff.TimeStamp);
      // 下一天
      kBuff.TimeStamp = nextDayTs;
      switch (param.cycle) {
        case (_1day): {
          kCycle = _daySinceEpoch(kBuff.TimeStamp + beginOffset, ZONE);
        }
        break;
        case (_1week): {
          kCycle = _weekSinceEpoch(kBuff.TimeStamp + beginOffset, ZONE);
        }
        break;
        default: {
          kCycle = _monthSinceEpoch(kBuff.TimeStamp + beginOffset);
        }
      }
      //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
      if (_isDayOff(kBuff.TimeStamp, param.holidaysTS, param.hSize, ZONE) == 1) {
        continue;
      }

      if (oldCycle != kCycle) {
        kBuff.Volume = kBuff.Inc = kBuff.Imp = 0;
        kBuff.Amount = 0;
        kBuff.OpenInterest_diff = 0;
        callback(symbolWithCycle, kBuff);
      }
    }
    return kCycle; // 补完后 K 线所处周期

  }

  private long _monthSinceEpoch(long ts) {
    Date date = new Date(ts * 1000);
    calendar.clear();
    calendar.setTime(date);
    return (calendar.get(Calendar.YEAR) - 70) * 12 + calendar.get(Calendar.MONTH) + 1;
  }

  int _weekSinceEpoch(long ts, long zone) {
    return (int) (ts + zone * _60min + _1day * 3) / (_1day * 7);
  }

  private void getKLineNightHour(CKData tickData, CKData kBuff, KParamStruct param,
      String info, KLineCallback callback, Pointer any)
      throws CloneNotSupportedException {

    int tickMod = toTodySeconds(tickData.TimeStamp); // 是每天的第 N 秒
    int kMod = toTodySeconds(kBuff.TimeStamp);       // 是每天的第 N 秒
    int isValidTick = 0;    // 是否有效时间内的 Tick, 如: 09:30 ~ 11:30, 13:00 ~ 15:00
    int isEndTick = 0;      // 是否交易结束时间点的 Tick, 如 11:30, 15:00
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    long tickCycle, kCycle;  // 当前 Tick, K 线所属周期
    int beginOffset = 0;

    // 开盘时间偏移量
    if (param.dSize > 0) {
      beginOffset = param.durations[0].begin - _60min;
    }

    // 判断当前 Tick 和 K 线所处时间段
    int begin, end, restTime;
    for (int i = 0; i < param.dSize; i++) {
      // 判断是否为交易结束时间点的 Tick ?
      if (tickMod == (_1day + param.durations[i].end) % _1day) {
        isEndTick = 1;
      }

      // 判断 Tick 时间是否在有效交易时段 ?
      if (param.durations[i].begin < 0) {
        begin = (_1day + param.durations[i].begin) % _1day;
      } else {
        begin = param.durations[i].begin;
      }

      if (param.durations[i].end < 0) {
        end = (_1day + param.durations[i].end) % _1day;
      } else {
        end = param.durations[i].end;
      }

      if (begin < end) {
        if (tickMod >= begin && tickMod <= end) {
          isValidTick = 1;
        }
      } else {
        if (tickMod >= begin && tickMod < _1day || tickMod >= 0 && tickMod <= end) {
          isValidTick = 1;
        }
      }

      if (i > 0) {
        // Tick 时间点已经历的小节休息时间
        if ((tickMod - beginOffset) % _1day >= begin - beginOffset) {
          restTime = param.durations[i].begin - param.durations[i - 1].end;
          if (restTime < _60min * 3) {
            tickBreakOffset += restTime;
          }
        }
        // K 线时间点已经历的小节休息时间
        if ((kMod - beginOffset) % _1day >= begin - beginOffset) {
          restTime = param.durations[i].begin - param.durations[i - 1].end;
          if (restTime < _60min * 3) {
            kBreakOffset += restTime;
          }
        }
      }
    }

    // 如果周期是 0
    if (param.cycle == 0) {
      kBuff.PreClosePrice = kBuff.ClosePrice; // 记录上周期收盘价
      kBuff.Volume = 0;        // 初始化量额为 0
      kBuff.Amount = 0;
      kBuff.OpenInterest_diff = 0;
      kBuff.TimeStamp = tickData.TimeStamp;
      kBuff.Msec = tickData.Msec;
      //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
      kBuff.HighPrice = kBuff.OpenPrice = kBuff.LowPrice = kBuff.ClosePrice = tickData.LatestPrice;
      _calcVol(kBuff, tickData, param.isTotalVol);
      kBuff.ClosePrice = tickData.LatestPrice;

      // 每个 Tick 都导致 K 线更新
      if (isValidTick == 1) {
        if (kBuff.PreClosePrice != 0) {
          kBuff.Inc = 100 * (tickData.LatestPrice - kBuff.PreClosePrice) / kBuff.PreClosePrice;
          kBuff.Imp = 100 * (kBuff.HighPrice - kBuff.LowPrice) / kBuff.PreClosePrice;
        }
        callback(info, kBuff);
      }
      return;
    }

    // 将交易时间拼接成连续的时间, 取周期
    tickCycle = (tickData.TimeStamp - beginOffset - tickBreakOffset) / param.cycle;

    // 收到第一个 Tick
    if (kBuff.TimeStamp == 0) {
      loadFirstKBuff(kBuff, tickData, param.cycle, tickCycle, beginOffset, tickBreakOffset);
    }

    // 将交易时间拼接成连续的时间, 取周期
    kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;

    // 周期差大于 1, 当前 Tick 之前有 K 线数据需要补
    if (tickCycle - kCycle > 1) {
      kCycle = _packKLineNightHour(tickData.TimeStamp, kBuff, param, info, callback, any);
    }

    // 周期相差 1, 表示新周期开始
    if (tickCycle - kCycle == 1) {
      // 是交易结束时间点, 归入上一周期
      if (isEndTick == 1) {
        tickCycle--;
      } else {
        // 将盘前搓合 Tick 归入第一个 K
        begin = (_1day + param.durations[0].begin) % _1day;
        if (kMod < begin && kMod >= begin - _60min) {
          if (kBuff.HighPrice < tickData.LatestPrice) {
            kBuff.HighPrice = tickData.LatestPrice;
          }
          if (kBuff.LowPrice > tickData.LatestPrice) {
            kBuff.LowPrice = tickData.LatestPrice;
          }
        } else {
          kBuff.PreClosePrice = kBuff.ClosePrice; // 记录上周期收盘价
          kBuff.Volume = 0;        // 初始化量额为 0
          kBuff.Amount = 0;
          kBuff.OpenInterest_diff = 0;
          kBuff.HighPrice = kBuff.OpenPrice = kBuff.LowPrice = kBuff.ClosePrice = tickData.LatestPrice;
        }
        // 创建新 K 线
        kBuff.TimeStamp = param.cycle * tickCycle + beginOffset + tickBreakOffset;
        //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
        _calcVol(kBuff, tickData, param.isTotalVol);
      }
    }

    // 是同一周期的 Tick
    if (tickCycle == kCycle) {
      // 更新 K 线最高价, 最低价
      if (kBuff.HighPrice < tickData.LatestPrice) {
        kBuff.HighPrice = tickData.LatestPrice;
      }
      if (kBuff.LowPrice > tickData.LatestPrice) {
        kBuff.LowPrice = tickData.LatestPrice;
      }
      _calcVol(kBuff, tickData, param.isTotalVol);
    }

    kBuff.ClosePrice = tickData.LatestPrice;

    // 每个 Tick 都导致 K 线更新
    if (isValidTick == 1) {
      if (kBuff.PreClosePrice != 0) {
        kBuff.Inc = 100 * (tickData.LatestPrice - kBuff.PreClosePrice) / kBuff.PreClosePrice;
        kBuff.Imp = 100 * (kBuff.HighPrice - kBuff.LowPrice) / kBuff.PreClosePrice;
      }
      callback(info, kBuff);
    }
    return;

  }

  private void loadFirstKBuff(CKData kBuff, CKData tickData, int cycle, long tickCycle,
      int beginOffset, int tickBreakOffset) {
    kBuff.TimeStamp = cycle * tickCycle + beginOffset + tickBreakOffset;
    //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
    kBuff.HighPrice = kBuff.OpenPrice = kBuff.LowPrice = kBuff.ClosePrice = tickData.LatestPrice;
    kBuff.YtClosePrice = tickData.YtClosePrice; // 昨日收价
    kBuff.Volume = tickData.Volume;
    kBuff.Amount = tickData.Amount;
    kBuff.OpenInterest_diff = tickData.OpenInterest_diff;
  }

  private long _packKLineNightHour(long endTime, CKData kBuff, KParamStruct param,
      String info, KLineCallback callback, Pointer any)
      throws CloneNotSupportedException {

    int isAfterCloseTime, nDay, isEndCycle, isValidK, begin, end, nextDayIsDayOff;
    long nextDayBegin, nextBeginTime, openTime, tickCycle, kCycle;
    // struct tm ttime;
    int beginOffset = 0;    // 开盘时间到 00:00:00 的偏移量(秒)
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kMod = toTodySeconds(kBuff.TimeStamp); // K 是每天的第 N 秒

    // 开盘时间偏移量
    if (param.dSize > 0) {
      beginOffset = param.durations[0].begin - _60min;
    }

    // 判断当前 Tick 和 K 线所经历的小节时间
    tickBreakOffset = _totalRestTime(endTime, beginOffset, param.durations, param.dSize);
    kBreakOffset = _totalRestTime(kBuff.TimeStamp, beginOffset, param.durations, param.dSize);

    kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
    tickCycle = (endTime - beginOffset - tickBreakOffset) / param.cycle;

    while (tickCycle - kCycle > 1) {
      nDay = _daySinceEpoch(kBuff.TimeStamp, ZONE); // 位于 1970-01-01 以来第 N 天
      kMod = toTodySeconds(kBuff.TimeStamp);
      kBreakOffset = _totalRestTime(kBuff.TimeStamp, beginOffset, param.durations, param.dSize);

/*      localtime_s(&ttime, kBuff.Timestamp);
      ttime.tm_hour = ttime.tm_min = ttime.tm_sec = 0;
      nextDayBegin = mktime(&ttime) + _1day;  // 就是求下一天的时间戳*/
      nextDayBegin = nextDayTimestamp(kBuff.TimeStamp);
      nextBeginTime = nextDayBegin + param.durations[0].begin;

      // 如果第二天是周末 或 节假日, 不需要补夜盘数据
      nextDayIsDayOff = _isDayOff(kBuff.TimeStamp + _1day, param.holidaysTS, param.hSize, ZONE);
      if (kMod > param.durations[param.dSize - 1].end && (nextDayIsDayOff == 1)) {
        nextBeginTime = nextDayBegin + param.durations[1].begin;
        if ((nextBeginTime - beginOffset - kBreakOffset) / param.cycle
            < (endTime - beginOffset - kBreakOffset) / param.cycle) {
          kBuff.TimeStamp = nextBeginTime;
        } else {
          kBuff.TimeStamp = ((endTime / param.cycle) - 1) * param.cycle;
        }
        kBreakOffset = _totalRestTime(kBuff.TimeStamp, beginOffset, param.durations, param.dSize);
        kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
        //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
        continue;
      }

      /* 判断补 K 是否周六, 周日, 或法定假日 */
      if (_isDayOff(kBuff.TimeStamp, param.holidaysTS, param.hSize, ZONE) == 1) {
        nextBeginTime = nextDayBegin + param.durations[1].begin;
        // 跳到下一日开盘时间, 如果大于等于 endTime, 则取 endTime 前一周期起始
        if ((nextBeginTime - beginOffset - kBreakOffset) / param.cycle
            < (endTime - beginOffset - kBreakOffset) / param.cycle) {
          kBuff.TimeStamp = nextBeginTime;
        } else {
          kBuff.TimeStamp = ((endTime / param.cycle) - 1) * param.cycle;
        }

        kBreakOffset = _totalRestTime(kBuff.TimeStamp, beginOffset, param.durations, param.dSize);
        kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
        //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
        continue;
      }

      /* 判断补 K 时间是否在有效交易时间区间 */
      isValidK = 0;
      isAfterCloseTime = 1;
      for (int i = 0; i < param.dSize; i++) {
        // 判断 Tick 时间是否在有效交易时段 ?
        if (param.durations[i].begin < 0) {
          begin = (_1day + param.durations[i].begin) % _1day;
        } else {
          begin = param.durations[i].begin;
        }

        if (param.durations[i].end < 0) {
          end = (_1day + param.durations[i].end) % _1day;
        } else {
          end = param.durations[i].end;
        }

        if (begin < end) {
          if (kMod >= begin && kMod <= end) {
            isValidK = 1;
            break;
          }
        } else {
          if (kMod >= begin && kMod < _1day || kMod >= 0 && kMod <= end) {
            isValidK = 1;
            break;
          }
        }

        if ((kMod - beginOffset) % _1day < param.durations[i].begin - beginOffset) {
          // K 线时间戳处于盘前的非交易时段, 跳到当天下一段交易起始时间; 如果大于等于 endTime, 则取 endTime前一周期
          if (param.durations[i].begin < 0) {
            openTime = nextDayBegin + param.durations[i].begin;
          } else {
            openTime = nextDayBegin - _1day + param.durations[i].begin;
          }
          if (((openTime - beginOffset - kBreakOffset) / param.cycle) < (
              (endTime - beginOffset - kBreakOffset) / param.cycle)) {
            kBuff.TimeStamp = openTime;
          } else {
            kBuff.TimeStamp = ((endTime / param.cycle) - 1) * param.cycle;
          }
          kBreakOffset = _totalRestTime(kBuff.TimeStamp, beginOffset, param.durations, param.dSize);
          kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
          //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
          isAfterCloseTime = 0;
          break;
        }
      }

      // K 线时间戳处于收盘后的非交易时段, 跳到下一交易日开盘时间, 如果大于等于 endTime, 则取 endTime前一周期
      if ((isAfterCloseTime == 1)) {
        if ((isAfterCloseTime == 1)) {
          openTime = nextDayBegin + param.durations[0].begin;
          if ((openTime - beginOffset - kBreakOffset) / param.cycle < (
              (endTime - beginOffset - kBreakOffset) / param.cycle)) {
            kBuff.TimeStamp = openTime; // 第二天的开始交易时间点
          } else {
            kBuff.TimeStamp = ((endTime / param.cycle) - 1) * param.cycle;
          }
          kBreakOffset = _totalRestTime(kBuff.TimeStamp, beginOffset, param.durations, param.dSize);
          kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
          //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
        }
        continue;
      }

      // 向前推进一周期
      kCycle++;
      kBuff.TimeStamp += param.cycle;
      //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);

      // 如果新 K 周期与交易结束时间点处于相同周期, 跳过
      kMod = toTodySeconds(kBuff.TimeStamp);   // 每天的第 N 秒
      isEndCycle = 0;
      isValidK = 0;
      for (int i = 0; i < param.dSize; i++) {
        // 判断 Tick 时间是否在有效交易时段 ?
        if (param.durations[i].begin < 0) {
          begin = (_1day + param.durations[i].begin) % _1day;
        } else {
          begin = param.durations[i].begin;
        }

        if (param.durations[i].end < 0) {
          end = (_1day + param.durations[i].end) % _1day;
        } else {
          end = param.durations[i].end;
        }

        // 检查是否为交易结束时间点的 K ?
        if (kMod == end) {
          isEndCycle = 1;
          break;
        }
        // 检查 K 时间是否在有效范围 ?
        if (begin < end) {
          if (kMod >= begin && kMod <= end) {
            isValidK = 1;
          }
        } else {
          if (kMod >= begin && kMod < _1day || kMod >= 0 && kMod <= end) {
            isValidK = 1;
          }
        }
      }

      if (isEndCycle == 1 || !(isValidK == 1)) {
        continue;
      }

      kBuff.Volume = kBuff.Inc = kBuff.Imp = 0;
      kBuff.Amount = 0;
      kBuff.OpenInterest_diff = 0;
      callback(info, kBuff);
    }
    return kCycle; // 补完后 K 线所处周期

  }

  private int _isDayOff(long timestamp, int[] holidaysTS, int hSize, int zone) {
    int result = 0;
    result = _isWeekEnd(timestamp);
    if (result == 1) {
      return result;
    }
    result = _isHoliday(timestamp, holidaysTS, hSize, zone);
    return result;
  }

  private int _isHoliday(long timestamp, int[] holidaysTS, int hSize, int zone) {
    if (holidaysTS == null || holidaysTS.length == 0) {
      return 0;
    }
    int index;
    int nDay = _daySinceEpoch(timestamp, zone);
    index = _binSearch(holidaysTS, 0, hSize - 1, nDay);
    return index >= 0 ? 1 : 0;
  }

  // 二分查找
  int _binSearch(int[] arr, int low, int high, int target) {
    while (low <= high) {
      int mid = low + (high - low) / 2;
      if (arr[mid] > target) {
        high = mid - 1;
      } else if (arr[mid] < target) {
        low = mid + 1;
      } else {
        return mid;
      }
    }
    return -1;
  }

  // 判断是否是周末，周6和周天
  private int _isWeekEnd(long timestamp) {
    Date date = new Date(timestamp * 1000);
    calendar.clear();
    calendar.setTime(date);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    return (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) ? 1 : 0;
  }

  private long nextDayTimestamp(long timestamp) {
    Date date = new Date(timestamp * 1000L); // 秒时间戳转换成毫秒
    calendar.clear();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    return calendar.getTimeInMillis() / 1000;
  }

  private void getKLineDayHour(CKData tickData, CKData kBuff, KParamStruct param,
      String info, KLineCallback callback, Pointer any)
      throws CloneNotSupportedException {

    int tickMod = toTodySeconds(tickData.TimeStamp); // 是每天的第 N 秒
    int kMod = toTodySeconds(kBuff.TimeStamp);       // 是每天的第 N 秒
    int isValidTick = 0;   // 是否有效时间内的 Tick, 如: 09:30 ~ 11:30, 13:00 ~ 15:00
    int isEndTick = 0;     // 是否交易结束时间点的 Tick, 如 11:30, 15:00
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    long tickCycle, kCycle; // 当前 Tick, K 线所属周期
    int beginOffset = 0;

    // 开盘时间偏移量
    if (param.dSize > 0) {
      beginOffset = param.durations[0].begin;
    }

    // 判断当前 Tick 和 K 线所处时间段
    int begin, end, restTime;
    for (int i = 0; i < param.dSize; i++) {
      // 判断是否为交易结束时间点的 Tick ?
      if (tickMod == param.durations[i].end) {
        isEndTick = 1;
      }

      // 判断 Tick 时间是否在有效交易时段 ?
      begin = param.durations[i].begin;
      end = param.durations[i].end;

      if (tickMod >= begin && tickMod <= end) {
        isValidTick = 1;
      }

      if (i > 0) {
        // Tick 时间点已经历的小节休息时间
        if (tickMod >= begin) {
          restTime = param.durations[i].begin - param.durations[i - 1].end;
          tickBreakOffset += restTime;
        }
        // K 线时间点已经历的小节休息时间
        if (kMod >= begin) {
          restTime = param.durations[i].begin - param.durations[i - 1].end;
          kBreakOffset += restTime;
        }
      }
    }

    // 15点-16点的tick数据，是最后的结算tick
    if (kMod > param.durations[param.dSize - 1].end
        && kMod < param.durations[param.dSize - 1].end + _60min) {
      kBuff.SettlePrice = tickData.SettlePrice;
      callback(info, kBuff);
    }

    // 如果周期是 0
    if (param.cycle == _0sec) {
      kBuff.PreClosePrice = kBuff.ClosePrice; // 记录上周期收盘价
      kBuff.Volume = 0;        // 初始化量额为 0
      kBuff.Amount = 0;
      kBuff.OpenInterest_diff = 0;
      kBuff.TimeStamp = tickData.TimeStamp;
      kBuff.Msec = tickData.Msec;
      //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
      kBuff.HighPrice = kBuff.OpenPrice = kBuff.LowPrice = kBuff.ClosePrice = tickData.LatestPrice;
      _calcVol(kBuff, tickData, param.isTotalVol);
      kBuff.ClosePrice = tickData.LatestPrice;

      // 每个 Tick 都导致 K 线更新
      if (isValidTick == 1) {
        if (kBuff.PreClosePrice != 0) {
          kBuff.Inc = 100 * (tickData.LatestPrice - kBuff.PreClosePrice) / kBuff.PreClosePrice;
          kBuff.Imp = 100 * (kBuff.HighPrice - kBuff.LowPrice) / kBuff.PreClosePrice;
        }
        callback(info, kBuff);
      }
      return;
    }

    // 将交易时间拼接成连续的时间, 取周期
    tickCycle = (tickData.TimeStamp - beginOffset - tickBreakOffset) / param.cycle;

    // 收到第一个 Tick
    if (kBuff.TimeStamp == 0) {
      loadFirstKBuff(kBuff, tickData, param.cycle, tickCycle, beginOffset, tickBreakOffset);
    }

    // 将交易时间拼接成连续的时间, 取周期
    kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;

    // 周期差大于 1, 当前 Tick 之前有 K 线数据需要补
    if (tickCycle - kCycle > 1) {
      kCycle = _packKLineDayHour(tickData.TimeStamp, kBuff, param, info, callback, any);
    }

    // 周期相差 1, 表示新周期开始
    if (tickCycle - kCycle == 1) {
      // 是交易结束时间点, 归入上一周期
      if ((isEndTick == 1)) {
        tickCycle--;
      } else {
        // 将盘前搓合 Tick 归入第一个 K
        if (kMod < param.durations[0].begin && kMod >= param.durations[0].begin - _60min) {
          if (kBuff.HighPrice < tickData.LatestPrice) {
            kBuff.HighPrice = tickData.LatestPrice;
          }
          if (kBuff.LowPrice > tickData.LatestPrice) {
            kBuff.LowPrice = tickData.LatestPrice;
          }
        } else {
          kBuff.PreClosePrice = kBuff.ClosePrice; // 记录上周期收盘价
          kBuff.Volume = 0;        // 初始化量额为 0
          kBuff.Amount = 0;
          kBuff.OpenInterest_diff = 0;
          kBuff.HighPrice = kBuff.OpenPrice = kBuff.LowPrice = kBuff.ClosePrice = tickData.LatestPrice;
        }
        // 创建新 K 线
        kBuff.TimeStamp = param.cycle * tickCycle + beginOffset + tickBreakOffset;
        //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
        _calcVol(kBuff, tickData, param.isTotalVol);
      }
    }

    // 是同一周期的 Tick
    if (tickCycle == kCycle) {
      // 更新 K 线最高价, 最低价
      if (kBuff.HighPrice < tickData.LatestPrice) {
        kBuff.HighPrice = tickData.LatestPrice;
      }

      if (kBuff.LowPrice > tickData.LatestPrice) {
        kBuff.LowPrice = tickData.LatestPrice;
      }
      _calcVol(kBuff, tickData, param.isTotalVol);
    }

    kBuff.ClosePrice = tickData.LatestPrice;

    // 每个 Tick 都导致 K 线更新
    if (isValidTick == 1) {
      if (kBuff.PreClosePrice != 0) {
        kBuff.Inc = 100 * (tickData.LatestPrice - kBuff.PreClosePrice) / kBuff.PreClosePrice;
        kBuff.Imp = 100 * (kBuff.HighPrice - kBuff.LowPrice) / kBuff.PreClosePrice;
      }
      callback(info, kBuff);
    }
    return;

  }

  private void getKLineDay(CKData tickData, CKData kBuff, KParamStruct param,
      String info, KLineCallback callback, Pointer any)
      throws CloneNotSupportedException {

    int tickMod = toTodySeconds(tickData.TimeStamp); // 是每天的第 N 秒
    int kMod = toTodySeconds(kBuff.TimeStamp);       // 是每天的第 N 秒
    int isValidTick = 0;   // 是否有效时间内的 Tick, 如: 09:30 ~ 11:30, 13:00 ~ 15:00
    int isEndTick = 0;     // 是否交易结束时间点的 Tick, 如 11:30, 15:00
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    long tickCycle, kCycle; // 当前 Tick, K 线所属周期
    int beginOffset = 0;

    // 开盘时间偏移量
    if (param.dSize > 0) {
      beginOffset = param.durations[0].begin;
    }

    // 判断当前 Tick 和 K 线所处时间段
    int begin, end, restTime;
    for (int i = 0; i < param.dSize; i++) {
      // 判断是否为交易结束时间点的 Tick ?
      if (tickMod == param.durations[i].end) {
        isEndTick = 1;
      }

      // 判断 Tick 时间是否在有效交易时段 ?
      begin = param.durations[i].begin;
      end = param.durations[i].end;

      if (tickMod >= begin && tickMod <= end) {
        isValidTick = 1;
      }

      if (i > 0) {
        // Tick 时间点已经历的小节休息时间
        if (tickMod >= begin) {
          restTime = param.durations[i].begin - param.durations[i - 1].end;
          tickBreakOffset += restTime;
        }
        // K 线时间点已经历的小节休息时间
        if (kMod >= begin) {
          restTime = param.durations[i].begin - param.durations[i - 1].end;
          kBreakOffset += restTime;
        }
      }
    }

    if (kMod > param.durations[param.dSize - 1].end
        && kMod < param.durations[param.dSize - 1].end + _60min) {
      kBuff.SettlePrice = tickData.SettlePrice;
      callback(info, kBuff);
    }

    // 如果周期是 0
    if (param.cycle == 0) {
      kBuff.PreClosePrice = kBuff.ClosePrice; // 记录上周期收盘价
      kBuff.Volume = 0;        // 初始化量额为 0
      kBuff.Amount = 0;
      kBuff.OpenInterest_diff = 0;
      kBuff.TimeStamp = tickData.TimeStamp;
      kBuff.Msec = tickData.Msec;
      //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
      kBuff.HighPrice = kBuff.OpenPrice = kBuff.LowPrice = kBuff.ClosePrice = tickData.LatestPrice;
      _calcVol(kBuff, tickData, param.isTotalVol);
      kBuff.ClosePrice = tickData.LatestPrice;

      // 每个 Tick 都导致 K 线更新
      if (isValidTick == 1) { // 0是false, 1是true
        if (kBuff.PreClosePrice != 0) {
          kBuff.Inc = 100 * (tickData.LatestPrice - kBuff.PreClosePrice) / kBuff.PreClosePrice;
          kBuff.Imp = 100 * (kBuff.HighPrice - kBuff.LowPrice) / kBuff.PreClosePrice;
        }
        callback(info, kBuff);
      }
      return;
    }

    // 将交易时间拼接成连续的时间, 取周期
    tickCycle = (tickData.TimeStamp - beginOffset - tickBreakOffset) / param.cycle;

    // 收到第一个 Tick
    if (kBuff.TimeStamp == 0) {
      loadFirstKBuff(kBuff, tickData, param.cycle, tickCycle, beginOffset, tickBreakOffset);
    }

    // 将交易时间拼接成连续的时间, 取周期
    kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;

    // 周期差大于 1, 当前 Tick 之前有 K 线数据需要补
    if (tickCycle - kCycle > 1) {
      kCycle = _packKLineDayHour(tickData.TimeStamp, kBuff, param, info, callback, any);
    }

    // 周期相差 1, 表示新周期开始
    if (tickCycle - kCycle == 1) {
      // 是交易结束时间点, 归入上一周期
      if (isEndTick == 1) {
        tickCycle--;
      } else {
        // 将盘前搓合 Tick 归入第一个 K
        if (kMod < param.durations[0].begin && kMod >= param.durations[0].begin - _60min) {
          if (kBuff.HighPrice < tickData.LatestPrice) {
            kBuff.HighPrice = tickData.LatestPrice;
          }
          if (kBuff.LowPrice > tickData.LatestPrice) {
            kBuff.LowPrice = tickData.LatestPrice;
          }
        } else {
          kBuff.PreClosePrice = kBuff.ClosePrice; // 记录上周期收盘价
          kBuff.Volume = 0;        // 初始化量额为 0
          kBuff.Amount = 0;
          kBuff.OpenInterest_diff = 0;
          kBuff.HighPrice = kBuff.OpenPrice = kBuff.LowPrice = kBuff.ClosePrice = tickData.LatestPrice;
        }
        // 创建新 K 线
        kBuff.TimeStamp = param.cycle * tickCycle + beginOffset + tickBreakOffset;
        //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
        _calcVol(kBuff, tickData, param.isTotalVol);
      }
    }

    // 是同一周期的 Tick
    if (tickCycle == kCycle) {
      // 更新 K 线最高价, 最低价
      if (kBuff.HighPrice < tickData.LatestPrice) {
        kBuff.HighPrice = tickData.LatestPrice;
      }
      if (kBuff.LowPrice > tickData.LatestPrice) {
        kBuff.LowPrice = tickData.LatestPrice;
      }
      _calcVol(kBuff, tickData, param.isTotalVol);
    }

    kBuff.ClosePrice = tickData.LatestPrice;

    // 每个 Tick 都导致 K 线更新
    if (isValidTick == 1) {
      if (kBuff.PreClosePrice != 0) {
        kBuff.Inc = 100 * (tickData.LatestPrice - kBuff.PreClosePrice) / kBuff.PreClosePrice;
        kBuff.Imp = 100 * (kBuff.HighPrice - kBuff.LowPrice) / kBuff.PreClosePrice;
      }
      callback(info, kBuff);
    }
    return;

  }

  private long _packKLineDayHour(long endTime, CKData kBuff, KParamStruct param,
      String info, KLineCallback callback, Pointer any)
      throws CloneNotSupportedException {

    int isCloseTime, nDay, isEndCycle, isValidK;
    long nextDayBegin, nextBeginTime, openTime, tickCycle, kCycle;
    // struct tm ttime;
    int beginOffset = 0;    // 开盘时间到 00:00:00 的偏移量(秒)
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int isValidTick = 0;    // 是否有效时间内的 Tick, 如: 09:30 ~ 11:30, 13:00 ~ 15:00
    int isEndTick = 0;      // 是否交易结束时间点的 Tick, 如 11:30, 15:00
    int tickMod = toTodySeconds(endTime); // Tick 是每天的第 N 秒
    int kMod = toTodySeconds(kBuff.TimeStamp); // K 是每天的第 N 秒

    // 开盘时间偏移量
    if (param.dSize > 0) {
      beginOffset = param.durations[0].begin;
    }

    // 判断当前 Tick 和 K 线所经历的小节时间
    int begin, end, restTime;
    for (int i = 0; i < param.dSize; i++) {
      begin = param.durations[i].begin;
      end = param.durations[i].end;

      if (i > 0) {
        // Tick 时间点已经历的小节休息时间
        if (tickMod >= begin) {
          restTime = param.durations[i].begin - param.durations[i - 1].end;
          tickBreakOffset += restTime;
        }
        // K 线时间点已经历的小节休息时间
        if (kMod >= begin) {
          restTime = param.durations[i].begin - param.durations[i - 1].end;
          kBreakOffset += restTime;
        }
      }
    }

    kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
    tickCycle = (endTime - beginOffset - tickBreakOffset) / param.cycle;

    while (tickCycle - kCycle > 1) {
      nDay = _daySinceEpoch(kBuff.TimeStamp, ZONE); // 位于 1970-01-01 以来第 N 天
      kMod = toTodySeconds(kBuff.TimeStamp);
      kBreakOffset = 0;
      // 判断当前 Tick 和 K 线所处时间段
      for (int i = 0; i < param.dSize; i++) {
        begin = param.durations[i].begin;

        if (i > 0) {
          // K 线时间点已经历的小节休息时间
          if (kMod >= begin) {
            restTime = param.durations[i].begin - param.durations[i - 1].end;
            kBreakOffset += restTime;
          }
        }
      }

      /* 判断补 K 是否周六, 周日, 或法定假日 */
      /*localtime_s(&ttime, &kBuff.Timestamp);
      ttime.tm_hour = ttime.tm_min = ttime.tm_sec = 0;
      nextDayBegin = mktime(&ttime) + _1day;*/
      nextDayBegin = nextDayTimestamp(kBuff.TimeStamp);
      nextBeginTime = nextDayBegin + param.durations[0].begin;
      if (_isDayOff(kBuff.TimeStamp, param.holidaysTS, param.hSize, ZONE) == 1) {
        // 跳到下一日开盘时间, 如果大于等于 endTime, 则取 endTime 前一周期起始
        if ((nextBeginTime - beginOffset - kBreakOffset) / param.cycle
            < (endTime - beginOffset - kBreakOffset) / param.cycle) {
          kBuff.TimeStamp = nextBeginTime;
        } else {
          kBuff.TimeStamp = ((endTime / param.cycle) - 1) * param.cycle;
        }

        kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
        //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
        continue;
      }

      /* 判断补 K 时间是否在有效交易时间区间 */
      isValidK = 0;
      isCloseTime = 1;
      for (int i = 0; i < param.dSize; i++) {
        if (toTodySeconds(kBuff.TimeStamp) >= param.durations[i].begin
            && toTodySeconds(kBuff.TimeStamp) <= param.durations[i].end) {
          isValidK = 1;
          break;
        }
        if (toTodySeconds(kBuff.TimeStamp) < param.durations[i].begin) {
          // K 线时间戳处于盘前的非交易时段, 跳到当天下一段交易起始时间; 如果大于等于 endTime, 则取 endTime前一周期
          openTime = nextDayBegin - _1day + param.durations[i].begin;
          if (((openTime - beginOffset - kBreakOffset) / param.cycle) < (
              (endTime - beginOffset - kBreakOffset) / param.cycle)) {
            kBuff.TimeStamp = openTime;
          } else {
            kBuff.TimeStamp = ((endTime / param.cycle) - 1) * param.cycle;
          }

          kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
          //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
          isCloseTime = 0;
          break;
        }
      }

      if (!(isValidK == 1)) {
        // K 线时间戳处于收盘后的非交易时段, 跳到下一交易日开盘时间, 如果大于等于 endTime, 则取 endTime前一周期
        if (isCloseTime == 1) {
          openTime = nextDayBegin + param.durations[0].begin;
          if ((openTime - beginOffset - kBreakOffset) / param.cycle < (
              (endTime - beginOffset - kBreakOffset) / param.cycle)) {
            kBuff.TimeStamp = openTime; // 第二天的开始交易时间点
          } else {
            kBuff.TimeStamp = ((endTime / param.cycle) - 1) * param.cycle;
          }
          kCycle = (kBuff.TimeStamp - beginOffset - kBreakOffset) / param.cycle;
          //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);
        }
        continue;
      }

      // 向前推进一周期
      kCycle++;
      kBuff.TimeStamp += param.cycle;
      //_ts2dateTime(kBuff.Timestamp, &kBuff.dt[0]);

      // 如果新 K 周期与交易结束时间点处于相同周期, 跳过
      kMod = toTodySeconds(kBuff.TimeStamp);   // 每天的第 N 秒
      isEndCycle = 0;
      isValidK = 0;
      for (int i = 0; i < param.dSize; i++) {
        // 检查是否为交易结束时间点的 K ?
        if (kMod == param.durations[i].end) {
          isEndCycle = 1;
          break;
        }
        // 检查 K 时间是否在有效范围 ?
        if (kMod >= param.durations[i].begin && kMod <= param.durations[i].end) {
          isValidK = 1;
        }
      }
      if ((isEndCycle == 1) || !(isValidK == 1)) {
        continue;
      }

      kBuff.Volume = kBuff.Inc = kBuff.Imp = 0;
      kBuff.Amount = 0;
      kBuff.OpenInterest_diff = 0;
      callback(info, kBuff);
    }
    return kCycle; // 补完后 K 线所处周期


  }

  int _totalRestTime(long ts, int beginOffset, TDurationStruct[] duration, int size) {
    int begin, end, restTime, pos, totalRestTime = 0;
    int mod = toTodySeconds(ts);

    for (int i = 0; i < size; i++) {
      // 判断 Tick 时间是否在有效交易时段 ?
      if (duration[i].begin < 0) {
        begin = (_1day + duration[i].begin) % _1day;
      } else {
        begin = duration[i].begin;
      }

      if (duration[i].end < 0) {
        end = (_1day + duration[i].end) % _1day;
      } else {
        end = duration[i].end;
      }

      if (i == 0) {
        continue;
      }
      // Tick 时间点已经历的小节休息时间
      pos = (mod - beginOffset) % _1day;
      if (pos >= begin - beginOffset && pos <= (_1day + duration[0].begin - _60min) % _1day) {
        restTime = duration[i].begin - duration[i - 1].end;
        if (restTime < _60min * 3) {
          totalRestTime += restTime;
        }
      }
    }
    return totalRestTime;
  }

  private int _daySinceEpoch(long timestamp, int zone) {
    return (int) ((timestamp + zone * _60min) / _1day);
  }

  private void _calcVol(CKData kd, CKData tickData, int isTotalVol) {
    if (!(isTotalVol == 1)) {
      kd.Volume += tickData.Volume;
      kd.Amount += tickData.Amount;
      // native接口中没有的，加上
      kd.OpenInterest = tickData.OpenInterest;
      kd.OpenInterest_diff += tickData.OpenInterest_diff;
      kd.Volume_Sell = tickData.Volume_Sell;
      kd.AvgPrice = tickData.AvgPrice;
    } else {
      // 暂时没有
      kd.Volume += tickData.Volume - kd.PreVolume;
      kd.Amount += tickData.Amount - kd.PreAmount;
      kd.PreAmount = tickData.Amount;
      kd.PreVolume = tickData.Volume;
    }
  }

  // 时间戳转成今天的多少秒
  @VisibleForTesting
  int toTodySeconds(long timeStamp) {
    calendar.clear();
    calendar.setTime(new Date(timeStamp * 1000));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);

    return (int) (timeStamp - (calendar.getTimeInMillis() / 1000));
  }

  /******************************end******************************************************/

  private void callback(String symbolWithCycle, CKData kBuff)
      throws CloneNotSupportedException {

    if (this.mergedKlineSet == null) {
      logger.info("mergedKlineSet may not have been initialized");
      return;
    }
    KData lastKLine = mergedKlineSet.get(symbolWithCycle);

    if ((lastKLine != null && lastKLine.getUtime(false) != kBuff.TimeStamp
        && lastKLine.getMillisec() != kBuff.TimeStamp)) {
      try {
        storeToCycleTable(symbolWithCycle, lastKLine);
      } catch (IOException e) {
        logger.error(e);
      }
    }

    KData updateKline = kBuff;
    updateKline = CKData.updateKline(updateKline, kBuff); // 结构体的属性更新到新k线中去
    mergedKlineSet.put(symbolWithCycle, updateKline);
  }


  // 调用c++接口，实现回调函数, 现在废弃了
  public interface Function extends Library {


    //加载本地动态库
    Function instance = (Function) Native.loadLibrary(NATIVE_DLL, Function.class);

    // native 方法接口
    /*void _createKLine(TickDataStructBak.ByReference tickData, KLineStructBak.ByReference kLine,
        KParamStruct.ByReference kParam, String info, KLineCallback callback, Pointer any);

    void _packKLine(long endTime, KLineStructBak.ByReference kLine,
        KParamStruct.ByReference kParam, String info, KLineCallback callback, Pointer any);
*/
    long _dateTime2ts(byte[] date);

    double _xDelta(double a, double b);

    double _maxVal(Pointer p, int size, MaxValCallback maxValCallback);

  }

  // 简单的回调，用来测试
  public static class MaxValCallback implements Callback {

    public void function(Pointer p) {
      // do nothing
      System.out.println("callback");
    }
  }

  // 生成k线函数回调
  // useless
  public static class KLineCallback implements Callback {

   /* public void function(String symbolWithCycle, KLineStructBak.ByReference kLine, Pointer any)
        throws Exception {

      // 这里第一个参数就应该是保存k线的数组，但是暂时没找到方法将java集合隐射成c++的对象
      // 暂时先这样获取k线集合
      // TODO(TWang): 这个做法不好，高度耦合了
      *//*Map<String, KData> mergedKlineSet = null;// = getMergedKlineSet();

      if(mergedKlineSet == null){
        logger.info("mergedKlineSet may not have been initialized");
        return;
      }
      KData lastKLine = mergedKlineSet.get(symbolWithCycle);

      KData newKLine = KLineStruct.createNewKline(lastKLine, kLine);

      mergedKlineSet.put(symbolWithCycle, newKLine); // 这里回调卡住？

      String rowkey = newKLine.getSymbol() + "_" + newKLine.getUtime(false);
      logger.debug(String.format("merge to new kLine, rowkey: %s", rowkey));*//*
    }*/
  }

}
