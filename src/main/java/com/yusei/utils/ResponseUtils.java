package com.yusei.utils;

import com.yusei.common.ReturnResult;

public class ResponseUtils {

  private static final Integer OK_CODE = 0;

  private static final Integer ERROR_CODE = 1;

  private static final String OK_MESSAGE = "OK";

  private static final String ERROR_MESSAGE = "ERROR";

  public static <T> ReturnResult init(Integer code, String message, T data) {
    return new ReturnResult<>(code, message, data);
  }

  public static ReturnResult ok() {
    return init(OK_CODE, OK_MESSAGE, null);
  }

  public static <T> ReturnResult ok(T data) {
    return init(OK_CODE, OK_MESSAGE, data);
  }

  public static ReturnResult error() {
    return init(ERROR_CODE, ERROR_MESSAGE, null);
  }

  public static ReturnResult error(String message) {
    return init(ERROR_CODE, message, null);
  }
}
