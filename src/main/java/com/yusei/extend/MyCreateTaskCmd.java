package com.yusei.extend;

import java.io.Serializable;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

public class MyCreateTaskCmd implements Command<Void>, Serializable {

  protected TaskEntity taskEntity;


  public MyCreateTaskCmd(TaskEntity taskEntity) {
    this.taskEntity = taskEntity;
  }

  @Override
  public Void execute(CommandContext commandContext) {
    commandContext.getTaskEntityManager().insert(taskEntity);
    return null;
  }
}
