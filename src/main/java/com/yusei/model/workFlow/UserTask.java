package com.yusei.model.workFlow;

import java.util.List;
import lombok.Data;

@Data
public class UserTask {
    private String id;
    private String name;
    private List<String> taskCandidateUsers;
    private String width;
    private String height;
    private String x;
    private String y;

    private String defaultFlow;

    //是否回退
    private Boolean rollBack;
    //是否撤销
    private Boolean revoke;
}
