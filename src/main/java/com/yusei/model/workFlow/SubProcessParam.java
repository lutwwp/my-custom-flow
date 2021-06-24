package com.yusei.model.workFlow;

import lombok.Data;

@Data
public class SubProcessParam {

  private String id;
  private String name;
  private String processDefinitionKey;
  private String width;
  private String height;
  private String x;
  private String y;
}
