package com.yusei.listener;

import com.yusei.model.entity.ProcessInfo;
import com.yusei.model.entity.ProcessTrigger;
import com.yusei.service.IActivitiService;
import com.yusei.service.IProcessInfoService;
import com.yusei.service.IProcessTriggerService;
import com.yusei.utils.SpringUtils;
import java.util.List;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FlowTriggerListener implements ExecutionListener {

  @Override
  public void notify(DelegateExecution execution) {
    RuntimeService runtimeService = SpringUtils.getBean(RuntimeService.class);
    IProcessTriggerService processTriggerService = SpringUtils.getBean(IProcessTriggerService.class);
    IProcessInfoService processInfoService = SpringUtils.getBean(IProcessInfoService.class);
    IActivitiService activitiService = SpringUtils.getBean(IActivitiService.class);

    //获取当前流程的所有的variables
    Map<String, Object> variables = runtimeService.getVariables(execution.getId());

    //找到当前监控对象对应的触发流程
    ProcessInfo processInfo = processInfoService
        .getProcessInfoByProcessDefinitionId(execution.getProcessDefinitionId());
    List<ProcessTrigger> processTriggers = processTriggerService
        .getByProcessInfoIdAndFlowKey(processInfo.getProcessInfoId(), execution.getCurrentActivityId());
    if (processTriggers.size() > 0) {
      for (ProcessTrigger processTrigger : processTriggers) {
        activitiService.startTriggerProcess(variables, processTrigger);
      }
    }
  }
}
