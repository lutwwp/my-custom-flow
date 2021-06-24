package com.yusei.model.dto.process;

import lombok.Data;

@Data
public class ProcessNodeAttributeDto {

  private String taskDefinitionKey;

  //是否回退
  private Boolean rollBack;
  //是否撤销
  private Boolean revoke;
}
