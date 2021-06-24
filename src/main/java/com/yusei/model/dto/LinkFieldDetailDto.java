package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class LinkFieldDetailDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long linkFieldId;

  private String linkType;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long slaveFieldId;

  private String slaveFieldName;
}
