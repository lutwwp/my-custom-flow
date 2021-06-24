package com.yusei.utils;

import com.yusei.common.SnowFlake;
import java.util.UUID;

public class IdWorker {

  private static final SnowFlake worker = new SnowFlake();

  public static long getId() {
    return worker.nextId();
  }

//  public static long getId() {
//    String idStr = UUID.randomUUID().toString().replace("-", "");
//    return Long.valueOf(idStr);
//  }
}
