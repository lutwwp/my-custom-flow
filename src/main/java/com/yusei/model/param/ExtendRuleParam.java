package com.yusei.model.param;


import lombok.Data;

import java.util.List;

/**
 * @author liuqiang
 * @TOTD  扩展规则
 * @date 2020/9/2 13:29
 */
@Data
public class ExtendRuleParam {

    /**
     * com.yusei.enums.ExtendRuleTypeEnum
     */
    private String type;

    private String name;

    private List<ExtendRuleDetailParam> detailParams;
}
