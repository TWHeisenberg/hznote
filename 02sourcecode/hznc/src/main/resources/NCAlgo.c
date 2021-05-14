#include "NCAlgo.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

double _xDelta(double a, double b)
{
    return fabs(a - b);
}

double _yDelta(double a, double b, char symbol)
{
    if (symbol == '/') {
        if (b == 0) return NAN;
        return a / b;
    }
    else
        return a - b;
}

double _vDelta(double a, double b, int standard)
{
    if (b == 0) return NAN;
    return (a - standard) / b;
}

double _conformance(double a, double reference)
{
    if (reference == 0) return NAN;
    return (a / reference - 1);
}

double _relConformance(double a, double reference, double ratio)
{
    if (reference == 0) return NAN;
    return 1 - _conformance(a, reference) * ratio / 100;
}

double _absConformance(double a, double reference, double ratio)
{
    if (reference == 0) return NAN;
    return 1 - fabs(_conformance(a, reference) * ratio) / 100;
}

double _multiAcc(WeightValue* arr, int size)
{
    double sum = 0;
    double sumWeight = 0;
    if (size == 0) return sum;

    for (int i = 0; i < size; i++) {
        sum += arr[i].value * arr[i].weight;  // 值 * 权 累加
        sumWeight += arr[i].weight;
    }
    return sum / sumWeight;
}

double _average(void* arr, int size, getValue get_v) {
    if (size == 0) return 0;
    double sum = 0;
    for (int i = 0; i < size; i++) {
        sum += get_v(arr, i); // 取得第 i 个元素内的某个数值
    }
    return sum / size;
}

double _movingAvgWithWeight(int size, double data, double weight, int cycle, double preCycleAvg)
{
    if (size == 0 || cycle == 0) return NAN;
    else if (size < cycle)
        cycle = size;
    // 根据最新的基准值求得最新一个滑动平均值
    return (weight * data + (cycle - weight) * preCycleAvg) / cycle;
}

double _MA(void* arr, int size, int cycle, double* sum, getValue get_v)
{
    if (size == 0 || cycle == 0) return NAN;
    // 周期大于等于数组大小, 只加不减
    if (size <= cycle) {
        cycle = size;
        *sum += get_v(arr, size - 1);
    }
    else {
        // 加新减旧
        *sum += get_v(arr, size - 1) - get_v(arr, size - 1 - cycle);
    }
    return *sum / cycle;
}

double _indexMovingAvg(int size, double data, int cycle, double preCycleAvg)
{
    if (size == 0 || cycle == 0)
        return NAN;
    else if (size == 1)
        return data;
    else if (size < cycle)
        cycle = size;

    return (2 * data + (cycle - 1) * preCycleAvg) / (cycle + 1);
}

double _weightMovingAvg(void* arr, int size, int cycle, getValue get_v)
{
    if (size == 0 || cycle == 0)
        return NAN;
    else if (size < cycle)
        cycle = size;

    double sum = 0;
    for (int i = 0; i < cycle; i++)
        sum += (cycle - i) * get_v(arr, size - 1 - i);

    return sum / (cycle * (cycle + 1) / 2);
}

double _standardDeviation(void* arr, int size, int cycle, int SSTD, getValue get_v)
{
    if (size == 0 || cycle == 0)
        return NAN;
    else if (size == 1)
        return get_v(arr, size - 1);
    else if (size < cycle)
        cycle = size;

    double sum = 0;
    for (int i = 0; i < cycle; i++)
        sum += get_v(arr, size - 1 - i);

    double avg = sum / cycle; // 平均值

    sum = 0;
    for (int i = 0; i < cycle; i++)
        sum += pow(get_v(arr, size - 1 - i) - avg, 2); // 求方差之和
    if (SSTD == 1)
        return sqrt(sum / (cycle - 1));
    else
        return sqrt(sum / cycle);
}

void   _paralleLines(void* arr, int size, getValue get_h, getValue get_l, double* width, double* height, double* delta)
{
    if (size == 0) return;
    int x1, x2;
    double a, b, y1, y2, y, A, B, C;
    x1 = 0;                  // 首值时间, 下标当作时间
    x2 = size - 1;           // 末值时间
    y1 = get_h(arr, x1);     // 首值高点价
    y2 = get_h(arr, x2);     // 末值高点价
    double deltaY = y1 - y2; // 直角三角形的高
    int deltaX = x2 - x1; // 直角三角形的底边

    // y = ax + b
    a = (y1 - y2) / ((double)x1 - x2); // 直线方程的 a
    b = y1 - x1 * (y1 - y2) / ((double)x1 - x2); // 直线方程的 b

    // 找出 y 轴向最低价与直线差值最大的下标, 和最大差值
    double maxDis = fabs(b - get_l(arr, 0)); // 先把第一个的差值作为最大差值
    int maxIndex = 0;   // 最大差值所在的 x 位置(下标)
    double dis;
    for (int i = 1; i < size; i++) {
        y = a * i + b;
        dis = fabs(y - get_l(arr, i));
        if (maxDis < dis) {
            maxDis = dis;
            maxIndex = i;
        }
    }

    // 求出最低价到直线的最短(垂直)距离. Ax + By + C = 0
    A = a;
    B = -1;
    C = b;

    // 勾股定理求出斜边长
    *width = sqrt(pow(deltaY, 2) + pow(deltaX, 2));
    // 求出 y 轴向差值
    *delta = a * maxIndex + b - get_l(arr, maxIndex);
    // 点到直线最短距离
    *height = fabs(A * maxIndex + B * get_l(arr, maxIndex) + C) / sqrt(pow(A, 2) + pow(B, 2));
}

int    _timeRangeMax(void* arr, int size, int64 begin, int64 end, getTime get_t, getValue get_v)
{
    if (begin >= end || size <= 0) return -1;
    double _max = NAN;
    double tmp = NAN;
    int64 msec;
    int maxIndex = -1;
    for (int i = 0; i < size; i++) {
        msec = get_t(arr, i); // 取得时间
        if (msec < begin) continue;
        if (msec > end) break;
        if (isnan(tmp)) {
            tmp = _max = get_v(arr, i); // 取值
            maxIndex = i;
            continue;
        }
        tmp = get_v(arr, i);
        if (_max < tmp) {
            _max = tmp;
            maxIndex = i;
        }
    }
    return maxIndex;
}

