package com.moon.core.enums;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.LongUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author moonsky
 */
public class PatternsTest {

    public final static Pattern IPV6 = Pattern.compile("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))");

    @Test
    void testUUIDLength() {
        String ipv6 = "2001:0410:0000:0000:FB00:1400:5000:45FF";
        System.out.println(IPV6.matcher(ipv6).matches());
        System.out.println(Patterns.IPV6.test(ipv6));

        ipv6 = "fe80:0000:0000:0000:0204:61ff:fe9d:f156";
        System.out.println(IPV6.matcher(ipv6).matches());
        System.out.println(Patterns.IPV6.test(ipv6));
    }

    public PatternsTest() {}

    private static String[] REGS = {
        "^((1?[1-9]?\\d|[1-2][0-4]\\d|25[0-5])\\.){3}(1?[1-9]?\\d|[1-2][0-4]\\d|25[0-5])$",
        "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b",
        "^(([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])$",
        "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$",
        "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$",
        "^(((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))$",
    };

    static String reg
        = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";

    private final static Pattern[] PATTERNS = new Pattern[REGS.length];

    static {
        for (int i = 0; i < REGS.length; i++) {
            PATTERNS[i] = Pattern.compile(REGS[i]);
        }
    }


    static String getIpv4() {
        int s0 = RandomUtil.nextInt(0, 256);
        int s1 = RandomUtil.nextInt(0, 256);
        int s2 = RandomUtil.nextInt(0, 256);
        int s3 = RandomUtil.nextInt(0, 256);
        int[] ss = {s0, s1, s2, s3};
        return JoinerUtil.join(ss, ".");
    }

    private static final String[] getIpv4Arr(int size) {
        List<String> ipv4List = ListUtil.newList(size);
        IteratorUtil.forEach(size, idx -> {
            ipv4List.add(getIpv4());
        });
        return ListUtil.toArray(ipv4List, String[]::new);
    }

    private static final long runningTime(String[] ipv4List, Pattern pattern) {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            for (String ipv4 : ipv4List) {
                pattern.matcher(ipv4).matches();
            }
        }
        long end = System.currentTimeMillis();
        return end - begin;
    }

    @Test
    @Disabled
    void testTest1() {
        Pattern[] definitions = PATTERNS;
        for (int i = 0; i < 100; i++) {
            String ipv4 = getIpv4();
            for (Pattern pattern : definitions) {
                assertTrue(pattern.matcher(ipv4).matches());
            }
        }
        int definitionsLen = definitions.length;
        Map<String, Pattern> patternMap = MapUtil.newHashMap();
        String[] ipv4Arr = getIpv4Arr(150);
        for (int i = 0; i < 100; i++) {
            int index = RandomUtil.nextInt(0, definitionsLen);
            String key = StringUtil.format("{} - {}", index, StringUtil.padStart(i, 3, '0'));
            patternMap.putIfAbsent(key, definitions[index]);
        }
        Map<String, Long> timeMap = MapUtil.newTreeMap();
        patternMap.forEach((key, pattern) -> {
            timeMap.put(key, runningTime(ipv4Arr, pattern));
        });

        Map<String, Long>[] times = new Map[REGS.length];
        timeMap.forEach((key, time) -> {
            int index = Integer.parseInt(key.substring(0, 1));
            Map<String, Long> indexedMap = times[index];
            if (indexedMap == null) {
                indexedMap = MapUtil.newTreeMap();
                times[index] = indexedMap;
            }
            indexedMap.put(key, time);
        });
        for (Map<String, Long> time : times) {
            String key = ListUtil.newList(time.keySet()).get(0);
            long[] values = LongUtil.toPrimitiveArr(0, CollectUtil.toArray(time.values(), Long[]::new));
            String sum = StringUtil.padStart(LongUtil.sum(values), 5, ' ');
            String avg = StringUtil.padStart(LongUtil.avg(values), 3, ' ');
            String template = "Index: {}, avg: {}ms, sum: {}ms, count: {}";
            String info = StringUtil.format(template, key, avg, sum, values.length);
            System.out.println(info);
        }

        System.out.println("==================================================");
        timeMap.forEach((key, time) -> {
            System.out.println(StringUtil.format("{} : {}", key, time));
        });
    }
}
