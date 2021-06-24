package com.yusei.model.workFlow;

import java.util.List;
import lombok.Data;

@Data
public class FlowChart {

  private ProcessParam processParam;

  private List<String> runtimeTaskKeyList;

  private List<String> historicTaskKeyList;
}
