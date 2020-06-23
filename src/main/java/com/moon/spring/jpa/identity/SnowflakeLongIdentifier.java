package com.moon.spring.jpa.identity;

import com.moon.core.util.OSUtil;
import com.moon.core.util.RandomUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author benshaoye
 */
public class SnowflakeLongIdentifier implements IdentifierGenerator {

    // =================== fields ==========================================

    /**
     * 开始时间截
     */
    private final long starting = System.currentTimeMillis();

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据标识id所占的位数
     */
    private final long centerIdBits = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private final long maxCenterId = -1L ^ (-1L << centerIdBits);

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + centerIdBits;

    /**
     * 生成序列的掩码，这里为4095 ( 0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long centerId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    private final static SnowflakeLongIdentifier WORKER = new SnowflakeLongIdentifier();

    public static SnowflakeLongIdentifier of() {return WORKER; }

    public static SnowflakeLongIdentifier of(long workerId, long centerId) {
        return new SnowflakeLongIdentifier(workerId, centerId);
    }

    public SnowflakeLongIdentifier() { this(getWorkId(), getDataCenterId()); }

    // ============================ Constructors ====================================

    /**
     * 构造函数
     *
     * @param workerId 工作ID (0~31)
     * @param centerId 数据中心ID (0~31)
     */
    public SnowflakeLongIdentifier(long workerId, long centerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0",
                maxWorkerId));
        }
        if (centerId > maxCenterId || centerId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0",
                maxCenterId));
        }
        this.workerId = workerId;
        this.centerId = centerId;
    }

    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized Long nextId() {
        long timestamp = nextTimestamp();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = block2NewTimestamp(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - starting) << timestampLeftShift) | (centerId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     *
     * @return 当前时间戳
     */
    protected long block2NewTimestamp(long lastTimestamp) {
        long timestamp = nextTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = nextTimestamp();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long nextTimestamp() {
        Date date = new Date();
        return date.getTime();
    }

    private static Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] points = toCodePoints(hostAddress);
            int sums = 0;
            for (int b : points) {
                sums += b;
            }
            return (long) (sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtil.nextLong(0, 31);
        }
    }

    private static Long getDataCenterId() {
        int[] points = toCodePoints(getHostName());
        int sums = 0;
        for (int i : points) {
            sums += i;
        }
        return (long) (sums % 32);
    }

    static String getHostName() {
        return OSUtil.onWindows() ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME");
    }

    static int[] toCodePoints(final CharSequence str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new int[0];
        }

        final String s = str.toString();
        final int[] result = new int[s.codePointCount(0, s.length())];
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = s.codePointAt(index);
            index += Character.charCount(result[i]);
        }
        return result;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) { return nextId(); }
}
