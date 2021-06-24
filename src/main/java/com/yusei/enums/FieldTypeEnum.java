package com.yusei.enums;

import java.util.List;

/**
 * 表单字段类型的枚举类
 */
public enum FieldTypeEnum {

  //主键
  PRIMARY_KEY,

  //文本输入
  TEXT,
  //数字输入
  NUMBER,
  //日期输入
  DATE,
  //下拉选择
  SELECT,
  //关联查询
  LINK_QUERY,
  //关联数据
  LINK_DATA,
  //流水号
  SERIAL_NUMBER,
  //单选框
  RADIOBUTTON,
  // 复选框
  CHECKBOX,

  // 子表单
  SUB_TABLE

  ;
}
