package com.yusei.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndEventListener implements ExecutionListener {

  @Autowired
  private RuntimeService runtimeService;

  @Override
  public void notify(DelegateExecution execution) {
    execution.getProcessInstanceId();
    execution.getProcessDefinitionId();
    execution.getVariables();
  }
}
