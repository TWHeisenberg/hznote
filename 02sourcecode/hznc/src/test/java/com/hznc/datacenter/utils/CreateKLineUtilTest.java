package com.hznc.datacenter.utils;

import com.google.common.collect.Maps;
import com.hznc.datacenter.hbase.MockHbaseClient;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CreateKLineUtilTest {

  private CreateKLineUtil createKLine;

  @Before
  public void setUp() {
    createKLine = new CreateKLineUtil(Maps.newHashMap(), new MockHbaseClient());
  }

  @Test // 生成k线接口测试
  public void createKLineTest() {
    // util.createKLine(tickData, kLine, kParam,  info, callback, null);
  }

  @Test // 简单的native接口测试
  public void simpleTest() {
    double result = CreateKLineUtil.Function.instance._xDelta(1618896940, 1618896920);
    Assert.assertTrue(result == 20.0);
  }

  @Test // 简单的回调native接口测试
  public void simpleCallBackTest(){
    Pointer p = new Memory(100);

    CreateKLineUtil.MaxValCallback callback = new CreateKLineUtil.MaxValCallback();
    double result = CreateKLineUtil.Function.instance._maxVal(p, 1, callback);
    System.out.println(result);
  }

  @Test
  public void toTodySecondsTest(){
    long time1 = 1620316805; // 当天 00:00:05
    long time2 = 1620351005; // 当天 09:30:05
    long time3 = 1620372600; // 当天 15:30:00

    Assert.assertEquals(5, createKLine.toTodySeconds(time1));
    Assert.assertEquals(34205, createKLine.toTodySeconds(time2));
    Assert.assertEquals(55800, createKLine.toTodySeconds(time3));
  }

  public void packKLineTest(){

  }
}