int    _timeRangeMin(void* arr, int size, int64 begin, int64 end, getTime get_t, getValue get_v)
{
    if (begin >= end || size <= 0) return -1;
    double _min = NAN;
    double tmp = NAN;
    int64 msec;
    int minIndex = -1;
    for (int i = 0; i < size; i++) {
        msec = get_t(arr, i); // 取得时间
        if (msec < begin) continue;
        if (msec > end) break;
        if (isnan(tmp)) {
            tmp = _min = get_v(arr, i); // 取值
            minIndex = i;
            continue;
        }
        tmp = get_v(arr, i);
        if (_min > tmp) {
            _min = tmp;
            minIndex = i;
        }
    }
    return minIndex;
}

double _timeRangeAvg(void* arr, int size, int64 begin, int64 end, getTime get_t, getValue get_v)
{
    if (begin >= end || size <= 0) return NAN;
    double sum = 0;
    double count = 0;
    int64 msec;
    for (int i = 0; i < size; i++) {
        msec = get_t(arr, i); // 取得时间
        if (msec < begin) continue;
        if (msec > end) break;
        sum += get_v(arr, i); // 取值累加
        count++;
    }
    return sum / count;
}

int    _timeRangeCount(void* arr, int size, int64 begin, int64 end, getTime get_t)
{
    int count = 0;
    if (begin >= end) return count;
    int64 msec;
    for (int i = 0; i < size; i++) {
        msec = get_t(arr, i); // 取得时间
        if (msec < begin) continue;
        if (msec > end) break;
        count++;
    }
    return count;
}

double _timeRangeSum(void* arr, int size, int64 begin, int64 end, getTime get_t, getValue get_v)
{
    if (begin >= end) return NAN;
    double sum = 0;
    int64 msec;
    for (int i = 0; i < size; i++) {
        msec = get_t(arr, i); // 取得时间
        if (msec < begin) continue;
        if (msec > end) break;
        sum += get_v(arr, i);
    }
    return sum;
}

void   _linearEqua(Point p1, Point p2, LinearEquation* line)
{
    //两点 x 相等时, 是平等于 y 轴的直线, 无斜率
    if (p1.x == p2.x) {
        line->a = NAN;
        line->b = NAN;
    }
    else {
        line->a = (p1.y - p2.y) / (p1.x - p2.x); // p1, p2两点直线方程的 a
        line->b = p1.y - p1.x * (p1.y - p2.y) / (p1.x - p2.x); // p1, p2两点直线方程的 b
    }
}

int    _maxDistance2Line(Point p1, Point p2, void* arr, int size, double x0, int position, getValue get_v)
{
    if (size == 0) return -1;
    LinearEquation line;
    p1.x -= x0;
    p2.x -= x0;
    _linearEqua(p1, p2, &line); // 过p1,p2的直线
    int maxDisIndex = -1;
    double maxDis = -1;

    double dis, y0, y1;
    for (int i = 0; i < size; i++) {
        y0 = get_v(arr, i);
        y1 = line.a * i + line.b;
        if (position == 1 && y0 < y1) continue;
        if (position == -1 && y0 > y1) continue;
        dis = fabs(get_v(arr, i) - (line.a * i + line.b));
        if (dis > maxDis) {
            maxDis = dis;
            maxDisIndex = i;
        }
    }
    return maxDisIndex;
}

double _paralleLine(Point p, double a)
{
    // 已知 x,y,a 求 b : y - ax = b
    return p.y - a * p.x;
}

void   _paralleLineABC(Point p1, Point p2, Point p3, LinearEquation* line1, LinearEquation* line2)
{
    _linearEqua(p1, p2, line1);
    line2->a = line1->a;
    line2->b = _paralleLine(p3, line1->a);
}

double _minVal(void* arr, int size, getValue get_v)
{
    if (size <= 0) return NAN;
    double _min = get_v(arr, 0);
    double tmp;
    for (int i = 1; i < size; i++) {
        tmp = get_v(arr, i);
        if (_min > tmp)
            _min = tmp;
    }
    return _min;
}

double _maxVal(void* arr, int size, getValue get_v)
{
    if (size <= 0) return NAN;
    double _max = get_v(arr, 0);
    double tmp;
    for (int i = 1; i < size; i++) {
        tmp = get_v(arr, i);
        if (_max < tmp)
            _max = tmp;
    }
    return _max;
}

void   _createKLine(TickData* tickData, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p)
{
    if (tickData->Volume < 0 || tickData->Amount < 0)
        return;

    if (param->cycle <= _60min) {
        if (!param->isNight)
            _getKLineDayHour(tickData, kBuff, param, arr, callback, p);
        else {
            _getKLineNightHour(tickData, kBuff, param, arr, callback, p);
        }
    }
    else {
        _getKLineDay(tickData, kBuff, param, arr, callback, p);
    }
}

void   _packKLine(int64 endTime, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p)
{
    if (param->cycle == 0 || kBuff->Timestamp == 0) return;

    if (param->cycle <= _60min) {
        if (!param->isNight)
            _packKLineDayHour(endTime, kBuff, param, arr, callback, p);
        else {
            _packKLineNightHour(endTime, kBuff, param, arr, callback, p);
        }
    }
    else {
        _packKLineDay(endTime, kBuff, param, arr, callback, p);
    }
}

