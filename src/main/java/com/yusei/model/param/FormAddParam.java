package com.yusei.model.param;

import java.util.List;
import lombok.Data;

@Data
public class FormAddParam {

  private Long formId;

  private String formName;

  private String formType;


  List<FieldAddParam> fieldAddParams;
}
