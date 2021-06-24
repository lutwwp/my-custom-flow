package com.yusei.enums;


public enum ExtendRuleTypeEnum {

  DATE_FORMAT_YYYY_MM("yyyy-MM"),
  DATE_FORMAT_YYYY_MM_DD("yyyy-MM-dd"),
  DATE_FORMAT_YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
  DATE_FORMAT_YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
  ;

  private String format;

  ExtendRuleTypeEnum(String format) {
    this.format = format;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }
}
