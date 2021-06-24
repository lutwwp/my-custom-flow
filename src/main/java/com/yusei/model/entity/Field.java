package com.yusei.model.entity;

import lombok.Data;

@Data
public class Field {

  private Long fieldId;

  private Long formId;

  private String fieldCode;

  private Integer orderNumber;

  private String fieldName;

  private String fieldType;

  private Long linkFormId;

  private Long linkFieldId;

  private String defaultValueType;

  private String defaultValue;

  private Boolean necessary;

  private Boolean allowRepeated;

  private Boolean showField;

  private Boolean editField;
}
