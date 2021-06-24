package com.yusei.model.entity;

import lombok.Data;

@Data
public class FieldValue {

  private Long fieldValueId;

  private Long formId;

  private Long fieldId;

  private String fieldType;

  private String fieldValue;

  private Long primaryKeyValue;

  private Long groupId;
}
