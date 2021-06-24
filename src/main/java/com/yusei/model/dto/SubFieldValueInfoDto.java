package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class SubFieldValueInfoDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fieldId;

  private String fieldValue;
}
