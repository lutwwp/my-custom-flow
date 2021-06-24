package com.yusei.listener;

import com.yusei.constant.FlowConstant;
import com.yusei.extend.MyDeleteOtherExecutionCmd;
import com.yusei.extend.MyEndProcessCmd;
import com.yusei.utils.SpringUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

@Component
public class EndErrorFlowListener implements ExecutionListener {


  @Override
  public void notify(DelegateExecution execution) {
    //走失败结束节点的监听
    RuntimeService runtimeService = SpringUtils.getBean(RuntimeService.class);
    TaskService taskService = SpringUtils.getBean(TaskService.class);
    //将当前流程设置为流程失败
    runtimeService.setVariable(execution.getId(), FlowConstant.PROCESS_FAIL, true);
    if (execution.getSuperExecutionId() != null) {
      runtimeService.setVariable(execution.getSuperExecutionId(), FlowConstant.SUB_PROCESS_FAIL, true);
    }
    //将当前流程结束掉,走失败流程
    //要删除此执行实例以外的其他执行实例
    CommandExecutor commandExecutor = ((TaskServiceImpl) taskService).getCommandExecutor();
    ExecutionEntity executionEntity = (ExecutionEntity) execution;
    commandExecutor.execute(new MyDeleteOtherExecutionCmd(executionEntity, "失败流程处理"));
  }
}