void   _getKLineDayHour(TickData* tickData, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p)
{
    int tickMod = (tickData->Timestamp + ZONE_SECS) % _1day; // 是每天的第 N 秒
    int kMod = (kBuff->Timestamp + ZONE_SECS) % _1day;       // 是每天的第 N 秒
    int isValidTick = 0;   // 是否有效时间内的 Tick, 如: 09:30 ~ 11:30, 13:00 ~ 15:00
    int isEndTick = 0;     // 是否交易结束时间点的 Tick, 如 11:30, 15:00
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int64 tickCycle, kCycle; // 当前 Tick, K 线所属周期
    int beginOffset = 0;

    // 开盘时间偏移量
    if (param->dSize > 0) {
        beginOffset = param->duration[0].begin;
    }

    // 判断当前 Tick 和 K 线所处时间段
    int begin, end, restTime;
    for (int i = 0; i < param->dSize; i++) {
        // 判断是否为交易结束时间点的 Tick ?
        if (tickMod == param->duration[i].end)
            isEndTick = 1;

        // 判断 Tick 时间是否在有效交易时段 ?
        begin = param->duration[i].begin;
        end = param->duration[i].end;

        if (tickMod >= begin && tickMod <= end)
            isValidTick = 1;

        if (i > 0) {
            // Tick 时间点已经历的小节休息时间
            if (tickMod >= begin) {
                restTime = param->duration[i].begin - param->duration[i - 1].end;
                tickBreakOffset += restTime;
            }
            // K 线时间点已经历的小节休息时间
            if (kMod >= begin) {
                restTime = param->duration[i].begin - param->duration[i - 1].end;
                kBreakOffset += restTime;
            }
        }
    }

    if (kMod > param->duration[param->dSize - 1].end && kMod < param->duration[param->dSize - 1].end + _60min) {
        kBuff->SettlePrice = tickData->SettlePrice;
        callback(arr, *kBuff, p);
    }

    // 如果周期是 0
    if (param->cycle == _0sec) {
        kBuff->PreClosePrice = kBuff->ClosePrice; // 记录上周期收盘价
        kBuff->Volume = 0;        // 初始化量额为 0
        kBuff->Amount = 0;
        kBuff->Timestamp = tickData->Timestamp;
        kBuff->Msec = tickData->Msec;
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
        kBuff->HighPrice = kBuff->OpenPrice = kBuff->LowPrice = kBuff->ClosePrice = tickData->LatestPrice;
        _calcVol(kBuff, tickData, param->isTotalVol);
        kBuff->ClosePrice = tickData->LatestPrice;

        // 每个 Tick 都导致 K 线更新
        if (isValidTick) {
            if (kBuff->PreClosePrice != 0) {
                kBuff->Inc = 100 * (tickData->LatestPrice - kBuff->PreClosePrice) / kBuff->PreClosePrice;
                kBuff->Imp = 100 * (kBuff->HighPrice - kBuff->LowPrice) / kBuff->PreClosePrice;
            }
            callback(arr, *kBuff, p);
        }
        return;
    }

    // 将交易时间拼接成连续的时间, 取周期
    tickCycle = (tickData->Timestamp - beginOffset - tickBreakOffset) / param->cycle;

    // 收到第一个 Tick
    if (kBuff->Timestamp == 0) {
        kBuff->Timestamp = (int64)param->cycle * tickCycle + beginOffset + tickBreakOffset;
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
        kBuff->HighPrice = kBuff->OpenPrice = kBuff->LowPrice = kBuff->ClosePrice = tickData->LatestPrice;
    }

    // 将交易时间拼接成连续的时间, 取周期
    kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;

    // 周期差大于 1, 当前 Tick 之前有 K 线数据需要补
    if (tickCycle - kCycle > 1) {
        kCycle = _packKLineDayHour(tickData->Timestamp, kBuff, param, arr, callback, p);
    }

    // 周期相差 1, 表示新周期开始
    if (tickCycle - kCycle == 1) {
        // 是交易结束时间点, 归入上一周期
        if (isEndTick) {
            tickCycle--;
        }
        else {
            // 将盘前搓合 Tick 归入第一个 K
            if (kMod < param->duration[0].begin && kMod >= param->duration[0].begin - _60min) {
                if (kBuff->HighPrice < tickData->LatestPrice)
                    kBuff->HighPrice = tickData->LatestPrice;
                if (kBuff->LowPrice > tickData->LatestPrice)
                    kBuff->LowPrice = tickData->LatestPrice;
            }
            else {
                kBuff->PreClosePrice = kBuff->ClosePrice; // 记录上周期收盘价
                kBuff->Volume = 0;        // 初始化量额为 0
                kBuff->Amount = 0;
                kBuff->HighPrice = kBuff->OpenPrice = kBuff->LowPrice = kBuff->ClosePrice = tickData->LatestPrice;
            }
            // 创建新 K 线
            kBuff->Timestamp = (int64)param->cycle * tickCycle + beginOffset + tickBreakOffset;
            //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
            _calcVol(kBuff, tickData, param->isTotalVol);
        }
    }

    // 是同一周期的 Tick
    if (tickCycle == kCycle) {
        // 更新 K 线最高价, 最低价
        if (kBuff->HighPrice < tickData->LatestPrice)
            kBuff->HighPrice = tickData->LatestPrice;
        if (kBuff->LowPrice > tickData->LatestPrice)
            kBuff->LowPrice = tickData->LatestPrice;
        _calcVol(kBuff, tickData, param->isTotalVol);
    }

    kBuff->ClosePrice = tickData->LatestPrice;

    // 每个 Tick 都导致 K 线更新
    if (isValidTick) {
        if (kBuff->PreClosePrice != 0) {
            kBuff->Inc = 100 * (tickData->LatestPrice - kBuff->PreClosePrice) / kBuff->PreClosePrice;
            kBuff->Imp = 100 * (kBuff->HighPrice - kBuff->LowPrice) / kBuff->PreClosePrice;
        }
        callback(arr, *kBuff, p);
    }
    return;
}

