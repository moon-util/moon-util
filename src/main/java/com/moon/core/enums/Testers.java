package com.moon.core.enums;

import com.moon.validator.annotation.RequirePatternOf;
import com.moon.validator.annotation.RequireTesterOf;

import java.util.*;
import java.util.function.Predicate;

/**
 * 一系列检查函数，这个可作为参数传递也可用在注解中，而不只是静态方法调用
 *
 * @author moonsky
 * @see Patterns 检查字符串是否匹配规则
 * @see RequireTesterOf 可用于 hibernate-validator 验证
 * @see RequirePatternOf 可用于 hibernate-validator 验证
 */
@SuppressWarnings("rawtypes")
public enum Testers implements Predicate, EnumDescriptor {
    /**
     * true
     */
    TRUE {
        @Override
        public boolean test(Object o) { return true; }
    },
    /**
     * false
     */
    FALSE {
        @Override
        public boolean test(Object o) { return false; }
    },
    /**
     * 是 true
     */
    isTrue {
        @Override
        public boolean test(Object o) { return Boolean.TRUE.equals(o); }
    },
    /**
     * 是 false
     */
    isFalse {
        @Override
        public boolean test(Object o) { return Boolean.FALSE.equals(o); }
    },
    /**
     * 是否为 null
     */
    isNull {
        @Override
        public boolean test(Object o) { return o == null; }
    },
    /**
     * 是否为 null
     */
    isNotNull {
        @Override
        public boolean test(Object o) { return o != null; }
    },
    /**
     * 是空字符串
     */
    isEmptyString {
        @Override
        public boolean test(Object o) {
            return o instanceof CharSequence ? ((CharSequence) o).length() == 0 : (o == null);
        }
    },
    /**
     * 是空集合
     */
    isEmptyCollect {
        @Override
        public boolean test(Object o) { return o instanceof Collection ? ((Collection) o).isEmpty() : (o == null); }
    },
    /**
     * 是空 Map
     */
    isEmptyMap {
        @Override
        public boolean test(Object o) { return o instanceof Map ? ((Map) o).isEmpty() : (o == null); }
    },
    /**
     * 是空数组
     */
    isEmptyArray {
        @Override
        public boolean test(Object o) {
            return isArrayObject.test(o) ? Arrays2.getOrObjects(o).length(o) == 0 : (o == null);
        }
    },
    isEmptyIterable {
        @Override
        public boolean test(Object o) {
            return o instanceof Iterable ? (!((Iterable) o).iterator().hasNext()) : (o == null);
        }
    },
    isEmptyIterator {
        @Override
        public boolean test(Object o) { return o == null || ((o instanceof Iterator) && !((Iterator) o).hasNext()); }
    },
    /**
     * 是否为空
     */
    isEmptyObject {
        @Override
        public boolean test(Object o) {
            for (Testers tester : CachedIsEmpty.LIST) {
                if (tester.test(o)) {
                    return true;
                }
            }
            return false;
        }
    },
    /**
     * 是空白字符串
     */
    isBlankString {
        @Override
        public boolean test(Object o) { return o != null && o.toString().trim().length() == 0; }
    },
    /**
     * 是否是数组
     */
    isArrayObject {
        @Override
        public boolean test(Object o) { return o != null && o.getClass().isArray(); }
    },
    /**
     * 是数组类型
     */
    isArrayClass {
        @Override
        public boolean test(Object o) { return o instanceof Class && ((Class) o).isArray(); }
    },
    /**
     * 是否是枚举值
     */
    isEnumValue {
        @Override
        public boolean test(Object o) { return o != null && o.getClass().isEnum(); }
    },
    /**
     * 是否是枚举值
     */
    isEnumClass {
        @Override
        public boolean test(Object o) { return o instanceof Class && ((Class) o).isEnum(); }
    },
    /**
     * 是 0 值
     */
    isZeroInt {
        @Override
        public boolean test(Object o) {
            if (o instanceof Number) {
                return ((Number) o).intValue() == 0;
            }
            if (o instanceof CharSequence) {
                try {
                    return Integer.parseInt(o.toString()) == 0;
                } catch (Throwable ignored) { }
            }
            return false;
        }
    },
    /**
     * 是 0 值
     */
    isZeroLong {
        @Override
        public boolean test(Object o) {
            if (o instanceof Number) {
                return ((Number) o).longValue() == 0;
            }
            if (o instanceof CharSequence) {
                try {
                    return Long.parseLong(o.toString()) == 0;
                } catch (Throwable ignored) { }
            }
            return false;
        }
    },
    /**
     * 是 0 值
     */
    isZeroDouble {
        @Override
        public boolean test(Object o) {
            if (o instanceof Number) {
                return ((Number) o).doubleValue() == 0;
            }
            if (o instanceof CharSequence) {
                try {
                    return Double.parseDouble(o.toString()) == 0;
                } catch (Throwable ignored) { }
            }
            return false;
        }
    },
    /**
     * 负数
     */
    isNegative {
        @Override
        public boolean test(Object o) {
            if (o instanceof Number) {
                return ((Number) o).doubleValue() < 0;
            }
            if (o instanceof CharSequence) {
                try {
                    return Double.parseDouble(o.toString()) < 0;
                } catch (Throwable ignored) { }
            }
            return false;
        }
    },
    /**
     * 正数
     */
    isPositive {
        @Override
        public boolean test(Object o) {
            if (o instanceof Number) {
                return ((Number) o).doubleValue() > 0;
            }
            if (o instanceof CharSequence) {
                try {
                    return Double.parseDouble(o.toString()) > 0;
                } catch (Throwable ignored) { }
            }
            return false;
        }
    },
    /**
     * 奇数
     */
    isOdd {
        @Override
        public boolean test(Object o) {
            if (o instanceof Integer || o instanceof Short || o instanceof Byte) {
                int mod = ((Number) o).intValue() % 2;
                return mod == 1 || mod == -1;
            }
            if (o instanceof Long) {
                long mod = ((Long) o) % 2;
                return mod == 1 || mod == -1;
            }
            if (o instanceof CharSequence) {
                try {
                    long mod = Long.parseLong(o.toString()) % 2;
                    return mod == 1 || mod == -1;
                } catch (Throwable ignored) { }
            }
            return false;
        }
    },
    isEven {
        @Override
        public boolean test(Object o) {
            if (o instanceof Integer || o instanceof Short || o instanceof Byte) {
                return ((Number) o).intValue() % 2 == 0;
            }
            if (o instanceof Long) {
                return ((Long) o) % 2 == 0;
            }
            if (o instanceof CharSequence) {
                try {
                    return Long.parseLong(o.toString()) % 2 == 0;
                } catch (Throwable ignored) { }
            }
            return false;
        }
    },
    ;

    private static class CachedIsEmpty {

        final static List<Testers> LIST = new ArrayList<>();
    }

    public final Predicate not;

    Testers() {
        not = this.negate();
        if (name().startsWith("isEmpty") && !"isEmptyObject".equals(name())) {
            CachedIsEmpty.LIST.add(this);
        }
    }

    /**
     * 枚举信息
     *
     * @return 枚举信息
     */
    @Override
    public String getText() { return name(); }
}
