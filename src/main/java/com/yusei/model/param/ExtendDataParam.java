package com.yusei.model.param;

import lombok.Data;

/**
 * @author liuqiang
 * @TOTD   扩展数据
 * @date 2020/8/31 16:25
 */
@Data
public class ExtendDataParam {

    private String name;

    private String value;

    private Boolean selected;

    private String dataType;

    private String type;
}