int64  _packKLineDayHour(int64 endTime, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p)
{
    int isCloseTime, nDay, isEndCycle, isValidK;
    int64 nextDayBegin, nextBeginTime, openTime, tickCycle, kCycle;
    struct tm ttime;
    int beginOffset = 0;    // 开盘时间到 00:00:00 的偏移量(秒)
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int isValidTick = 0;    // 是否有效时间内的 Tick, 如: 09:30 ~ 11:30, 13:00 ~ 15:00
    int isEndTick = 0;      // 是否交易结束时间点的 Tick, 如 11:30, 15:00
    int tickMod = (endTime + ZONE_SECS) % _1day; // Tick 是每天的第 N 秒
    int kMod = (kBuff->Timestamp + ZONE_SECS) % _1day; // K 是每天的第 N 秒

    // 开盘时间偏移量
    if (param->dSize > 0)
        beginOffset = param->duration[0].begin;

    // 判断当前 Tick 和 K 线所经历的小节时间
    int begin, end, restTime;
    for (int i = 0; i < param->dSize; i++) {
        begin = param->duration[i].begin;
        end = param->duration[i].end;

        if (i > 0) {
            // Tick 时间点已经历的小节休息时间
            if (tickMod >= begin) {
                restTime = param->duration[i].begin - param->duration[i - 1].end;
                tickBreakOffset += restTime;
            }
            // K 线时间点已经历的小节休息时间
            if (kMod >= begin) {
                restTime = param->duration[i].begin - param->duration[i - 1].end;
                kBreakOffset += restTime;
            }
        }
    }

    kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
    tickCycle = (endTime - beginOffset - tickBreakOffset) / param->cycle;

    while (tickCycle - kCycle > 1) {
        nDay = _daySinceEpoch(kBuff->Timestamp, ZONE); // 位于 1970-01-01 以来第 N 天
        kMod = (kBuff->Timestamp + ZONE_SECS) % _1day;
        kBreakOffset = 0;
        // 判断当前 Tick 和 K 线所处时间段
        for (int i = 0; i < param->dSize; i++) {
            begin = param->duration[i].begin;

            if (i > 0) {
                // K 线时间点已经历的小节休息时间
                if (kMod >= begin) {
                    restTime = param->duration[i].begin - param->duration[i - 1].end;
                    kBreakOffset += restTime;
                }
            }
        }

        /* 判断补 K 是否周六, 周日, 或法定假日 */
        localtime_s(&ttime, &kBuff->Timestamp);
        ttime.tm_hour = ttime.tm_min = ttime.tm_sec = 0;
        nextDayBegin = mktime(&ttime) + _1day;
        nextBeginTime = nextDayBegin + param->duration[0].begin;
        if (_isDayOff(kBuff->Timestamp, param->holidaysTS, param->hSize, ZONE)) {
            // 跳到下一日开盘时间, 如果大于等于 endTime, 则取 endTime 前一周期起始
            if ((nextBeginTime - beginOffset - kBreakOffset) / param->cycle < (endTime - beginOffset - kBreakOffset) / param->cycle)
                kBuff->Timestamp = nextBeginTime;
            else
                kBuff->Timestamp = ((endTime / param->cycle) - 1) * param->cycle;
            kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
            //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
            continue;
        }

        /* 判断补 K 时间是否在有效交易时间区间 */
        isValidK = 0;
        isCloseTime = 1;
        for (int i = 0; i < param->dSize; i++) {
            if (((kBuff->Timestamp + ZONE_SECS) % _1day) >= param->duration[i].begin
                && ((kBuff->Timestamp + ZONE_SECS) % _1day) <= param->duration[i].end) {
                isValidK = 1;
                break;
            }
            if (((kBuff->Timestamp + ZONE_SECS) % _1day) < param->duration[i].begin) {
                // K 线时间戳处于盘前的非交易时段, 跳到当天下一段交易起始时间; 如果大于等于 endTime, 则取 endTime前一周期
                openTime = nextDayBegin - _1day + param->duration[i].begin;
                if (((openTime - beginOffset - kBreakOffset) / param->cycle) < ((endTime - beginOffset - kBreakOffset) / param->cycle))
                    kBuff->Timestamp = openTime;
                else
                    kBuff->Timestamp = ((endTime / param->cycle) - 1) * param->cycle;

                kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
                //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
                isCloseTime = 0;
                break;
            }
        }

        if (!isValidK) {
            // K 线时间戳处于收盘后的非交易时段, 跳到下一交易日开盘时间, 如果大于等于 endTime, 则取 endTime前一周期
            if (isCloseTime) {
                openTime = nextDayBegin + param->duration[0].begin;
                if ((openTime - beginOffset - kBreakOffset) / param->cycle < ((endTime - beginOffset - kBreakOffset) / param->cycle))
                    kBuff->Timestamp = openTime; // 第二天的开始交易时间点
                else
                    kBuff->Timestamp = ((endTime / param->cycle) - 1) * param->cycle;
                kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
                //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
            }
            continue;
        }

        // 向前推进一周期
        kCycle++;
        kBuff->Timestamp += param->cycle;
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);

        // 如果新 K 周期与交易结束时间点处于相同周期, 跳过
        kMod = (kBuff->Timestamp + ZONE_SECS) % _1day;   // 每天的第 N 秒
        isEndCycle = 0;
        isValidK = 0;
        for (int i = 0; i < param->dSize; i++) {
            // 检查是否为交易结束时间点的 K ?
            if (kMod == param->duration[i].end) {
                isEndCycle = 1;
                break;
            }
            // 检查 K 时间是否在有效范围 ?
            if (kMod >= param->duration[i].begin && kMod <= param->duration[i].end)
                isValidK = 1;
        }
        if (isEndCycle || !isValidK) continue;

        kBuff->Volume = kBuff->Inc = kBuff->Imp = 0;
        kBuff->Amount = 0;
        callback(arr, *kBuff, p);
    }
    return kCycle; // 补完后 K 线所处周期
}

