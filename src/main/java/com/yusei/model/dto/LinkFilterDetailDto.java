package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class LinkFilterDetailDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long linkFilterId;

  private String linkType;


  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long currentFormFieldId;

  private String currentFormFieldName;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long linkFormFieldId;

  private String linkFormFieldName;

  private String method;
}
