package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import lombok.Data;

@Data
public class LinkQueryFieldValueDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long fieldId;

  private String fieldValue;

  private List<FieldLinkInfoDto> fieldValueInfoDtos;
}