void   _getKLineNightHour(TickData* tickData, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p)
{
    int tickMod = (tickData->Timestamp + ZONE_SECS) % _1day; // 是每天的第 N 秒
    int kMod = (kBuff->Timestamp + ZONE_SECS) % _1day;       // 是每天的第 N 秒
    int isValidTick = 0;    // 是否有效时间内的 Tick, 如: 09:30 ~ 11:30, 13:00 ~ 15:00
    int isEndTick = 0;      // 是否交易结束时间点的 Tick, 如 11:30, 15:00
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int64 tickCycle, kCycle;  // 当前 Tick, K 线所属周期
    int beginOffset = 0;

    // 开盘时间偏移量
    if (param->dSize > 0) {
        beginOffset = param->duration[0].begin - _60min;
    }

    // 判断当前 Tick 和 K 线所处时间段
    int begin, end, restTime;
    for (int i = 0; i < param->dSize; i++) {
        // 判断是否为交易结束时间点的 Tick ?
        if (tickMod == (_1day + param->duration[i].end) % _1day)
            isEndTick = 1;

        // 判断 Tick 时间是否在有效交易时段 ?
        if (param->duration[i].begin < 0)
            begin = (_1day + param->duration[i].begin) % _1day;
        else
            begin = param->duration[i].begin;

        if (param->duration[i].end < 0)
            end = (_1day + param->duration[i].end) % _1day;
        else
            end = param->duration[i].end;

        if (begin < end) {
            if (tickMod >= begin && tickMod <= end)
                isValidTick = 1;
        }
        else {
            if (tickMod >= begin && tickMod < _1day || tickMod >= 0 && tickMod <= end)
                isValidTick = 1;
        }

        if (i > 0) {
            // Tick 时间点已经历的小节休息时间
            if ((tickMod - beginOffset) % _1day >= begin - beginOffset) {
                restTime = param->duration[i].begin - param->duration[i - 1].end;
                if (restTime < _60min * 3)
                    tickBreakOffset += restTime;
            }
            // K 线时间点已经历的小节休息时间
            if ((kMod - beginOffset) % _1day >= begin - beginOffset) {
                restTime = param->duration[i].begin - param->duration[i - 1].end;
                if (restTime < _60min * 3)
                    kBreakOffset += restTime;
            }
        }
    }

    // 如果周期是 0
    if (param->cycle == 0) {
        kBuff->PreClosePrice = kBuff->ClosePrice; // 记录上周期收盘价
        kBuff->Volume = 0;        // 初始化量额为 0
        kBuff->Amount = 0;
        kBuff->Timestamp = tickData->Timestamp;
        kBuff->Msec = tickData->Msec;
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
        kBuff->HighPrice = kBuff->OpenPrice = kBuff->LowPrice = kBuff->ClosePrice = tickData->LatestPrice;
        _calcVol(kBuff, tickData, param->isTotalVol);
        kBuff->ClosePrice = tickData->LatestPrice;

        // 每个 Tick 都导致 K 线更新
        if (isValidTick) {
            if (kBuff->PreClosePrice != 0) {
                kBuff->Inc = 100 * (tickData->LatestPrice - kBuff->PreClosePrice) / kBuff->PreClosePrice;
                kBuff->Imp = 100 * (kBuff->HighPrice - kBuff->LowPrice) / kBuff->PreClosePrice;
            }
            callback(arr, *kBuff, p);
        }
        return;
    }

    // 将交易时间拼接成连续的时间, 取周期
    tickCycle = (tickData->Timestamp - beginOffset - tickBreakOffset) / param->cycle;

    // 收到第一个 Tick
    if (kBuff->Timestamp == 0) {
        kBuff->Timestamp = (int64)param->cycle * tickCycle + beginOffset + tickBreakOffset;
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
        kBuff->HighPrice = kBuff->OpenPrice = kBuff->LowPrice = kBuff->ClosePrice = tickData->LatestPrice;
    }

    // 将交易时间拼接成连续的时间, 取周期
    kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;

    // 周期差大于 1, 当前 Tick 之前有 K 线数据需要补
    if (tickCycle - kCycle > 1) {
        kCycle = _packKLineNightHour(tickData->Timestamp, kBuff, param, arr, callback, p);
    }

    // 周期相差 1, 表示新周期开始
    if (tickCycle - kCycle == 1) {
        // 是交易结束时间点, 归入上一周期
        if (isEndTick) {
            tickCycle--;
        }
        else {
            // 将盘前搓合 Tick 归入第一个 K
            begin = (_1day + param->duration[0].begin) % _1day;
            if (kMod < begin && kMod >= begin - _60min) {
                if (kBuff->HighPrice < tickData->LatestPrice)
                    kBuff->HighPrice = tickData->LatestPrice;
                if (kBuff->LowPrice > tickData->LatestPrice)
                    kBuff->LowPrice = tickData->LatestPrice;
            }
            else {
                kBuff->PreClosePrice = kBuff->ClosePrice; // 记录上周期收盘价
                kBuff->Volume = 0;        // 初始化量额为 0
                kBuff->Amount = 0;
                kBuff->HighPrice = kBuff->OpenPrice = kBuff->LowPrice = kBuff->ClosePrice = tickData->LatestPrice;
            }
            // 创建新 K 线
            kBuff->Timestamp = (int64)param->cycle * tickCycle + beginOffset + tickBreakOffset;
            //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
            _calcVol(kBuff, tickData, param->isTotalVol);
        }
    }

    // 是同一周期的 Tick
    if (tickCycle == kCycle) {
        // 更新 K 线最高价, 最低价
        if (kBuff->HighPrice < tickData->LatestPrice)
            kBuff->HighPrice = tickData->LatestPrice;
        if (kBuff->LowPrice > tickData->LatestPrice)
            kBuff->LowPrice = tickData->LatestPrice;
        _calcVol(kBuff, tickData, param->isTotalVol);
    }

    kBuff->ClosePrice = tickData->LatestPrice;

    // 每个 Tick 都导致 K 线更新
    if (isValidTick) {
        if (kBuff->PreClosePrice != 0) {
            kBuff->Inc = 100 * (tickData->LatestPrice - kBuff->PreClosePrice) / kBuff->PreClosePrice;
            kBuff->Imp = 100 * (kBuff->HighPrice - kBuff->LowPrice) / kBuff->PreClosePrice;
        }
        callback(arr, *kBuff, p);
    }
    return;
}

