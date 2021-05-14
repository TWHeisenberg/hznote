#ifndef ALGO_H
#define ALGO_H

#ifdef _MSC_VER
#define DLL_EXPORT __declspec( dllexport )
#else
#define DLL_EXPORT
#endif

#ifdef __cplusplus
extern "C" {
#endif

#include "time.h"

#define ZONE +8 // 时区
#define ZONE_SECS (3600 * ZONE) // 时区差 +08

    typedef long long int64;

    typedef enum K_CYCLES {
        _0sec = 0,
        _1sec,
        _2sec,
        _3sec,
        _5sec  = 5, // 秒
        _10sec = 10,
        _15sec = 15,
        _30sec = 30,
        _1min  = 60,
        _5min  = 300,
        _15min = 900,
        _30min = 1800,
        _60min = 3600,
        _1day  = 86400,
        _1week = 604800,
        _1month,

    } K_CYCLES;

    // 交易起止时间点, 每日中的第 N 秒
    typedef enum TIME_POINT {
        T2100 = -10800,// 昨日 21:00
        T2300 = -3600, // 昨日 23:00
        T2330 = -1800, // 昨日 23:30
        T0100 = 3600,  // 01:00:00
        T0230 = 9000,  // 02:30:00
        T0900 = 32400, // 09:00:00
        T0915 = 33300, // 09:15:00
        T0930 = 34200,
        T1015 = 36900,
        T1030 = 37800,
        T1130 = 41400,
        T1300 = 46800,
        T1330 = 48600,
        T1500 = 54000,
        T1515 = 54900,
    } TIME_POINT;

    // 交易时段
    typedef struct TDuration {
        TIME_POINT begin;   // 交易起始时间
        TIME_POINT end;     // 交易终止时间
    } TDuration;

    // K线数据
    typedef  struct KLine {
        int Msec;           // 毫秒
        int64 Amount;       // 成交量
        int64 PreAmount;    // 前Tick总成交量
        int64 Timestamp;    // 时间戳
        double Volume;      // 成交金额
        double PreVolume;   // 前Tick总成交金额
        double OpenPrice;   // 开盘价
        double HighPrice;   // 最高价
        double LowPrice;    // 最低价
        double ClosePrice;  // 收盘价
        double PreClosePrice;// 前周期收盘价
        double SettlePrice; // 结算价
        double Inc;         // 涨幅[%]
        double Imp;         // 振幅[%]
        //char dt[20];
    } KLine;

    typedef struct KParam {
        int isNight;        // 是否夜盘
        int dSize;          // 有效交易时间点数组大小
        int hSize;          // 法定交易日数组大小
        int isTotalVol;     // Tick 数据中交易量, 交易额是差值还是总值 [0差值|1总值]
        K_CYCLES cycle;     // K 线周期
        TDuration* duration;// 有效交易时间点数组指针, 如: { {T0930, T1130}, {T1300, T1500} }
        int* holidaysTS;    // 回溯区间内所有法定假日(非交易日) 时间戳, 可以为 NULL, 表示无法定追
    } KParam;

    // Tick 信息
    typedef struct TickData {
        int Msec;           // 毫秒
        int64 Amount;       // 交易量
        int64 Timestamp;    // 时间戳
        double LatestPrice; // 最新价
        double SettlePrice; // 结算价
        double Volume;      // 交易额
        char date[20];
    } TickData;


    // 权值结构体
    typedef struct WeightValue {
        double value;       // 值
        double weight;      // 权重
    } WeightValue;

    // 直线方程 ax + b
    typedef struct LinearEquation {
        double a;
        double b;
    } LinearEquation;

    // 点 (x, y)
    typedef struct Point {
        double x;
        double y;
    } Point;

    typedef double(*getValue) (void* arr, int n);
    typedef void(*getDate) (void* arr, int n, char*);
    typedef int64(*getTime) (void* arr, int n);
    typedef void(*KLineCallback) (void* arr, KLine, void*);


    /* 时间差值, 时间戳 公式, |时间戳1 - 时间戳2|
     * @param: a    时间戳1
     * @param: b    时间戳2
     * @return: double
     **/
    DLL_EXPORT double _xDelta(double a, double b);

    /* 空间(价格)关系公式: 名称1 [ /|- ] 名称2
     * @param: a        价格1
     * @param: b        价格2
     * @return: double
     **/
    DLL_EXPORT double _yDelta(double a, double b, char symbol);

    /* 速度差值公式: (名称1 - standard) / 名称2
     * @param: a            值1
     * @param: b            值2
     * @param: standard     标准值
     * @return: double
     **/
    DLL_EXPORT double _vDelta(double a, double b, int standard);

    /* 符合度公式: (名称1 / 参考值 - 1)
     * @param: a            值1
     * @param: refrence     参考值
     * @return: double
     **/
    DLL_EXPORT double _conformance(double a, double refrence);

    /* 相对符合度公式: 100% - (名称1 / 参考值 - 1) * 应用系数
     * @param: a            值1
     * @param: reference    参考值
     * @param: ratio        应用系数
     * @return: double
     **/
    DLL_EXPORT double _relConformance(double a, double reference, double ratio);

    /* 绝对符合度公式: 100% - |(名称1 / 参考值 - 1) * 应用系数|
     * @param: a            值1
     * @param: reference    参考值
     * @param: ratio        应用系数
     * @return: double
     **/
    DLL_EXPORT double _absConformance(double a, double reference, double ratio);

    /*
     * 乘权累加: (名称1 * 权重1 + ... + 名称N * 权重N) / (权重1 + ... + 权重N)
     * @param: weightPairs     值-权数组
     * @return: double
     **/
    DLL_EXPORT double _multiAcc(WeightValue* arr, int size);

    /*
     * 求平均值:
     * @param: arr     任意类型数组
     * @param: size    数组大小
     * @param: get_v   回调函数, 返回该类型第 N 个元素内的某个数值
     * @return: double
     **/
    DLL_EXPORT double _average(void* arr, int size, getValue get_v);

    /*
     * 移动均值: (设定权重 * 基准值 + (设定周期数 - 设定权重) * 前1周期移动均值) / 设定周期数
     * @param: size         基准值数组大小
     * @param: value        最新一个基准值
     * @param: weight       设定权重
     * @param: cycle        设定的周期数(滑动框的大小)
     * @param: preCycleAvg  上个周期移动平均值, 如果上个周期移动平均值, 给 0
     * @return: double
     **/
    DLL_EXPORT double _movingAvgWithWeight(int size, double value, double weight, int cycle, double preCycleAvg);

    /*
     * 算术平均:  MA(N)=( 基准值n+ 基准值(n-1) + … +基准值（n-N+1） ) / 数据量N
     * @param: arr     任意类型数组
     * @param: size    数组大小, 大于 1
     * @param: cycle   设定的周期数(滑动框的大小)
     * @param: sum     记录每周期的和, 初始可设置 0
     * @param: get_v   回调函数, 返回该类型第 N 个元素内的某个数值
     * @return: 最新一个算术平均值
     *
     * Example1:
     * 求窗大小为 5 的滑动平均数组
     * ------------------------
     * // 定义回调函数
     * inline double func(void* arr, int n)
     * {
     *     double* y = (double*)arr;
     *     return y[n];
     * }
     *
     * int main()
     * {
     *     const int cycle = 5;
     *     double sum = 0;
     *     double arr[10] = {1.2, 2.2, 1.4, 2.1, 3.4, 4.5, 3.2, 3.3, 2.1, 2.8};
     *     double avg[10]; // 输出, 滑动平均数组
     *     for (int i = 0; i < 10; i++) {
     *         avg[i] = _MA(arr, i + 1, cycle, &sum, func);
     *     }
     *     return 0;
     * }
     *
     * Example2:
     * 求周期为 [5,3,3] 滑动平均数组
     * ----------------------------------
     * const int cycleCount = 3;
     * double arr[10] = {1.2, 2.2, 1.4, 2.1, 3.4, 4.5, 3.2, 3.3, 2.1, 2.8};
     * int cycles[cycleCount] = {5,3,3};
     *
     * // 定义一个大小为 3 x 10 的二维数组, 用来保存不同周期的均值数组
     * double** resultArr = (double**)calloc(cycleCount, sizeof(double));
     * for (int i = 0; i < cycleCount; i++)
     *     resultArr[i] = (double*)calloc(10, sizeof(double));
     *
     * // 周期之和数组, 用来保存和, 减少计算量
     * double* sumArr = (double*)calloc(cycleCount, sizeof(double));
     *
     * for (int i = 0; i < 10; i++) {
     *     resultArr[0][i] = _MA(arr, i + 1, cycles[0] , &sumArr[0], get_v);
     *     for (int c = 1; c < cycleCount; c++) {
     *         resultArr[c][i] = _MA(resultArr[c - 1], i + 1, cycles[c], &sumArr[c], func);
     * }
     *
     * free(sumArr);
     * for (int i = 0; i < 3; i++)
     *     free(resultArr[i]);
     * free(resultArr);
     **/
    DLL_EXPORT double _MA(void* arr, int size, int cycle, double* sum, getValue get_v);

    /*
     * 指数平滑移动均值: (2 * 基准值 + (设定周期数 - 1) * 前1周期指数平滑移动均值) / (设定周期数 + 1)
     * @param: size         基准值数组大小
     * @param: value        最新一个基准值
     * @param: cycle        设定周期数(滑动框的大小)
     * @param: preCycleAvg  上个周期移动平均值, 如果上个周期移动平均值, 给 0
     * @return: double
     **/
    DLL_EXPORT double _indexMovingAvg(int size, double value, int cycle, double preCycleAvg);

    /*
     * 加权移动均值: (N * 前0基准值 + (N-1)*前1基准值 + (N-2)*前2基准值 + ... + 1 * 前N基准值) / (N + (N - 1) + (N - 2) + ... + 1)
     * @param: arr      任意类型数组首地址
     * @param: size     数组大小
     * @param: cycle    设定周期数(滑动框的大小)
     * @param: get_v    回调函数, 返回该类型数组第 N 个元素内的某个数值
     * @return: double
     **/
    DLL_EXPORT double _weightMovingAvg(void* arr, int size, int cycle, getValue get_v);

    /*
     * 标准差: sqrt(设定周期(N)内的 各个 基准值偏离值的平方(基准价偏离值1^2 + 基准价偏离值2 ^2+ ... + 基准价偏离值N^2 (N为同节点的设定周期数) )之和/设定周期(N))
     * @param: arr      任意类型数组首地址
     * @param: size     数组大小
     * @param: cycle    设定周期数(滑动框的大小)
     * @param: SSTD     0 标准差, 1 样本标准差
     * @param: get_v    回调函数, 返回该类型数组第 N 个元素内的某个数值
     * @return: double
     **/
    DLL_EXPORT double _standardDeviation(void* arr, int size, int cycle, int SSTD, getValue get_v);

    /*
     * 通道平行线 高/低价画线: 数组头尾高/低点价画直线
     * @param: arr      输入参数, 数组首地址
     * @param: size     数组大小
     * @param: get_h    回调函数, 返回该类型数组第 N 个元素内的某个数值(画直线的点), 如最高价
     * @param: get_l    回调函数, 返回该类型数组第 N 个元素内的某个数值, 如最低价
     * @param: width    输出. 第一高/低点价与最终高/低点价间的距离
     * @param: height   输出. 与直线差值最大的低/高价点, 点到直线的最短(垂直)距离
     * @param: delta    输出. 与直线差值最大的低/高价点, 点到直线的距离(y轴方向)
     * @return: double
     *
     * Example:
     * -------------------------------
     * typedef struct KLineHL {
     *     double high;
     *     double low;
     * } KLineHL;
     *
     * inline double get_h(void* arr, int n)
     * {
     *     KLineHL* y = (KLineHL*)arr;
     *     return y[n].high;
     * }
     *
     * inline double get_l(void* arr, int n)
     * {
     *     KLineHL* y = (KLineHL*)arr;
     *     return y[n].low;
     * }
     *
     * int main()
     * {
     *     KLineHL* arr = (KLineHL*)malloc(sizeof(KLineHL) * 10);
     *     arr[0] = { 6850.2, 6800.2 };
     *     arr[1] = { 6840.2, 6810.2 };
     *     arr[2] = { 6830.2, 6820.2 };
     *     arr[3] = { 6820.2, 6800.2 };
     *     arr[4] = { 6810.2, 6805.2 };
     *     arr[5] = { 6790.2, 6780.2 };
     *     arr[6] = { 6780.2, 6750.2 };
     *     arr[7] = { 6790.2, 6785.2 };
     *     arr[8] = { 6800.2, 6782.2 };
     *     arr[9] = { 6810.2, 6800.2 };
     *
     *     double width, height, delta;
     *     _paralleLines(arr, 10, get_h, get_l, &width, &height, &delta);
     *     free(arr);
     *     return 0;
     * }
     **/
    DLL_EXPORT void _paralleLines(void* arr, int size, getValue get_h, getValue get_l, double* width, double* height, double* delta);

    /*
     * 求时间区间内最大基准值
     * @param: arr      任意类型数组首地址, 需按时间升序排列
     * @param: size     数组大小
     * @param: begin    起始毫秒时间戳
     * @param: end      终止毫秒时间戳(包含), 须大于 begin
     * @param: get_t    回调函数, 用来获取该类型数组第 N 个元素内的毫秒时间戳
     * @param: get_v    回调函数, 用来获取该类型数组第 N 个元素内的某个数值
     * @return: int     最大值的下标, -1 表示没有找到
     **/
    DLL_EXPORT int _timeRangeMax(void* arr, int size, int64 begin, int64 end, getTime get_t, getValue get_v);

    /*
     * 求时间区间内最小基准值
     * @param: arr      任意毫秒类型数组首地址, 需按时间升序排列
     * @param: size     数组大小
     * @param: begin    起始毫秒时间戳
     * @param: end      终止毫秒时间戳(包含), 须大于 begin
     * @param: get_t    回调函数, 用来获取该类型数组第 N 个元素内的毫秒时间戳
     * @param: get_v    回调函数, 用来获取该类型数组第 N 个元素内的某个数值
     * @return: int     最小值的下标, -1 表示没有找到
     **/
    DLL_EXPORT int _timeRangeMin(void* arr, int size, int64 begin, int64 end, getTime get_t, getValue get_v);

    /*
     * 求时间区间内基准平均值
     * @param: arr      任意类型数组首地址, 需按时间升序排列
     * @param: size     数组大小
     * @param: begin    起始毫秒时间戳
     * @param: end      终止毫秒时间戳(包含), 须大于 begin
     * @param: get_t    回调函数, 用来获取该类型数组第 N 个元素内的毫秒时间戳
     * @param: get_v    回调函数, 用来获取该类型数组第 N 个元素内的某个数值
     * @return: double  平均基准值
     **/
    DLL_EXPORT double _timeRangeAvg(void* arr, int size, int64 begin, int64 end, getTime get_t, getValue get_v);

    /*
     * 求时间区间内基准值个数
     * @param: arr      任意类型数组首地址, 需按时间升序排列
     * @param: size     数组大小
     * @param: begin    起始毫秒时间戳
     * @param: end      终止毫秒时间戳(包含), 须大于 begin
     * @param: get_t    回调函数, 用来获取该类型数组第 N 个元素内的毫秒时间戳
     * @return: int     基准值个数
     **/
    DLL_EXPORT int    _timeRangeCount(void* arr, int size, int64 begin, int64 end, getTime get_t);

    /*
    * 求和
    * @param: arr       任意类型数组指针
    * @param: size      数组大小
    * @param: begin     起始毫秒时间戳
    * @param: end       终止毫秒时间戳(包含), 须大于 begin
    * @param: get_t     回调函数, 用来获取该类型数组第 N 个元素内的毫秒时间戳
    * @param: get_v     回调函数, 用来获取该类型数组第 N 个元素内的某个数值
    * @return:  double  数组之合
    */
    DLL_EXPORT double _timeRangeSum(void* arr, int size, int64 begin, int64 end, getTime get_t, getValue get_v);

    /* 过两点求直线方程
     * @param: p1     点1
     * @param: p2     点2
     * @param: line   输出. 含直线方程 a, b 的值.
     *                如果 a,b 是 nan, 则直线平行于 y 轴
     **/
    DLL_EXPORT void   _linearEqua(Point p1, Point p2, LinearEquation* line);

    /* 求过两点的直线, 再求数组中的点到直线距离最远的点下标
     * @param: p1     点1
     * @param: p2     点2
     * @param: arr    任意类型数组首地址
     * @param: size   数组大小
     * @param: x0     数组第一个 x 值
     * @param: position  [-1|0|1]; 0 任意方位距离最大的点; 1 直线上方距离最大的点; -1 直线下方距离最大的点
     * @param: get_v  回调函数, 用来获取该类型数组第 N 个元素内的某个数值
     * @return: 距过p1, p2直线最远点的下标, 如果返回 -1 则表示不存在
     **/
    DLL_EXPORT int    _maxDistance2Line(Point p1, Point p2, void* arr, int size, double x0, int position, getValue get_v);

    /*
     * 已知直线 ax + b, 求过 p 点的平行直线 ax + b1, (两条直线的 a 必相等)
     * @param: p        点 p
     * @param: a        已知直线方程的 a 值
     * @return: double  过点 p 且与直线 ax + b 平行的直线的 b1 的值
     **/
    DLL_EXPORT double _paralleLine(Point p, double a);

    /*
     * 已知三点 p1, p2, p3, 求过 p1, p2 的直线, 和过 p3 且与过p1, p2直线平行的直线(斜率 a 必相等)
     * @param: p1     点 p1
     * @param: p2     点 p2
     * @param: p3     点 p3
     * @param: line1  输出. 含直线方程 a, b 的值
     * @param: line2  输出. 含平行直线方程 a, b1 的值
     **/
    DLL_EXPORT void   _paralleLineABC(Point p1, Point p2, Point p3, LinearEquation* line1, LinearEquation* line2);

    /*
     * 求数组最小值
     * @param: arr      任意类型数组首地址
     * @param: size     数组大小
     * @param: get_v    回调函数, 用来获取该类型数组第 N 个元素内的某个数值
     **/
    DLL_EXPORT double _minVal(void* arr, int size, getValue get_v);

    /*
     * 求数组最大值
     * @param: arr      任意类型数组首地址
     * @param: size     数组大小
     * @param: get_v    回调函数, 用来获取该类型数组第 N 个元素内的某个数值
     **/
    DLL_EXPORT double _maxVal(void* arr, int size, getValue get_v);

    /*
     * 计算K线的交易量, 交易额
     * @param: kd       K 线数据
     * @param: tickData 当前 Tick 数据
     * @param: isTotalVol  [ 0|1 ]  0 表示 Tick 所带交易量, 交易额是差值; 1 表示 Tick 所带交易量, 交易额是总值
     **/
    inline void _calcVol(KLine* kd, TickData* tickData, int isTotalVol);

    /*
     * 生成法定假日(非交易日)的时间戳数组
     * @param: arr      时间字符串数组, 如 {"2021-04-04", "2021-05-01"}
     * @param: size     数组大小
     * @param: result   输出数组指针. 法定假日为 1970-01-01以来的第N天
     * @param: get_d    回调函数, 获取 arr 中各个元素
     * @param: zone     时区, 如: +8
     **/

    DLL_EXPORT void _convertNonTradingDays(void* arr, int size, int* result, getDate get_d, int zone);

    /*
     * 排序用比较函数
     * @param: p1  第一个数值指针
     * @param: p2  第二个数值指针
     * @return: 差值
     **/
    inline int _compare(const void* p1, const void* p2);

    /*
     * 时间字符串转时间戳
     * @param: dt    时间字符串, 格式: "2021-03-24"
     * @return: 时间戳
     */
    DLL_EXPORT inline int64 _date2ts(const char* dt);

    /*
     * 时间字符串转时间戳
     * @param: dt    时间字符串, 格式: "2021-03-24 09:30:00"
     * @return: 时间戳
     */
    DLL_EXPORT inline int64 _dateTime2ts(const char* dt);

    /*
     * 时间字符串转时间戳
     * @param: ts    时间戳
     * @param: rs    输出. 时间字符串, 如: "2021-03-24 09:30:00"
     */
    DLL_EXPORT inline void _ts2dateTime(int64 ts, char* rs);

    /*
    * 从 1970-01-01 以来的第N天
    * @param: ts    时间戳
    * @param: zone  时区. 中国 +8
    * @return: 第几天
    */
    inline int _daySinceEpoch(int64 ts, int64 zone);

    /*
    * 从 1970-01-01 以来的第N周
    * @param: ts    时间戳
    * @param: zone  时区. 中国 +8
    * @return: 第几周
    */
    inline int _weekSinceEpoch(int64 ts, int64 zone);

    /*
    * 从 1970-01-01 以来的第N月
    * @param: ts    时间戳
    * @return: 第几月
    */
    inline int _monthSinceEpoch(int64 ts);

    /*
    * 周期的起始时间
    * @param: ts    时间戳
    * @param: cycle 周期
    * @return: 时间戳
    */
    inline int64 _getBeginOfCycle(int64 ts, K_CYCLES cycle);

    /*
    * 日盘小时及以下周期 K 线使用此函数
    */
    void  _getKLineDayHour(TickData* tickData, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p);

    /*
    * 日盘小时及以下周期 K 线使用此函数
    */
    int64 _packKLineDayHour(int64 endTime, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p);

    /*
    * 夜盘小时及以下周期 K 线使用此函数
    */
    void  _getKLineNightHour(TickData* tickData, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p);

    /*
    * 夜盘小时及以下周期 K 线使用此函数
    */
    int64 _packKLineNightHour(int64 endTime, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p);

    /*
    * 天及以上周期使用此函数
    */
    void  _getKLineDay(TickData* tickData, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p);

    /*
    * 天及以上周期使用此函数
    */
    int64 _packKLineDay(int64 endTime, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p);

    /*
    * 根据 Tick 数据生成K线
    * @param: tickData      Tick 数据
    * @param: kBuff         K 线buffer
    * @param: param         K 线计算参数
    * @param: arr           K 线数组首地址
    * @param: callback      回调函数, 用来处理生成的 K 线数据, arr 会被传入
    * @param: p             任意指针, 会在回调函数中使用
    *
    * Example:
    * ------------------------------------------------------------
    #include "NCAlgo.h"
    #include "ncs_def.h"
    #include "nchbinterface.h"

    inline void klineCallback(void* arr, KLine kd, void* p)
    {
        std::vector<KLine>* vec = (std::vector<KLine>*)arr;
        if (vec->size() == 0 || vec->back().Timestamp != kd.Timestamp || vec->back().Msec != kd.Msec)
            vec->emplace_back(kd);
        else {
            vec->back() = kd;
        }
    }

    void readHB(int dtype, void* data, int dlen, void* u, void* m)
    {
        std::vector<ncs_kdata>* vec = (std::vector<ncs_kdata>*)u;
        ncs_kdata* kdatas = (ncs_kdata*)data;
        int size = sizeof(ncs_kdata);
        int cnt = dlen / sizeof(ncs_kdata);
        if (cnt <= 0) return;
        for (int i = 0; i < cnt; i++) {
            vec->push_back(*kdatas);
            kdatas++;
        }
    }

    void loadTick(ncs_kdata& row, TickData& tick) {
        tick.Timestamp = row.uTime;
        tick.Msec = row.iBeatCnt;
        tick.LatestPrice = row.dbClosePrice;
        tick.Amount = row.dbSum;
        tick.Volume = row.uVolume;
    }

    int main()
    {
        std::string ip_str = "10.10.0.254";
        char* ip = &ip_str[0];
        int port = 9090;
        nchb_init(ip, port);

        // ================== 夜盘 ====================
        std::string symbol_str = "au06";
        char* symbol = &symbol_str[0];
        std::vector<ncs_kdata> vec;
        int oldSize, newSize;
        char begin[20] = "2021-04-01 20:00:00"; // 夜盘起始交易时间为 21:00
        char end[20]   = "2021-04-07 18:00:00";
        int64 beginTs = _dateTime2ts(begin);
        int64 endTs   = _dateTime2ts(end);
        while (1) {
            oldSize = vec.size();
            // 读取Hbase Tick 数据
            nchb_readkd_hbase(symbol, 6, 9, 99999, beginTs, endTs, readHB, &vec);
            newSize = vec.size();
            if (oldSize == newSize) break;
            endTs = vec.back().uTime - 1;
        }

        TickData tk;
        KLine kd{ 0 };
        std::vector<KLine> klineVec;
        // 有效交易时段
        std::vector<TDuration> durVec = { {T2100, T0230}, {T0900, T1015}, {T1030, T1130}, {T1330, T1500} };

        K_CYCLES cycle = _60min;
        // 法定假日
        std::vector<std::string> holidays = { "2021-04-05" };
        std::vector<int> holidaysTSVec(holidays.size());
        // 将法定假日转为自1970-01-01以来第N天
        _convertNonTradingDays(&holidays[0], holidays.size(), &holidaysTSVec[0], get_d, 8);
        // 生成 K 线的参数
        KParam param = { 1, durVec.size(), holidays.size(), 0, cycle, &durVec[0], &holidaysTSVec[0] };
        for (auto iter = vec.rbegin(); iter != vec.rend(); iter++) {
            loadTick(*iter, tk);
            _createKLine(&tk, &kd, &param, &klineVec, klineCallbackReal, NULL);
        }

        // 必须. 因为 K 线生成是由 Tick 触发, 交易结束时间点可能无 Tick 发出(无交易量)
        // K线生成完后,必须调用 _packKLine() 再补一下到结束时间点的 K 线
        _packKLine(_dateTime2ts(end), &kd, &param, &klineVec, klineCallbackReal, NULL);

        // ================== 日盘 ====================
        symbol_str = "IC04";
        symbol = &symbol_str[0];
        vec.clear();
        oldSize = newSize = 0;
        char begin2[20] = "2021-04-01 00:00:00";
        char end2[20]   = "2021-04-07 18:00:00";
        beginTs = _dateTime2ts(begin2);
        endTs   = _dateTime2ts(end2);
        // 从Hbase读取 Tick 数据
        while (1) {
            oldSize = vec.size();
            nchb_readkd_hbase(symbol, 6, 9, 99999, beginTs, endTs, readHB, &vec);
            newSize = vec.size();
            if (oldSize == newSize) break;
            endTs = vec.back().uTime - 1;
        }

        tk = { 0 };
        kd = { 0 };
        klineVec.clear();
        durVec = { {T0930, T1130}, {T1300, T1500} };
        param = { 0, durVec.size(), holidays.size(), 0, cycle, &durVec[0], &holidaysTSVec[0] };
        for (auto iter = vec.rbegin(); iter != vec.rend(); iter++) {
            loadTick(*iter, tk);
            _createKLine(&tk, &kd, &param, &klineVec, klineCallbackReal, NULL);
        }

        // 必须. 因为 K 线生成是由 Tick 触发, 交易结束时间点可能无Tick(无交易量)
        // K线生成完后,必须再补一下到结束时间点的 K 线
        _packKLine(_dateTime2ts(end), &kd, &param, &klineVec, klineCallbackReal, NULL);
    }
    * ---------------------------------------------
    */
    DLL_EXPORT void _createKLine(TickData* tickData, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p);

    /*
    * 补足最后 K 线数据, 因为有时候最后终点时间未收到 Tick
    * @param: endTime       时间戳, 终点时间
    * @param: kBuff         _createKLine 产生的带有最后计算值的 K 线 buffer
    * @param: param         K 线计算参数
    * @param: arr           K 线数组首地址
    * @param: callback      回调函数, 用来处理生成的 K 线数据, arr 会被传入
    * @param: any           任意指针, 会作参数传入回调函数
    * Example:
    * -----------------------------------------
    * 参考 _createKLine() 例子
    */
    DLL_EXPORT void _packKLine(int64 endTime, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p);

    /*
    * 依据时间戳判断是否周末
    * @param:  ts       时间戳
    * @return: 0 否, 1 是
    */
    inline int _isWeekEnd(int64 ts);

    /*
    * 依据时间戳判断是否法定假日
    * @param:  ts       时间戳
    * @return: 0 否, 1 是
    */
    inline int _isHoliday(int64 ts, int* holidayArr, int size, int zone);

    /*
    * 依据时间戳判断是否周末或法定假日
    * @param:  ts       时间戳
    * @return: 0 否, 1 是
    */
    inline int _isDayOff(int64 ts, int* holidayArr, int size, int zone);

    /*
    * 时间戳所经历的交易盘中休息总时间
    * @param: ts        时间戳
    * @param: beginOffset  日 偏移量(秒)
    * @param: duration     每日有效交易时段
    * @param: duration 数组的大小
    * @return: int  秒
    */
    inline int _totalRestTime(int64 ts, int beginOffset, TDuration* duration, int size);

    /*
    * 二分查找是否法定假日
    * @param:  arr      法定假日数组, 已转存为是第几天
    * @param: low      搜索范围最小下标
    * @param: high     搜索范围最大下标
    * @param: target   要查找的目标(第N天)
    * @return: int     目标所在位置下标, -1 表示没找到
    */
    inline int _binSearch(int* arr, int low, int high, int target);

#ifdef __cplusplus
}
#endif

#endif
