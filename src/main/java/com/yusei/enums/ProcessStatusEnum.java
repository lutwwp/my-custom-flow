package com.yusei.enums;

public enum ProcessStatusEnum {

  PENDING("进行中"),

  SUSPEND("挂起"),

  OVER("流转完成");

  private String processStatus;

  ProcessStatusEnum(String processStatus) {
    this.processStatus = processStatus;
  }

  public String getProcessStatus() {
    return processStatus;
  }

  public void setProcessStatus(String processStatus) {
    this.processStatus = processStatus;
  }
}
