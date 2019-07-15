package com.songshuang.snowflake.generator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 根据Twitter的snowflake算法结合zookeeper实现动态machine id生成ID
 */
public class SnowFlakeGenerator {

  public static long MATCH_ID = 0;

  private static final int TOTAL_LENGTH = 64;

  private static final int TIME_LENGTH = 41;

  private static final int MACHINE_LENGTH = 10;

  private static final int SEQUENCE_LENGTH = 12;

  private static final long MAX_SEQUENCE = ~(-1 << SEQUENCE_LENGTH);

  private static AtomicLong sequenceNumber = new AtomicLong(0);

  private static volatile long number = 0;

  private static volatile long initNumber = 0L;

  private static final Object lock = new Object();

  private static Long getTime() {
    long time = -1 << (MACHINE_LENGTH + SEQUENCE_LENGTH);
    long currentTime = System.currentTimeMillis();

    return currentTime & time;
  }

  private static Long getSequence() {
    if (sequenceNumber.get() == MAX_SEQUENCE) {
      sequenceNumber.set(0);
    }

    number = sequenceNumber.getAndAdd(1);

    return ~(-1 << SEQUENCE_LENGTH) & number;
  }

  private static Long getMachine() {
   return  (-1 << SEQUENCE_LENGTH) & (-1L >>> (TOTAL_LENGTH - MACHINE_LENGTH - SEQUENCE_LENGTH)) & MATCH_ID;
  }

  private static Long init() {
    return getTime() << (SEQUENCE_LENGTH + MACHINE_LENGTH) | getMachine() << SEQUENCE_LENGTH;
  }

  public static Long getId() {
    if (initNumber == 0L) {
      synchronized (lock) {
        initNumber = init();
      }
    }

    return initNumber | getSequence();
  }
}