int64  _packKLineNightHour(int64 endTime, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p)
{
    int isAfterCloseTime, nDay, isEndCycle, isValidK, begin, end, nextDayIsDayOff;
    int64 nextDayBegin, nextBeginTime, openTime, tickCycle, kCycle;
    struct tm ttime;
    int beginOffset = 0;    // 开盘时间到 00:00:00 的偏移量(秒)
    int tickBreakOffset = 0;// Tick 需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kBreakOffset = 0;   // K 线需剔除的小节休息时间(秒), 如: 11:30 ~ 13:00
    int kMod = (kBuff->Timestamp + ZONE_SECS) % _1day; // K 是每天的第 N 秒

    // 开盘时间偏移量
    if (param->dSize > 0)
        beginOffset = param->duration[0].begin - _60min;

    // 判断当前 Tick 和 K 线所经历的小节时间
    tickBreakOffset = _totalRestTime(endTime, beginOffset, param->duration, param->dSize);
    kBreakOffset = _totalRestTime(kBuff->Timestamp, beginOffset, param->duration, param->dSize);

    kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
    tickCycle = (endTime - beginOffset - tickBreakOffset) / param->cycle;

    while (tickCycle - kCycle > 1) {
        nDay = _daySinceEpoch(kBuff->Timestamp, ZONE); // 位于 1970-01-01 以来第 N 天
        kMod = (kBuff->Timestamp + ZONE_SECS) % _1day;
        kBreakOffset = _totalRestTime(kBuff->Timestamp, beginOffset, param->duration, param->dSize);

        localtime_s(&ttime, &kBuff->Timestamp);
        ttime.tm_hour = ttime.tm_min = ttime.tm_sec = 0;
        nextDayBegin = mktime(&ttime) + _1day;
        nextBeginTime = nextDayBegin + param->duration[0].begin;

        // 如果第二天是周末 或 节假日, 不需要补夜盘数据
        nextDayIsDayOff = _isDayOff(kBuff->Timestamp + _1day, param->holidaysTS, param->hSize, ZONE);
        if (kMod > param->duration[param->dSize - 1].end && nextDayIsDayOff) {
            nextBeginTime = nextDayBegin + param->duration[1].begin;
            if ((nextBeginTime - beginOffset - kBreakOffset) / param->cycle < (endTime - beginOffset - kBreakOffset) / param->cycle)
                kBuff->Timestamp = nextBeginTime;
            else
                kBuff->Timestamp = ((endTime / param->cycle) - 1) * param->cycle;
            kBreakOffset = _totalRestTime(kBuff->Timestamp, beginOffset, param->duration, param->dSize);
            kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
            //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
            continue;
        }

        /* 判断补 K 是否周六, 周日, 或法定假日 */
        if (_isDayOff(kBuff->Timestamp, param->holidaysTS, param->hSize, ZONE)) {
            nextBeginTime = nextDayBegin + param->duration[1].begin;
            // 跳到下一日开盘时间, 如果大于等于 endTime, 则取 endTime 前一周期起始
            if ((nextBeginTime - beginOffset - kBreakOffset) / param->cycle < (endTime - beginOffset - kBreakOffset) / param->cycle)
                kBuff->Timestamp = nextBeginTime;
            else
                kBuff->Timestamp = ((endTime / param->cycle) - 1) * param->cycle;

            kBreakOffset = _totalRestTime(kBuff->Timestamp, beginOffset, param->duration, param->dSize);
            kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
            //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
            continue;
        }

        /* 判断补 K 时间是否在有效交易时间区间 */
        isValidK = 0;
        isAfterCloseTime = 1;
        for (int i = 0; i < param->dSize; i++) {
            // 判断 Tick 时间是否在有效交易时段 ?
            if (param->duration[i].begin < 0)
                begin = (_1day + param->duration[i].begin) % _1day;
            else
                begin = param->duration[i].begin;

            if (param->duration[i].end < 0)
                end = (_1day + param->duration[i].end) % _1day;
            else
                end = param->duration[i].end;

            if (begin < end) {
                if (kMod >= begin && kMod <= end) {
                    isValidK = 1;
                    break;
                }
            }
            else {
                if (kMod >= begin && kMod < _1day || kMod >= 0 && kMod <= end) {
                    isValidK = 1;
                    break;
                }
            }

            if ((kMod - beginOffset) % _1day < param->duration[i].begin - beginOffset) {
                // K 线时间戳处于盘前的非交易时段, 跳到当天下一段交易起始时间; 如果大于等于 endTime, 则取 endTime前一周期
                if (param->duration[i].begin < 0)
                    openTime = nextDayBegin + param->duration[i].begin;
                else
                    openTime = nextDayBegin - _1day + param->duration[i].begin;
                if (((openTime - beginOffset - kBreakOffset) / param->cycle) < ((endTime - beginOffset - kBreakOffset) / param->cycle))
                    kBuff->Timestamp = openTime;
                else
                    kBuff->Timestamp = ((endTime / param->cycle) - 1) * param->cycle;
                kBreakOffset = _totalRestTime(kBuff->Timestamp, beginOffset, param->duration, param->dSize);
                kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
                //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
                isAfterCloseTime = 0;
                break;
            }
        }

        if (!isValidK) {
            // K 线时间戳处于收盘后的非交易时段, 跳到下一交易日开盘时间, 如果大于等于 endTime, 则取 endTime前一周期
            if (isAfterCloseTime) {
                openTime = nextDayBegin + param->duration[0].begin;
                if ((openTime - beginOffset - kBreakOffset) / param->cycle < ((endTime - beginOffset - kBreakOffset) / param->cycle))
                    kBuff->Timestamp = openTime; // 第二天的开始交易时间点
                else
                    kBuff->Timestamp = ((endTime / param->cycle) - 1) * param->cycle;
                kBreakOffset = _totalRestTime(kBuff->Timestamp, beginOffset, param->duration, param->dSize);
                kCycle = (kBuff->Timestamp - beginOffset - kBreakOffset) / param->cycle;
                //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
            }
            continue;
        }

        // 向前推进一周期
        kCycle++;
        kBuff->Timestamp += param->cycle;
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);

        // 如果新 K 周期与交易结束时间点处于相同周期, 跳过
        kMod = (kBuff->Timestamp + ZONE_SECS) % _1day;   // 每天的第 N 秒
        isEndCycle = 0;
        isValidK = 0;
        for (int i = 0; i < param->dSize; i++) {
            // 判断 Tick 时间是否在有效交易时段 ?
            if (param->duration[i].begin < 0)
                begin = (_1day + param->duration[i].begin) % _1day;
            else
                begin = param->duration[i].begin;

            if (param->duration[i].end < 0)
                end = (_1day + param->duration[i].end) % _1day;
            else
                end = param->duration[i].end;

            // 检查是否为交易结束时间点的 K ?
            if (kMod == end) {
                isEndCycle = 1;
                break;
            }
            // 检查 K 时间是否在有效范围 ?
            if (begin < end) {
                if (kMod >= begin && kMod <= end)
                    isValidK = 1;
            }
            else {
                if (kMod >= begin && kMod < _1day || kMod >= 0 && kMod <= end)
                    isValidK = 1;
            }
        }

        if (isEndCycle || !isValidK) continue;

        kBuff->Volume = kBuff->Inc = kBuff->Imp = 0;
        kBuff->Amount = 0;
        callback(arr, *kBuff, p);
    }
    return kCycle; // 补完后 K 线所处周期
}

