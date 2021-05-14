package com.hznc.datacenter.bean;

import static com.hznc.datacenter.constants.Constants.*;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * 封装kParam的结构体，作调用c++的参数:
 * **************************************************************
 *     typedef struct KParam {
 *         int isNight;        // 是否夜盘
 *         int dSize;          // 有效交易时间点数组大小
 *         int hSize;          // 法定交易日数组大小
 *         int isTotalVol;     // Tick 数据中交易量, 交易额是差值还是总值 [0差值|1总值]
 *         K_CYCLES cycle;     // K 线周期
 *         TDuration* duration;// 有效交易时间点数组指针, 如: { {T0930, T1130}, {T1300, T1500} }
 *         int* holidaysTS;    // 回溯区间内所有法定假日(非交易日) 时间戳, 可以为 NULL, 表示无法定追
 *     } KParam;
 * **************************************************************
 *  @author TWang
 *  @date 2021-04-19 16:30:00
 */
public class KParamStruct extends Structure{
    public int isNight;
    public int dSize;
    public int hSize;
    public int isTotalVol;
    public int cycle;
    public TDurationStruct.ByReference[] durations = new TDurationStruct.ByReference[3];
    public int[] holidaysTS;


    @Override
    public List getFieldOrder(){
        return Arrays.asList(new String[]{"isNight", "dSize", "hSize"
                , "isTotalVol", "cycle", "duration", "holidaysTS"});
    }

    public static KParamStruct newInstance(int cycle,int isTotalVol,int isNight,int dSize,int hSize){
      KParamStruct kParamStruct = new KParamStruct();
      kParamStruct.cycle = cycle; // 合并周期k线
      kParamStruct.isTotalVol = isTotalVol; // 交易额是差值还是总值 [0差值|1总值]
      kParamStruct.isNight = isNight; // 是否夜盘 [0否|1是]
      kParamStruct.dSize = dSize; // 有效交易时间点数组大小
      kParamStruct.hSize = hSize; // 法定假日数组大小

      // 有效交易时间区间
      TDurationStruct.ByReference tdurationDayAm = new TDurationStruct.ByReference();
      TDurationStruct.ByReference tdurationDayPm = new TDurationStruct.ByReference();
      TDurationStruct.ByReference tdurationNight = new TDurationStruct.ByReference();
      tdurationNight.begin = KPARAM_TIME_T2100;// 昨日 21:00:00
      tdurationNight.end = KPARAM_TIME_T1500; // 03:00:00
      tdurationDayAm.begin = KPARAM_TIME_T0930; // 9:30:00
      tdurationDayAm.end = KPARAM_TIME_T1130; // 11:30:00
      tdurationDayPm.begin = KPARAM_TIME_T1300; // 13:00:00
      tdurationDayPm.end = KPARAM_TIME_T1500; // 15:00:00
      kParamStruct.durations[0] = tdurationDayAm; // 日盘开盘时间
      kParamStruct.durations[1] = tdurationDayPm;

      // kParamStruct.durations[3] = tdurationNight; //TODO: 夜盘 暂时不考虑吧
      // kParam.holidaysTS = 0;// TODO: 回溯取件内法定假日的时间戳，暂时不考虑
      return kParamStruct;
    }
}
