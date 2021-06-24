package com.yusei.model.dto.process;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class ProcessNodeFieldDetailDto {

  private String taskDefinitionKey;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fieldId;

  private String fieldName;

  private String fieldType;

  private Boolean showField;

  private Boolean editField;

  private Boolean summary;
}
