package com.moon.more.validator.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 数值集合/数组验证器
 *
 * @author benshaoye
 */
public abstract class CollectValidator extends BaseValidator {

    /**
     * （缓存，方便快速定位）要求数字数组至少有一项符合要求
     */
    private final static Map<Class, Matcher> ANY_MAPPED = new HashMap<>();
    /**
     * （缓存，方便快速定位）要求数字数组所有项符合要求
     */
    private final static Map<Class, Matcher> ALL_MAPPED = new HashMap<>();
    /**
     * 匹配类型，区分是至少有一项符合要求或者所有项符合要求
     * 在验证数组时会用到
     */
    protected final MatchType matchType;
    /**
     * 是否要求所有项都匹配
     * 在验证集合是会用到
     */
    protected final boolean requireAllMatch;

    protected CollectValidator(Set<? extends Number> values, boolean requireAllMatch) {
        super(values);
        this.requireAllMatch = requireAllMatch;
        this.matchType = requireAllMatch ? MatchType.ALL : MatchType.ANY;
    }

    /**
     * 数组验证器，集合验证也可以这样实现，但是没必要
     */
    interface Matcher {

        /**
         * 检测
         *
         * @param value     数组字段
         * @param validator 数组验证器实际类型
         *
         * @return 是否验证通过
         */
        boolean test(Object value, CollectValidator validator);
    }

