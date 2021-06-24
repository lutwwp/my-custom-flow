package com.yusei.model.dto.process;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class ProcessFormInfoDto {

  private Long processInfoId;

  private String processDefinitionKey;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long formId;

  private String processName;

  private Integer version;
}
