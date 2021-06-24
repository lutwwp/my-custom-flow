package com.yusei.model.dto.process;

import com.yusei.model.dto.FieldValueDetailDto;
import com.yusei.model.entity.ProcessNodeAttribute;
import java.util.List;
import lombok.Data;

@Data
public class ProcessDetailDto {

  private List<FieldValueDetailDto> fieldValueDetailDtos;

  private ProcessNodeAttribute processNodeAttribute;

  /**
   * 监控人的按钮权限.
   */
  private List<String> processFunctions;
}
