package com.yusei.model.param;

import lombok.Data;

import java.util.List;

@Data
public class FieldValueAddParam {

  private Long fieldValueId;

  private Long fieldId;

  private String fieldCode;

  private String fieldValue;

  private Long groupId;

  private List<FieldValueAddParam> subFieldValueAddParams;
}
