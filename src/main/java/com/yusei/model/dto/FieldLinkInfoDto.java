package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author liuqiang
 * @TOTD 关联
 * @date 2020/9/3 18:42
 */
@Data
public class FieldLinkInfoDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fieldId;

    private String fieldValue;

}
