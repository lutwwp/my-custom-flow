package com.yusei.extend;

import com.yusei.constant.FlowConstant;
import com.yusei.enums.ProcessFunctionEnum;
import java.io.Serializable;
import java.util.List;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.history.DeleteReason;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

public class MyEndProcessCmd implements Command<Void>, Serializable {

  protected String processInstanceId;
  protected String deleteReason;
  protected String processFunction;


  public MyEndProcessCmd(String processInstanceId, String deleteReason,String processFunction) {
    this.processInstanceId = processInstanceId;
    this.deleteReason = deleteReason;
    this.processFunction = processFunction;
  }

  @Override
  public Void execute(CommandContext commandContext) {
    HistoricProcessInstanceEntity historicProcessInstanceEntity = commandContext
        .getHistoricProcessInstanceEntityManager()
        .findById(processInstanceId);

    if (deleteReason == null) {
      deleteReason = DeleteReason.PROCESS_INSTANCE_DELETED;
    }

    ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
    //获取执行实例
    ExecutionEntity execution = executionEntityManager.findById(processInstanceId);
    for (ExecutionEntity subExecutionEntity : execution.getExecutions()) {
      if (subExecutionEntity.isMultiInstanceRoot()) {
        for (ExecutionEntity miExecutionEntity : subExecutionEntity.getExecutions()) {
          if (miExecutionEntity.getSubProcessInstance() != null) {
            executionEntityManager.deleteProcessInstance(
                miExecutionEntity.getSubProcessInstance().getProcessInstanceId(), deleteReason, true);
          }
        }

      } else if (subExecutionEntity.getSubProcessInstance() != null) {
        // 结束子流程实例
        executionEntityManager.deleteProcessInstance(
            subExecutionEntity.getSubProcessInstance().getProcessInstanceId(), deleteReason, true);
      }
    }

    //删除流程实例对应的进行中任务
    commandContext.getTaskEntityManager()
        .deleteTasksByProcessInstanceId(execution.getId(), deleteReason, true);

    if (commandContext.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
      commandContext.getProcessEngineConfiguration().getEventDispatcher()
          .dispatchEvent(ActivitiEventBuilder.createCancelledEvent(execution.getProcessInstanceId(),
              execution.getProcessInstanceId(), null, deleteReason));
    }
    //删除子执行实例
    List<ExecutionEntity> childExecutions = executionEntityManager
        .findChildExecutionsByParentExecutionId(processInstanceId);
    for (int i = childExecutions.size() - 1; i >= 0; i--) {
      ExecutionEntity childExecutionEntity = childExecutions.get(i);
      executionEntityManager
          .deleteExecutionAndRelatedData(childExecutionEntity, deleteReason, false);
    }

    //创建节点为结束节点的执行实例
    ExecutionEntity childExecution = commandContext.getExecutionEntityManager()
        .createChildExecution(execution);
    BpmnModel bpmnModel = ProcessDefinitionUtil
        .getBpmnModel(historicProcessInstanceEntity.getProcessDefinitionId());
    FlowElement theEnd = null;
    if (ProcessFunctionEnum.PROCESS_END_ERROR.name().equals(processFunction)) {
      theEnd = bpmnModel.getFlowElement("endError");
      if (theEnd == null) {
        theEnd = bpmnModel.getFlowElement("endSuccess");
      }
    }
    if (ProcessFunctionEnum.PROCESS_END_SUCCESS.name().equals(processFunction)) {
      theEnd = bpmnModel.getFlowElement("endSuccess");
    }
    childExecution.setCurrentFlowElement(theEnd);
    //继续当前流程
    commandContext.getAgenda().planContinueProcessOperation(childExecution);
    commandContext.getHistoryManager().recordProcessInstanceEnd(processInstanceId, deleteReason, null);
    return null;
  }

  public void deleteExecutionData(CommandContext commandContext,
      ExecutionEntity execution, String deleteReason, Boolean cascade) {
    if (deleteReason == null) {
      deleteReason = DeleteReason.PROCESS_INSTANCE_DELETED;
    }

    for (ExecutionEntity subExecutionEntity : execution.getExecutions()) {
      if (subExecutionEntity.isMultiInstanceRoot()) {
        for (ExecutionEntity miExecutionEntity : subExecutionEntity.getExecutions()) {
          if (miExecutionEntity.getSubProcessInstance() != null) {
            deleteExecutionData(commandContext, miExecutionEntity.getSubProcessInstance(),
                deleteReason, true);
          }
        }

      } else if (subExecutionEntity.getSubProcessInstance() != null) {
        deleteExecutionData(commandContext, subExecutionEntity.getSubProcessInstance(),
            deleteReason, true);
      }
    }
    //删除流程实例对应的进行中任务
    commandContext.getTaskEntityManager()
        .deleteTasksByProcessInstanceId(execution.getId(), deleteReason, true);

    if (commandContext.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
      commandContext.getProcessEngineConfiguration().getEventDispatcher()
          .dispatchEvent(ActivitiEventBuilder.createCancelledEvent(execution.getProcessInstanceId(),
              execution.getProcessInstanceId(), null, deleteReason));
    }

    //删除子执行实例
    List<ExecutionEntity> childExecutions = commandContext.getExecutionEntityManager()
        .findChildExecutionsByParentExecutionId(processInstanceId);
    for (int i = childExecutions.size() - 1; i >= 0; i--) {
      ExecutionEntity childExecutionEntity = childExecutions.get(i);
      commandContext.getExecutionEntityManager()
          .deleteExecutionAndRelatedData(childExecutionEntity, deleteReason, true);
    }

    commandContext.getExecutionEntityManager().deleteExecutionAndRelatedData(execution, deleteReason, false);
  }
}
