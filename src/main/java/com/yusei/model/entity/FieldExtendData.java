package com.yusei.model.entity;

import java.util.Date;
import java.math.BigDecimal;
import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * FieldExtendData实体类
 * @author generated by liuqiang
 * @date 2020-08-31 16:42:39
 */
@Data
@Accessors(chain = true)
public class FieldExtendData implements Serializable {

  /**
   * 
   */
  private Long fieldExtendDataId;
  /**
   * 
   */
  private Long formId;
  /**
   * 
   */
  private Long fieldId;
  /**
   * 
   */
  private String type;
  /**
   * 
   */
  private Boolean selected;
  /**
   * 
   */
  private String name;
  /**
   * 
   */
  private String value;

  private String dataType;
}