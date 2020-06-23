package com.giot.eco_building.utils;

/**
 * @Author: pyt
 * @Date: 2020/6/10 14:54
 * @Description:
 */
public class SnowflakeIdWorker{
    /**
     * 开始时间戳
     */
    private final long START_STAMP = 1591772439070l;
    /**
     * 机器码：10位
     */
    private final long machineIdBits = 10l;
    private final long maxMachineId = -1l ^ (-1l << machineIdBits);
    /**
     * 序列号：12位
     */
    private final long sequenceBits = 12l;
    /**
     * 序列号掩码
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    /**
     * 机器码左移12位
     */
    private final long machineIdLeftShift = sequenceBits;
    /**
     * 时间戳左移22位
     */
    private final long timestampLeftShift = sequenceBits + machineIdBits;
    /**
     * 机器码
     */
    private long machineId;
    /**
     * 序列号
     */
    private long sequence = 0l;
    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1l;

    /**
     * 构造器
     *
     * @param machineId
     */
    private SnowflakeIdWorker(long machineId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",
                    maxMachineId));
        }
        this.machineId = machineId;
    }

    private volatile static SnowflakeIdWorker worker;

    public static SnowflakeIdWorker getInstance(Long machineId){
        if (worker == null){
            synchronized (SnowflakeIdWorker.class){
                if (worker==null){
                    worker = new SnowflakeIdWorker(machineId);
                }
            }
        }
        return worker;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - START_STAMP) << timestampLeftShift)
                | (machineId << machineIdLeftShift)
                | (sequence);
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
}
