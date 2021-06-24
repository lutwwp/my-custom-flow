package com.yusei.extend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.DeleteReason;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

public class MyDeleteOtherExecutionCmd implements Command<Void>, Serializable {

  protected ExecutionEntity execution;
  protected String deleteReason;

  public MyDeleteOtherExecutionCmd(ExecutionEntity execution, String deleteReason) {
    this.execution = execution;
    this.deleteReason = deleteReason;
  }


  @Override
  public Void execute(CommandContext commandContext) {
    if (deleteReason == null) {
      deleteReason = DeleteReason.PROCESS_INSTANCE_DELETED;
    }
    String currentExecutionId = execution.getId();
    String processInstanceId = execution.getProcessInstanceId();
    List<String> noDeleteExecutionIds = new ArrayList<>();
    noDeleteExecutionIds.add(currentExecutionId);
    //获取所有父流程实例id，这些流程实例不能删除
    getParentExecution(commandContext, noDeleteExecutionIds, currentExecutionId);

    ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();

    //删除子执行实例
    List<ExecutionEntity> childExecutions = executionEntityManager
        .findChildExecutionsByParentExecutionId(processInstanceId);
    for (int i = childExecutions.size() - 1; i >= 0; i--) {
      ExecutionEntity childExecutionEntity = childExecutions.get(i);
      if (!noDeleteExecutionIds.contains(childExecutionEntity.getId())) {
        //先结束子流程实例
        if (childExecutionEntity.isMultiInstanceRoot()) {
          for (ExecutionEntity miExecutionEntity : childExecutionEntity.getExecutions()) {
            if (miExecutionEntity.getSubProcessInstance() != null) {
              executionEntityManager.deleteProcessInstance(
                  miExecutionEntity.getSubProcessInstance().getProcessInstanceId(), deleteReason, true);
            }
          }

        } else if (childExecutionEntity.getSubProcessInstance() != null) {
          // 结束子流程实例
          executionEntityManager.deleteProcessInstance(
              childExecutionEntity.getSubProcessInstance().getProcessInstanceId(), deleteReason, true);
        }
        //再删除流程实例对应任务
        List<TaskEntity> tasksByExecutionId = commandContext.getTaskEntityManager()
            .findTasksByExecutionId(childExecutionEntity.getId());
        if (tasksByExecutionId.size() > 0) {
          for (TaskEntity taskEntity : tasksByExecutionId) {
            commandContext.getTaskEntityManager().deleteTask(taskEntity,deleteReason,true,false);
          }
        }
        //外键原因最后删除流程实例
        executionEntityManager
            .deleteExecutionAndRelatedData(childExecutionEntity, deleteReason, false);
      }
    }
    return null;
  }

  private void getParentExecution(CommandContext commandContext, List<String> noDeleteExecutionIds, String currentExecutionId) {
    //获取执行实例
    ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
    ExecutionEntity execution = executionEntityManager.findById(currentExecutionId);
    if (execution.getParent() != null) {
      noDeleteExecutionIds.add(execution.getParentId());
      getParentExecution(commandContext, noDeleteExecutionIds, execution.getParentId());
    }
  }
}
