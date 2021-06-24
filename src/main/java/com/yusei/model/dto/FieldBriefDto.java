package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yusei.model.param.SubFieldAddParam;
import lombok.Data;

import java.util.List;

@Data
public class FieldBriefDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fieldId;

  private String fieldCode;

  private String fieldName;

  private String fieldType;

  private List<SubFieldAddParam> subFieldAddParam;
}
