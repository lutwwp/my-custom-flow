package com.yusei.model.entity;

import lombok.Data;

@Data
public class LinkFilter {

  private Long linkFilterId;

  private String linkType;

  private Long masterFieldId;

  private Long currentFormFieldId;

  private Long linkFormFieldId;

  private String method;
}
