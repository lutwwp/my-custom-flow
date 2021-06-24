package com.yusei.listener;

import com.yusei.constant.FlowConstant;
import com.yusei.enums.DefaultValueTypeEnum;
import com.yusei.enums.FieldTypeEnum;
import com.yusei.model.entity.Field;
import com.yusei.model.entity.ProcessInfo;
import com.yusei.model.entity.ProcessNodeField;
import com.yusei.model.param.FieldValueAddParam;
import com.yusei.service.IFieldService;
import com.yusei.service.IFieldValueService;
import com.yusei.service.IProcessInfoService;
import com.yusei.service.IProcessNodeFieldService;
import com.yusei.utils.SpringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class StartFlowListener implements ExecutionListener {

  @Override
  public void notify(DelegateExecution execution) {
    System.out.println("流程开始啦");
    //监控子流程开启流程时，为流程创建业务数据
    RuntimeService runtimeService = SpringUtils.getBean(RuntimeService.class);
    IProcessNodeFieldService processNodeFieldService = SpringUtils
        .getBean(IProcessNodeFieldService.class);
    IProcessInfoService processInfoService = SpringUtils.getBean(IProcessInfoService.class);
    IFieldService fieldService = SpringUtils.getBean(IFieldService.class);
    IFieldValueService fieldValueService = SpringUtils.getBean(IFieldValueService.class);
    if (StringUtils.isEmpty(execution.getProcessInstanceBusinessKey())) {
      //如果流程开启后未创建表单数据，自动创建一条数据
      ProcessInfo processInfo = processInfoService
          .getProcessInfoByProcessDefinitionId(execution.getProcessDefinitionId());
      Long formId = processInfo.getFormId();
      //流程启动时表单需要新增的数据
      List<FieldValueAddParam> fieldValueAddParams = new ArrayList<>();
      //查询当前流程父流程的全局参数
      Map<String, Object> variables = null;
      if (execution.getParent() != null && execution.getParent().getSuperExecutionId() != null) {
        variables = runtimeService.getVariables(execution.getParent().getSuperExecutionId());
      }
      if (variables != null) {
        //获取当前流程表单启动时需要新增的字段
        List<ProcessNodeField> processNodeFields = processNodeFieldService
            .getByInfoIdAndTaskKey(processInfo.getProcessInfoId(), "theStart");
        for (ProcessNodeField processNodeField : processNodeFields) {
          if (processNodeField.getEditField() != null && processNodeField.getEditField()) {
            FieldValueAddParam fieldValueAddParam = new FieldValueAddParam();
            Long fieldId = processNodeField.getFieldId();
            fieldValueAddParam.setFieldId(fieldId);
            Field fieldInfoById = fieldService.getFieldInfoById(fieldId);
            //查询此字段的关联值
            if (FieldTypeEnum.SELECT.name().equals(fieldInfoById.getFieldType())) {
              if (DefaultValueTypeEnum.LINK_FORM.name()
                  .equals(fieldInfoById.getDefaultValueType())) {
                //下拉框的关联表单
                Long linkFieldId = fieldInfoById.getLinkFieldId();
                Object fieldValue = variables.get("field" + linkFieldId);
                if (fieldValue != null) {
                  fieldValueAddParam.setFieldValue(fieldValue.toString());
                  fieldValueAddParams.add(fieldValueAddParam);
                }
              }
            }
          }
        }
      }

      //当前流程新增表单数据
      Long primaryKeyValue = fieldValueService.addFormData(formId, fieldValueAddParams);
      runtimeService
          .updateBusinessKey(execution.getProcessInstanceId(), primaryKeyValue.toString());

      Map<String, Object> thisVariables = new HashMap<>();
      thisVariables.put(FlowConstant.PROCESS_FAIL, false);
      for (FieldValueAddParam param : fieldValueAddParams) {
        thisVariables.put("field" + param.getFieldId(), param.getFieldValue());
      }
      runtimeService.setVariables(execution.getProcessInstanceId(), thisVariables);
    }
  }
}
