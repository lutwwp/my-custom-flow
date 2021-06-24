package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class FormInfoDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long formId;

  private String formName;

  private String formType;

  public String getFormIdStr() {
    if (formId == null) {
      return null;
    } else {
      return formId.toString();
    }
  }
}
