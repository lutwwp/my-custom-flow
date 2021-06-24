package com.yusei.service;

import com.yusei.common.PageParam;
import com.yusei.model.dto.FieldValueDetailDto;
import com.yusei.model.dto.FieldValueInfoDto;
import com.yusei.model.dto.FieldValueRecordDto;
import com.yusei.model.dto.LinkQueryFieldValueDto;
import com.yusei.model.entity.FieldValue;
import com.yusei.model.param.FieldValueAddParam;
import java.util.List;
import java.util.Map;

public interface IFieldValueService {

  Long addFormData(Long formId, List<FieldValueAddParam> fieldValueAddParams);

  Map<Long, String> getByFormIdAndPrimaryKeyValue(Long formId, Long primaryKeyValue);

  void updateFormData(Long formId, List<FieldValueAddParam> fieldValueAddParams, Long primaryKeyValue);

  List<FieldValueRecordDto> getFormDataByPage(PageParam pageParam, Long formId);

  Long countFormData(Long formId);

  List<FieldValueDetailDto> getFormDataDetail(Long formId, Long primaryKeyValue);

  List<String> getSelectFieldDataSource(Long fieldId);

  List<LinkQueryFieldValueDto> getLinkQueryDataSource(Long fieldId, String fieldValue);

  List<FieldValue> getByFormIdAndFieldId(Long formId, Long fieldId);
}