void   _getKLineDay(TickData* tickData, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p)
{
    int64 tickCycle, kCycle; // 当前 Tick, K 线所属周期
    int beginOffset = 0;
    if (param->isNight)
        beginOffset = _60min * 4;

    while (param->isNight && _isDayOff(tickData->Timestamp + beginOffset, param->holidaysTS, param->hSize, ZONE)) {
        tickData->Timestamp += _1day;
    }

    // 收到第一个 Tick
    if (kBuff->Timestamp == 0) {
        kBuff->Timestamp = _getBeginOfCycle(tickData->Timestamp + beginOffset, param->cycle);
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
        kBuff->HighPrice = kBuff->OpenPrice = kBuff->LowPrice = kBuff->ClosePrice = tickData->LatestPrice;
    }

    switch (param->cycle) {
    case(_1day): {
        tickCycle = _daySinceEpoch(tickData->Timestamp + beginOffset, ZONE);
        kCycle = _daySinceEpoch(kBuff->Timestamp + beginOffset, ZONE);
    } break;
    case(_1week): {
        tickCycle = _weekSinceEpoch(tickData->Timestamp + beginOffset, ZONE);
        kCycle = _weekSinceEpoch(kBuff->Timestamp + beginOffset, ZONE);
    } break;
    default: {
        tickCycle = _monthSinceEpoch(tickData->Timestamp + beginOffset);
        kCycle = _monthSinceEpoch(kBuff->Timestamp + beginOffset);
    }
    }

    // 周期差大于 1, 当前 Tick 之前有 K 线数据需要补
    if (tickCycle - kCycle > 1) {
        kCycle = _packKLineDay(tickData->Timestamp, kBuff, param, arr, callback, p);
    }

    // 周期相差 1, 表示新周期开始
    if (tickCycle - kCycle == 1) {
        kBuff->PreClosePrice = kBuff->ClosePrice; // 记录上周期收盘价
        kBuff->Volume = 0;        // 初始化量额为 0
        kBuff->Amount = 0;
        kBuff->Timestamp = _getBeginOfCycle(tickData->Timestamp + beginOffset, param->cycle);
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
        kBuff->HighPrice = kBuff->OpenPrice = kBuff->LowPrice = kBuff->ClosePrice = tickData->LatestPrice;
        _calcVol(kBuff, tickData, param->isTotalVol);
    }

    // 是同一周期的 Tick
    if (tickCycle == kCycle) {
        // 更新 K 线最高价, 最低价
        if (kBuff->HighPrice < tickData->LatestPrice)
            kBuff->HighPrice = tickData->LatestPrice;
        if (kBuff->LowPrice > tickData->LatestPrice)
            kBuff->LowPrice = tickData->LatestPrice;
        _calcVol(kBuff, tickData, param->isTotalVol);
    }

    kBuff->ClosePrice = tickData->LatestPrice;
    kBuff->SettlePrice = tickData->SettlePrice;

    if (kBuff->PreClosePrice != 0) {
        kBuff->Inc = 100 * (tickData->LatestPrice - kBuff->PreClosePrice) / kBuff->PreClosePrice;
        kBuff->Imp = 100 * (kBuff->HighPrice - kBuff->LowPrice) / kBuff->PreClosePrice;
    }
    callback(arr, *kBuff, p);
    return;
}

int64  _packKLineDay(int64 endTime, KLine* kBuff, KParam* param, void* arr, KLineCallback callback, void* p)
{
    int64 tickCycle, kCycle, oldCycle;
    struct tm ttime;
    int beginOffset = 0;

    // 开盘时间偏移量
    if (param->isNight)
        beginOffset = _60min * 4;

    switch (param->cycle) {
    case(_1day): {
        tickCycle = _daySinceEpoch(endTime + beginOffset, ZONE);
        kCycle = _daySinceEpoch(kBuff->Timestamp + beginOffset, ZONE);
    } break;
    case(_1week): {
        tickCycle = _weekSinceEpoch(endTime + beginOffset, ZONE);
        kCycle = _weekSinceEpoch(kBuff->Timestamp + beginOffset, ZONE);
    } break;
    default: {
        tickCycle = _monthSinceEpoch(endTime + beginOffset);
        kCycle = _monthSinceEpoch(kBuff->Timestamp + beginOffset);
    }
    }

    while (tickCycle - kCycle > 1) {
        oldCycle = kCycle;
        /* 判断补 K 是否周六, 周日, 或法定假日 */
        localtime_s(&ttime, &kBuff->Timestamp);
        ttime.tm_hour = ttime.tm_min = ttime.tm_sec = 0;
        // 下一天
        kBuff->Timestamp = mktime(&ttime) + _1day;
        switch (param->cycle) {
        case(_1day): {
            kCycle = _daySinceEpoch(kBuff->Timestamp + beginOffset, ZONE);
        } break;
        case(_1week): {
            kCycle = _weekSinceEpoch(kBuff->Timestamp + beginOffset, ZONE);
        } break;
        default: {
            kCycle = _monthSinceEpoch(kBuff->Timestamp + beginOffset);
        }
        }
        //_ts2dateTime(kBuff->Timestamp, &kBuff->dt[0]);
        if (_isDayOff(kBuff->Timestamp, param->holidaysTS, param->hSize, ZONE))
            continue;

        if (oldCycle != kCycle) {
            kBuff->Volume = kBuff->Inc = kBuff->Imp = 0;
            kBuff->Amount = 0;
            callback(arr, *kBuff, p);
        }
    }
    return kCycle; // 补完后 K 线所处周期
}

