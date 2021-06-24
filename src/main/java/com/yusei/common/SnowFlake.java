//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yusei.common;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.logging.Logger;
import org.springframework.util.StringUtils;

public class SnowFlake {
  private static final Logger log = Logger.getLogger(SnowFlake.class.getName());
  private static final long START_STMP = 1480166465631L;
  private static final long SEQUENCE_BIT = 6L;
  private static final long MACHINE_BIT = 5L;
  private static final long DATACENTER_BIT = 5L;
  private static final long MAX_DATACENTER_NUM = 31L;
  private static final long MAX_MACHINE_NUM = 31L;
  private static final long MAX_SEQUENCE = 63L;
  private static final long MACHINE_LEFT = 6L;
  private static final long DATACENTER_LEFT = 11L;
  private static final long TIMESTMP_LEFT = 16L;
  private long dataCenterId;
  private long machineId;
  private long sequence = 0L;
  private long lastStmp = -1L;
  private final long maxDataCenterId = 31L;
  private final long maxWorkerId = 31L;

  public SnowFlake() {
    this.dataCenterId = getDataCenterId(31L);
    this.machineId = getMaxWorkerId(this.dataCenterId, 31L);
    log.info("dataCenterId:" + this.dataCenterId);
    log.info("machineId:" + this.machineId);
  }

  public SnowFlake(long dataCenterId, long machineId) {
    if (dataCenterId <= 31L && dataCenterId >= 0L) {
      if (machineId <= 31L && machineId >= 0L) {
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
      } else {
        throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
      }
    } else {
      throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
    }
  }

  public synchronized long nextId() {
    long currStmp = this.getNewstmp();
    if (currStmp < this.lastStmp) {
      throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
    } else {
      if (currStmp == this.lastStmp) {
        this.sequence = this.sequence + 1L & 63L;
        if (this.sequence == 0L) {
          currStmp = this.getNextMill();
        }
      } else {
        this.sequence = 0L;
      }

      this.lastStmp = currStmp;
      return currStmp - 1480166465631L << 16 | this.dataCenterId << 11 | this.machineId << 6 | this.sequence;
    }
  }

  private long getNextMill() {
    long mill;
    for(mill = this.getNewstmp(); mill <= this.lastStmp; mill = this.getNewstmp()) {
      ;
    }

    return mill;
  }

  private long getNewstmp() {
    return System.currentTimeMillis();
  }

  public static void main(String[] args) {
    System.out.println(31L);
    System.out.println(31L);
    System.out.println(63L);
  }

  protected static long getMaxWorkerId(long dataCenterId, long maxWorkerId) {
    StringBuilder mpid = new StringBuilder();
    mpid.append(dataCenterId);
    String name = ManagementFactory.getRuntimeMXBean().getName();
    if (!StringUtils.isEmpty(name)) {
      mpid.append(name.split("@")[0]);
    }

    return (long)(mpid.toString().hashCode() & '\uffff') % (maxWorkerId + 1L);
  }

  protected static long getDataCenterId(long maxDataCenterId) {
    long id = 0L;

    try {
      InetAddress ip = InetAddress.getLocalHost();
      NetworkInterface network = NetworkInterface.getByInetAddress(ip);
      if (network == null) {
        id = 1L;
      } else {
        byte[] mac = network.getHardwareAddress();
        id = (255L & (long)mac[mac.length - 1] | 65280L & (long)mac[mac.length - 2] << 8) >> 6;
        id %= maxDataCenterId + 1L;
      }
    } catch (Exception var7) {
      log.info(" getDatacenterId: " + var7.getMessage());
    }

    return id;
  }
}
