package com.yusei.model.dto.process;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yusei.model.entity.ProcessGuarderAbility;
import com.yusei.model.entity.ProcessNodeAttribute;
import com.yusei.model.entity.ProcessNodeField;
import com.yusei.model.entity.ProcessTrigger;
import com.yusei.model.workFlow.ProcessParam;
import java.util.List;
import lombok.Data;

/**
 * 流程的详细信息，包含流程图参数，监听人，触发器以及流程节点字段
 */
@Data
public class ProcessDetailInfoDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Long processInfoId;

  private Integer version;

  private Integer status;

  private ProcessParam processParam;

  private List<ProcessNodeFieldDetailDto> processNodeFields;

  private List<ProcessNodeAttributeDto> processNodeAttributes;

  private List<ProcessTriggerDto> processTriggers;

  private List<ProcessGuarderAbilityDto> processGuarderAbilities;

}
