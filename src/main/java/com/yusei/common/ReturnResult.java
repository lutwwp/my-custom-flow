package com.yusei.common;

public class ReturnResult<T> {

  private Integer code;

  private String msg;

  private T data;

  public ReturnResult(){}

  public ReturnResult(Integer code, String message, T data){
    this.code = code;
    this.msg = message;
    this.data = data;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
