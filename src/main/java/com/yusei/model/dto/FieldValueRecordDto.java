package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import lombok.Data;

@Data
public class FieldValueRecordDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long id;

  private List<FieldValueInfoDto> fieldValueInfoDtos;
}
