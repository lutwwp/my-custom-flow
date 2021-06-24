package com.yusei.model.dto.process;

import com.yusei.model.dto.FieldValueDetailDto;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class ProcessTaskDto {

  private String processInstanceId;

  private String processName;

  private String processStatus;

  private Date startTime;

  private Date endTime;

  private String sponsorId;

  private String taskId;

  private String taskName;

  private String taskDefinitionKey;

  private Date taskCreateTime;

  /**
   * 任务是否领取.
   */
  private Boolean claimTask;

  private List<FieldValueDetailDto> summaryList;
}
