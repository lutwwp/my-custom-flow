package com.yusei.model.param.process;

import lombok.Data;

@Data
public class ProcessNodeFieldAddParam {

  private String taskDefinitionKey;

  private Long fieldId;

  private Boolean showField;

  private Boolean editField;

  private Boolean summary;
}
