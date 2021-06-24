package com.yusei.model.entity;

import lombok.Data;

@Data
public class LinkField {

  private Long linkFieldId;

  private String linkType;

  private Long masterFieldId;

  private Long slaveFieldId;
}
