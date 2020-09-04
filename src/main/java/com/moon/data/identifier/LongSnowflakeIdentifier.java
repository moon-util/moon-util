package com.moon.data.identifier;

import com.moon.core.lang.RuntimeUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.FastTimestamp;
import com.moon.core.util.ValidationUtil;
import com.moon.data.IdentifierGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadLocalRandom;

/**
 * copied from Mybatis-Plus
 * <p>
 * 分布式高效有序ID生产黑科技(sequence)
 * <p>优化开源项目：https://gitee.com/yu120/sequence</p>
 *
 * @author moonsky
 */
public class LongSnowflakeIdentifier implements IdentifierGenerator<Long, Object> {

    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
     */
    private final long twepoch = 1598889600000L;
    /**
     * 机器标识位数
     */
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    /**
     * 毫秒内自增位
     */
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    /**
     * 时间戳左移动位
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private final long workerId;

    /**
     * 数据标识 ID 部分
     */
    private final long datacenterId;
    /**
     * 并发控制
     */
    private long sequence = 0L;
    /**
     * 上次生产 ID 时间戳
     */
    private long lastTimestamp = -1L;

    public LongSnowflakeIdentifier() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public LongSnowflakeIdentifier(long workerId, long datacenterId) {
        ValidationUtil.requireFalse(workerId > maxWorkerId || workerId < 0,
            String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        ValidationUtil.requireFalse(datacenterId > maxDatacenterId || datacenterId < 0,
            String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    public static LongSnowflakeIdentifier of() {
        return new LongSnowflakeIdentifier();
    }

    public static LongSnowflakeIdentifier of(long workerId, long datacenterId) {
        return new LongSnowflakeIdentifier(workerId, datacenterId);
    }

    /**
     * 获取 maxWorkerId
     */
    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        mpid.append(RuntimeUtil.getCurrentPID());
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 数据标识id部分
     */
    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }
        } catch (Exception e) {
            // TODO 异常警告
        }
        return id;
    }

    /**
     * 下一个 ID
     *
     * @return 下一个 ID
     */
    public synchronized long nextId() {
        long timestamp = nextTimestamp();
        // 闰秒
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = nextTimestamp();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format(
                            "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                            offset));
                    }
                } catch (Exception e) {
                    ThrowUtil.runtime(e);
                }
            } else {
                throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    offset));
            }
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 同一毫秒的序列数已经达到最大
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为 1 - 3 随机数
            sequence = ThreadLocalRandom.current().nextLong(1, 3);
        }

        lastTimestamp = timestamp;

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = nextTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = nextTimestamp();
        }
        return timestamp;
    }

    protected long nextTimestamp() {
        return FastTimestamp.currentTimeMillis();
    }

    /**
     * 生成 ID
     *
     * @param entity 数据实体
     * @param o      元数据，在 JPA 中，通常是指{@link SharedSessionContractImplementor}
     *
     * @return id
     */
    @Override
    public Long generateId(Object entity, Object o) { return nextId(); }
}
