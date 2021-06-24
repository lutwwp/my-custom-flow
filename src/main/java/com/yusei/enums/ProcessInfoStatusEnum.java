package com.yusei.enums;

public enum ProcessInfoStatusEnum {

  //使用中
  ACTIVE(1),

  //历史版本
  HISTORY(2),

  //未发布（设计中）
  DESIGN(3);

  private Integer status;

  ProcessInfoStatusEnum(Integer status) {
    this.status = status;
  }

  public Integer getStatus() {
    return status;
  }

}
