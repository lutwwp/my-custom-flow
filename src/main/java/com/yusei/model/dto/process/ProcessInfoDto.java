package com.yusei.model.dto.process;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class ProcessInfoDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long processInfoId;

  private Integer version;

  private Integer status;
}
