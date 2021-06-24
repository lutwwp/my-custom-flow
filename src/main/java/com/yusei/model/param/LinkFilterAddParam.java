package com.yusei.model.param;

import lombok.Data;

@Data
public class LinkFilterAddParam {

  private String currentFormFieldCode;

  private Long linkFormFieldId;

  private String method;
}
