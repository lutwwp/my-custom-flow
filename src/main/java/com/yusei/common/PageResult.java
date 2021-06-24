package com.yusei.common;

import java.util.List;

public class PageResult<T> {

  private long total;

  private List<T> dataList;

  public PageResult() {
  }

  public PageResult(List<T> dataList, long total) {
    this.dataList = dataList;
    this.total = total;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public List<T> getDataList() {
    return dataList;
  }

  public void setDataList(List<T> dataList) {
    this.dataList = dataList;
  }
}
