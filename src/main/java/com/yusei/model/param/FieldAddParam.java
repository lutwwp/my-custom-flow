package com.yusei.model.param;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class FieldAddParam {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fieldId;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long formId;

  private String fieldCode;

  private String fieldName;

  private String fieldType;

  private Long linkFormId;

  private Long linkFieldId;

  private String type;

  private String defaultValueType;

  private String defaultValue;

  private Boolean necessary;

  private Boolean allowRepeated;

  private Boolean showField;

  private Boolean editField;

  private List<Long> linkedFieldIds;

  private List<LinkFilterAddParam> linkFilterAddParams;

  private List<ExtendDataParam> extendDataParams;

  private List<ExtendRuleParam> extendRuleParams;

  private List<FieldAddParam> subFieldAddParams;
}
