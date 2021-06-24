package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

import lombok.Data;

@Data
public class FieldDetailDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fieldId;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long formId;

  private String fieldCode;

  private String fieldName;

  private String fieldType;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long linkFormId;

  private String linkFormName;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long linkFieldId;

  private String linkFieldName;

  private String defaultValueType;

  private String defaultValue;

  private Boolean necessary;

  private Boolean allowRepeated;

  private Boolean showField;

  private Boolean editField;

  private List<LinkFieldDetailDto> linkFields;

  private List<LinkFilterDetailDto> linkFilters;

  private List<ExtendDataDto> extendDatas;

  private List<ExtendRuleDto> extendRules;

  private List<SubFieldDto> subFieldDtos;
}
