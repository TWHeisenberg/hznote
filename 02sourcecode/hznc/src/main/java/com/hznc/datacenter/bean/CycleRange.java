package com.hznc.datacenter.bean;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 每个周期又细分哪些k线
 * 每个周期有自己的容器保存tick数据做合并
 * @author TWang
 * @date 2021-04-30 17:30:00
 */
public class CycleRange {

  private int[] cycles;
  private BlockingQueue<KData> dataset;

  public CycleRange(int[] cycles){
    this.cycles = cycles;
    dataset = new LinkedBlockingQueue();
  }

  public void addData(KData kdata){
    this.dataset.add(kdata);
  }

  public int[] getCycles() {
    return cycles;
  }

  public void setCycles(int[] cycles) {
    this.cycles = cycles;
  }

  public BlockingQueue<KData> getDataset() {
    return dataset;
  }

  public void setDataset(BlockingQueue<KData> dataset) {
    this.dataset = dataset;
  }
}