void   _calcVol(KLine* kd, TickData* tickData, int isTotalVol)
{
    if (!isTotalVol) {
        kd->Volume += tickData->Volume;
        kd->Amount += tickData->Amount;
    }
    else {
        kd->Volume += tickData->Volume - kd->PreVolume;
        kd->Amount += tickData->Amount - kd->PreAmount;
        kd->PreAmount = tickData->Amount;
        kd->PreVolume = tickData->Volume;
    }
}

int    _binSearch(int* arr, int low, int high, int target)
{
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] > target)
            high = mid - 1;
        else if (arr[mid] < target)
            low = mid + 1;
        else
            return mid;
    }
    return -1;
}

int    _binSearchTS(void* arr, int low, int high, double target, getValue cb)
{
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (cb(arr, mid) > target)
            high = mid - 1;
        else if (cb(arr, mid) < target)
            low = mid + 1;
        else
            return mid;
    }
    return -1;
}

int64  _date2ts(const char* dt)
{
    time_t t;
    struct tm s;
    sscanf_s(dt, "%d-%d-%d", &s.tm_year, &s.tm_mon, &s.tm_mday);
    s.tm_year -= 1900;
    s.tm_mon -= 1;
    s.tm_hour = s.tm_min = s.tm_sec = 0;
    t = mktime(&s);
    return t;
}

int64  _dateTime2ts(const char* dt)
{
    time_t t;
    struct tm s;
    sscanf_s(dt, "%d-%d-%d %d:%d:%d", &s.tm_year, &s.tm_mon, &s.tm_mday, &s.tm_hour, &s.tm_min, &s.tm_sec);
    s.tm_year -= 1900;
    s.tm_mon -= 1;
    t = mktime(&s);
    return t;
}

void   _ts2dateTime(int64 ts, char* rs)
{
    time_t tt;
    struct tm ttime;
    tt = ts;
    localtime_s(&ttime, &tt);
    strftime(rs, 20, "%Y-%m-%d %H:%M:%S", &ttime);
}

int    _isOdd(int number) {
    return number % 2;
}

int    _compare(const void* p1, const void* p2)
{
    return (*(time_t*)p1 - *(time_t*)p2);
}

void   _convertNonTradingDays(void* arr, int size, int* result, getDate get_d, int zone)
{
    char date[11];
    int64 ts;
    for (int i = 0; i < size; i++) {
        get_d(arr, i, date);  // 获取日期, 如 2021-03-24
        ts = _date2ts(date);   // 转成时间戳
        result[i] = _daySinceEpoch(ts, zone); // 从 1970-01-01 以来的第 N 天
    }
    qsort(result, size, sizeof(int), _compare); // 正序排序
}

int    _daySinceEpoch(int64 ts, int64 zone)
{
    return (ts + zone * _60min) / _1day;
}

int    _weekSinceEpoch(int64 ts, int64 zone)
{
    return (ts + zone * _60min + _1day * 3) / (_1day * 7);
}

int    _monthSinceEpoch(int64 ts)
{
    struct tm ttime;
    localtime_s(&ttime, &ts);
    return (ttime.tm_year - 70) * 12 + ttime.tm_mon + 1;
}

int64  _getBeginOfCycle(int64 ts, K_CYCLES cycle)
{
    int64 result;
    struct tm ttime;
    switch (cycle) {
    case(_1day): {
        localtime_s(&ttime, &ts);
        ttime.tm_hour = ttime.tm_min = ttime.tm_sec = 0;
        result = mktime(&ttime);
    } break;
    case(_1week): {
        localtime_s(&ttime, &ts);
        ts = (ts - (ttime.tm_wday - 1) * _1day);
        localtime_s(&ttime, &ts);
        ttime.tm_hour = ttime.tm_min = ttime.tm_sec = 0;
        result = mktime(&ttime);
    } break;
    case(_1month): {
        localtime_s(&ttime, &ts);
        ttime.tm_mday = 1;
        ttime.tm_hour = ttime.tm_min = ttime.tm_sec = 0;
        result = mktime(&ttime);
    }break;
    default: {
        result = cycle * (ts / cycle);
    }
    }
    return result;
}

int    _isWeekEnd(int64 ts)
{
    struct tm ttime;
    localtime_s(&ttime, &ts);
    return (ttime.tm_wday == 0 || ttime.tm_wday == 6);
}

int    _isHoliday(int64 ts, int* holidayArr, int size, int zone)
{
    if (holidayArr == NULL)
        return 0;
    int index;
    int nDay = _daySinceEpoch(ts, zone);
    index = _binSearch(holidayArr, 0, size - 1, nDay);
    return index >= 0;
}

int    _isDayOff(int64 ts, int* holidayArr, int size, int zone)
{
    int result = 0;
    result = _isWeekEnd(ts);
    if (result) return result;
    result = _isHoliday(ts, holidayArr, size, zone);
    return result;
}

int    _totalRestTime(int64 ts, int beginOffset, TDuration* duration, int size)
{
    int begin, end, restTime, pos, totalRestTime = 0;
    int mod = (ts + ZONE_SECS) % _1day;

    for (int i = 0; i < size; i++) {
        // 判断 Tick 时间是否在有效交易时段 ?
        if (duration[i].begin < 0)
            begin = (_1day + duration[i].begin) % _1day;
        else
            begin = duration[i].begin;

        if (duration[i].end < 0)
            end = (_1day + duration[i].end) % _1day;
        else
            end = duration[i].end;

        if (i == 0) continue;
        // Tick 时间点已经历的小节休息时间
        pos = (mod - beginOffset) % _1day;
        if (pos >= begin - beginOffset && pos <= (_1day + duration[0].begin - _60min) % _1day) {
            restTime = duration[i].begin - duration[i - 1].end;
            if (restTime < _60min * 3)
                totalRestTime += restTime;
        }
    }
    return totalRestTime;
}
