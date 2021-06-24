package com.yusei.model.dto.process;

import java.util.Date;
import lombok.Data;

@Data
public class ProcessLogDto {

  private String actName;

  private String userId;

  private Date startTime;

  private Date endTime;

  private Long duringTime;


}
