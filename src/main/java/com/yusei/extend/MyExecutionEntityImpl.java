package com.yusei.extend;

import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;

public class MyExecutionEntityImpl extends ExecutionEntityImpl {

  protected String activityId;

  @Override
  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }
}
