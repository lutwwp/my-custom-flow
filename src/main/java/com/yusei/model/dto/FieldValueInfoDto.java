package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class FieldValueInfoDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fieldId;

  private String fieldValue;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long groupId;

  private List<FieldValueInfoDto> subFieldValueInfoDtos;
}
