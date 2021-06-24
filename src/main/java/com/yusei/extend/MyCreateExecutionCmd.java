package com.yusei.extend;

import java.io.Serializable;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

public class MyCreateExecutionCmd implements Command<Void>, Serializable {

  protected ExecutionEntity executionEntity;

  public MyCreateExecutionCmd(ExecutionEntity executionEntity) {
    this.executionEntity = executionEntity;
  }

  @Override
  public Void execute(CommandContext commandContext) {
//    commandContext.getExecutionEntityManager().insert(executionEntity);
    commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);
    return null;
  }
}
