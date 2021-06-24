package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import lombok.Data;

@Data
public class FieldValueDetailDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fieldId;

  private String fieldName;

  private String fieldType;

  private String fieldValue;

  private Boolean showField;

  private Boolean editField;

  private List<LinkFieldDetailDto> linkFields;

  private List<LinkFilterDetailDto> linkFilters;

  private List<FieldValueDetailDto> linkFieldDetailDtos;
}
