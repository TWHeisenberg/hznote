package com.hznc.datacenter.bean;

import java.util.Calendar;

/**
 * 时间区间，开始的小时和结束小时
 * @author TWang
 * @date 2021-04-27 14:30:00
 */
public class TDuration {

  // 开始小时
  public Calendar begin;
  // 结束小时
  public Calendar end;

  public TDuration(Calendar begin, Calendar end){
    this.begin = begin;
    this.end = end;
  }

  public Calendar getBegin() {
    return begin;
  }

  public Calendar getEnd() {
    return end;
  }




}
