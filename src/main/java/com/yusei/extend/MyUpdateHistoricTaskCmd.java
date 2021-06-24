package com.yusei.extend;

import java.io.Serializable;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;

public class MyUpdateHistoricTaskCmd implements Command<Void>, Serializable {

  protected HistoricTaskInstanceEntity historicTaskInstanceEntity;

  protected HistoricActivityInstanceEntity historicActivityInstanceEntity;

  public MyUpdateHistoricTaskCmd(HistoricTaskInstanceEntity historicTaskInstanceEntity,
      HistoricActivityInstanceEntity historicActivityInstanceEntity) {
    this.historicTaskInstanceEntity = historicTaskInstanceEntity;
    this.historicActivityInstanceEntity = historicActivityInstanceEntity;
  }

  @Override
  public Void execute(CommandContext commandContext) {
    commandContext.getHistoricActivityInstanceEntityManager().update(historicActivityInstanceEntity);
    commandContext.getHistoricTaskInstanceEntityManager().update(historicTaskInstanceEntity);
    return null;
  }
}
