package com.yusei.service;

import com.yusei.common.PageParam;
import com.yusei.model.dto.FormDetailDto;
import com.yusei.model.entity.Field;
import com.yusei.model.entity.Form;
import com.yusei.model.param.FormAddParam;
import com.yusei.model.param.FormQueryParam;
import java.util.List;

public interface IFormService {


  Field initFormPrimaryKey(Long formId);

  String addForm(FormAddParam addParam);

  List<Form> getFormByPage(PageParam pageParam, FormQueryParam queryParam);

  FormDetailDto getFormDetailById(Long formId);

  Form getFormById(Long formId);
}
