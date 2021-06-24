package com.yusei.extend;

import java.io.Serializable;
import java.util.Collection;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

public class MyDeleteTaskCmd implements Command<Void>, Serializable {

  private static final long serialVersionUID = 1L;
  protected String taskId;
  protected Collection<String> taskIds;
  protected boolean cascade;
  protected String deleteReason;
  protected TaskEntity task;
  protected ExecutionEntity executionEntity;
  protected HistoricActivityInstanceEntity activityInstanceEntity;

  public MyDeleteTaskCmd(TaskEntity task, ExecutionEntity executionEntity,
      HistoricActivityInstanceEntity activityInstanceEntity, String deleteReason, boolean cascade) {
    this.task = task;
    this.executionEntity = executionEntity;
    this.activityInstanceEntity = activityInstanceEntity;
    this.cascade = cascade;
    this.deleteReason = deleteReason;
  }

  @Override
  public Void execute(CommandContext commandContext) {
    if (task != null) {
      deleteRuntimeTask(commandContext);
    } else {
      throw new ActivitiIllegalArgumentException("task is null");
    }

    return null;
  }

  protected void deleteRuntimeTask(CommandContext commandContext) {
    //1.删除当前任务和历史任务
    commandContext.getTaskEntityManager().deleteTask(task, deleteReason, cascade, true);
    //2.删除当前任务的execution
    commandContext.getExecutionEntityManager().delete(executionEntity);
    //3.删除历史活动
    commandContext.getHistoryManager().recordActivityEnd(executionEntity, deleteReason);
  }
}
