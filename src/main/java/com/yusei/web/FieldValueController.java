package com.yusei.web;

import com.yusei.common.PageParam;
import com.yusei.common.PageResult;
import com.yusei.common.ReturnResult;
import com.yusei.model.dto.FieldValueDetailDto;
import com.yusei.model.dto.FieldValueRecordDto;
import com.yusei.model.dto.LinkQueryFieldValueDto;
import com.yusei.model.param.FieldValueAddParam;
import com.yusei.service.IFieldValueService;
import com.yusei.utils.ResponseUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fieldValue")
public class FieldValueController {

  @Autowired
  private IFieldValueService fieldValueService;

  @PostMapping("/addFormData")
  public ReturnResult addFormData(Long formId,
      @RequestBody List<FieldValueAddParam> fieldValueAddParams) {
    fieldValueService.addFormData(formId, fieldValueAddParams);
    return ResponseUtils.ok();
  }

  @GetMapping("/getFormDataByPage")
  public ReturnResult getFormDataByPage(PageParam pageParam, Long formId) {
    List<FieldValueRecordDto> fieldValueRecordDtos = fieldValueService
        .getFormDataByPage(pageParam, formId);
    Long total = fieldValueService.countFormData(formId);
    PageResult<FieldValueRecordDto> pageResult = new PageResult<>(fieldValueRecordDtos, total);
    return ResponseUtils.ok(pageResult);
  }

  @GetMapping("/getFormDataDetail")
  public ReturnResult getFormDataDetail(@RequestParam Long formId, @RequestParam Long primaryKeyValue) {
    List<FieldValueDetailDto> fieldValueDetailDtos = fieldValueService
        .getFormDataDetail(formId, primaryKeyValue);
    return ResponseUtils.ok(fieldValueDetailDtos);
  }

  @GetMapping("/getSelectFieldDataSource")
  public ReturnResult getSelectFieldDataSource(@RequestParam Long fieldId) {
    List<String> valueList = fieldValueService.getSelectFieldDataSource(fieldId);
    return ResponseUtils.ok(valueList);
  }

  @GetMapping("/getLinkQueryDataSource")
  public ReturnResult getLinkQueryDataSource(@RequestParam Long fieldId, String fieldValue) {
    List<LinkQueryFieldValueDto> dtoList = fieldValueService
        .getLinkQueryDataSource(fieldId, fieldValue);
    return ResponseUtils.ok(dtoList);
  }

}
