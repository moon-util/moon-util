package com.moon.core.util.concurrent;

import com.moon.core.lang.JoinerUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * @author moonsky
 */
public class ConcurrentHashMapTest {

    int value;
    String template;

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c; Type[] ts, as; Type t; ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                        ((p = (ParameterizedType)t).getRawType() ==
                            Comparable.class) &&
                        (as = p.getActualTypeArguments()) != null &&
                        as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    void running(IntConsumer consumer, int count){
        running(consumer, 0, count);
    }

    void running(IntConsumer consumer, int start, int count){
        for (int i = start; i < count; i++) {
            consumer.accept(i);
        }
    }

    @Test
    void testSpread() {
        template = "Spread {origin: %d, spread: %d}";
        List<String> values = new ArrayList<>();
        running(i -> {
            int sized = spread(i);
            if (sized != i){
                String ret = String.format(template, i, sized);
                values.add(ret);
            }
        }, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println(values.size());
        System.out.println(JoinerUtil.join(values, "\n"));
    }

    @Test
    void testTableSizeFor() {
        int value = 9;
        template = "TableSizeFor {origin: %d, sized: %d}";
        running(i -> {
            int sized = tableSizeFor(i);
            String ret = String.format(template, i, sized);
            System.out.println(ret);
        }, value);
    }
}
