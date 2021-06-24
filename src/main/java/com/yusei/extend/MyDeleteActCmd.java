package com.yusei.extend;

import java.io.Serializable;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;

public class MyDeleteActCmd implements Command<Void>, Serializable {

  protected HistoricActivityInstanceEntity activityInstanceEntity;

  public MyDeleteActCmd(HistoricActivityInstanceEntity activityInstanceEntity) {
    this.activityInstanceEntity = activityInstanceEntity;
  }

  @Override
  public Void execute(CommandContext commandContext) {
    //删除历史活动
    commandContext.getHistoricActivityInstanceEntityManager().delete(activityInstanceEntity);
    return null;
  }
}