    /**
     * 至少有一项匹配
     */
    protected enum AnyArrayMatch implements Matcher {
        /**
         * 数组
         */
        OBJECTS(null) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                Object[] arr = (Object[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Object val = arr[i];
                    if (validator.originalValid(val == null ? null : val.toString())) {
                        return true;
                    }
                }
                return false;
            }
        },
        BYTES(byte[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                byte[] arr = (byte[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Byte val = Byte.valueOf(arr[i]);
                    if (validator.originalValid(val)) {
                        return true;
                    }
                }
                return false;
            }
        },
        SHORTS(short[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                short[] arr = (short[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Short val = Short.valueOf(arr[i]);
                    if (validator.originalValid(val)) {
                        return true;
                    }
                }
                return false;
            }
        },
        INTS(int[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                int[] arr = (int[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Integer val = Integer.valueOf(arr[i]);
                    if (validator.originalValid(val)) {
                        return true;
                    }
                }
                return false;
            }
        },
        LONGS(long[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                long[] arr = (long[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Long val = Long.valueOf(arr[i]);
                    if (validator.originalValid(val)) {
                        return true;
                    }
                }
                return false;
            }
        },
        FLOATS(float[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                float[] arr = (float[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Float val = Float.valueOf(arr[i]);
                    if (validator.originalValid(val)) {
                        return true;
                    }
                }
                return false;
            }
        },
        DOUBLES(double[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                double[] arr = (double[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Double val = Double.valueOf(arr[i]);
                    if (validator.originalValid(val)) {
                        return true;
                    }
                }
                return false;
            }
        },
        CHARS(char[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                char[] arr = (char[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Character val = Character.valueOf(arr[i]);
                    if (validator.originalValid(val)) {
                        return true;
                    }
                }
                return false;
            }
        },
        BOOLEANS(boolean[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                boolean[] arr = (boolean[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Boolean val = Boolean.valueOf(arr[i]);
                    if (validator.originalValid(val)) {
                        return true;
                    }
                }
                return false;
            }
        };

        AnyArrayMatch(Class type) { ANY_MAPPED.put(type, this); }
    }

    protected enum AllArrayMatch implements Matcher {
        /**
         * 数组
         */
        OBJECTS(null) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                Object[] arr = (Object[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Object val = arr[i];
                    if (!validator.originalValid(val == null ? null : val.toString())) {
                        return false;
                    }
                }
                return true;
            }
        },
        BYTES(byte[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                byte[] arr = (byte[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Byte val = Byte.valueOf(arr[i]);
                    if (!validator.originalValid(val)) {
                        return false;
                    }
                }
                return true;
            }
        },
        SHORTS(short[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                short[] arr = (short[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Short val = Short.valueOf(arr[i]);
                    if (!validator.originalValid(val)) {
                        return false;
                    }
                }
                return true;
            }
        },
        INTS(int[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                int[] arr = (int[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Integer val = Integer.valueOf(arr[i]);
                    if (!validator.originalValid(val)) {
                        return false;
                    }
                }
                return true;
            }
        },
        LONGS(long[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                long[] arr = (long[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Long val = Long.valueOf(arr[i]);
                    if (!validator.originalValid(val)) {
                        return false;
                    }
                }
                return true;
            }
        },
        FLOATS(float[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                float[] arr = (float[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Float val = Float.valueOf(arr[i]);
                    if (!validator.originalValid(val)) {
                        return false;
                    }
                }
                return true;
            }
        },
        DOUBLES(double[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                double[] arr = (double[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Double val = Double.valueOf(arr[i]);
                    if (!validator.originalValid(val)) {
                        return false;
                    }
                }
                return true;
            }
        },
        CHARS(char[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                char[] arr = (char[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Character val = Character.valueOf(arr[i]);
                    if (!validator.originalValid(val)) {
                        return false;
                    }
                }
                return true;
            }
        },
        BOOLEANS(boolean[].class) {
            @Override
            public boolean test(Object value, CollectValidator validator) {
                boolean[] arr = (boolean[]) value;
                int length = arr.length;
                for (int i = 0; i < length; i++) {
                    Boolean val = Boolean.valueOf(arr[i]);
                    if (!validator.originalValid(val)) {
                        return false;
                    }
                }
                return true;
            }
        };

        AllArrayMatch(Class type) { ALL_MAPPED.put(type, this); }
    }

    protected enum MatchType {
        /**
         * 全匹配
         */
        ALL(ALL_MAPPED, AllArrayMatch.OBJECTS),
        /**
         * 至少一个匹配
         */
        ANY(ANY_MAPPED, AnyArrayMatch.OBJECTS);

        private final Map<Class, Matcher> mapped;
        private final Matcher defaultMatcher;

        MatchType(Map<Class, Matcher> mapped, Matcher defaultMatcher) {
            this.defaultMatcher = defaultMatcher;
            this.mapped = mapped;
        }

        public Matcher get(Class type) {
            return mapped.getOrDefault(type, defaultMatcher);
        }
    }

    /**
     * 获取验证器，根据匹配类型、数组类型
     *
     * @param value
     *
     * @return
     */
    protected Matcher getArrayMather(Object value) { return matchType.get(value.getClass()); }

    /**
     * 数组验证，首先快速定位数组类型，然后验证
     *
     * @param value 数组字段值
     *
     * @return 是否验证通过
     */
    protected boolean doValidateArray(Object value) { return getArrayMather(value).test(value, this); }

    /**
     * 集合验证
     *
     * @param value 集合字段值
     *
     * @return 是否验证通过
     */
    protected boolean doValidateCollect(Object value) {
        Iterable items = (Iterable) value;
        if (this.requireAllMatch) {
            for (Object item : items) {
                if (!originalValid(item)) {
                    return false;
                }
            }
            return true;
        } else {
            for (Object item : items) {
                if (originalValid(item)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 执行验证
     *
     * @param value 字段值
     *
     * @return 是否符合验证
     */
    @Override
    protected boolean doValidateValue(Object value) {
        if (value == null) {
            // 不验证字段本身是否是 null，nullable 只针对集合里面数据项的值
            return true;
        } else if (value instanceof Iterable) {
            return doValidateCollect(value);
        } else if (value.getClass().isArray()) {
            return doValidateArray(value);
        } else {
            // TODO 非数组或集合字段直接忽略，这里可以加一条警告日志，表示不可用于非数组或集合字段上
            return true;
        }
    }
}
