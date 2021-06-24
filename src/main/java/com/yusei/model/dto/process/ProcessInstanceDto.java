package com.yusei.model.dto.process;

import com.yusei.model.dto.FieldValueDetailDto;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class ProcessInstanceDto {

  private String processInstanceId;

  private String processName;

  private String processStatus;

  private Date startTime;

  private Date endTime;

  private String sponsorId;

  private List<ActTaskDto> taskDtoList;

  private List<FieldValueDetailDto> summaryList;
}
