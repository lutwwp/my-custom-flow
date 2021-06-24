package com.yusei.model.entity;

import lombok.Data;

@Data
public class ProcessNodeField {

  private Long processNodeFieldId;

  private Long processInfoId;

  private String processDefinitionKey;

  private String taskDefinitionKey;

  private Long formId;

  private Long fieldId;

  private Boolean showField;

  private Boolean editField;

  private Boolean summary;

}
