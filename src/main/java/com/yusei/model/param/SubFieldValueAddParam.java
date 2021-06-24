package com.yusei.model.param;

import lombok.Data;

@Data
public class SubFieldValueAddParam {

  private Long fieldId;

  private String fieldCode;

  private String fieldValue;

  private Long groupId;
}
