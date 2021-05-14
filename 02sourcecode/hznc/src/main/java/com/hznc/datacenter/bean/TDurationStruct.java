package com.hznc.datacenter.bean;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * 成员变量结构体：
 * ****************************************
 *     // 交易时段
 *     typedef struct TDuration {
 *         TIME_POINT begin;   // 交易起始时间
 *         TIME_POINT end;     // 交易终止时间
 *     } TDuration;
 * ****************************************
 *  @author TWang
 *  @date 2021-04-19 16:30:00
 */
public class TDurationStruct extends Structure {
    // TODO(TWang): c++里面是枚举类型，这里直接使用int变量行不行?
    public int begin;
    public int end;

    public static class ByReference extends TDurationStruct implements Structure.ByReference{}
    public static class ByValue extends TDurationStruct implements Structure.ByValue{}

    @Override
    public List getFieldOrder() {
        return Arrays.asList(new String[]{"begin", "end"});
    }
}