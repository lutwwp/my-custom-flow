package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import lombok.Data;

@Data
public class FormDetailDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long formId;

  private String formName;

  private String formType;

  private List<FieldDetailDto> fieldDetailDtos;
}
