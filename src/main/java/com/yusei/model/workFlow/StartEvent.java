package com.yusei.model.workFlow;

import lombok.Data;

@Data
public class StartEvent {
    private String id;
    private String name;
    private String width;
    private String height;
    private String x;
    private String y;

    private String defaultFlow;

}
