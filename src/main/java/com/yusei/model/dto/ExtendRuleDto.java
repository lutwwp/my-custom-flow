package com.yusei.model.dto;


import com.yusei.model.param.ExtendRuleDetailParam;
import lombok.Data;

import java.util.List;

/**
 * @author liuqiang
 * @TOTD  扩展规则
 * @date 2020/9/2 13:29
 */
@Data
public class ExtendRuleDto {

    /**
     * com.yusei.enums.ExtendRuleTypeEnum
     */
    private String type;

    private String name;

    private List<ExtendRuleDetailDto> detailDtos;
}
