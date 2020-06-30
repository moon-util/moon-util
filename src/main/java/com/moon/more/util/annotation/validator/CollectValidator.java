package com.moon.more.util.annotation.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
public abstract class CollectValidator extends BaseValidator {

    private final static Map<Class, Matcher> ANY_MAPPED = new HashMap<>();

    private final static Map<Class, Matcher> ALL_MAPPED = new HashMap<>();

    protected final MatchType matchType;

    protected final boolean requireAllMatch;

    protected CollectValidator(Set<? extends Number> values, boolean requireAllMatch) {
        super(values);
        this.requireAllMatch = requireAllMatch;
        this.matchType = requireAllMatch ? MatchType.ALL : MatchType.ANY;
    }

    interface Matcher {

        boolean test(Object value, CollectValidator validator);
    }

    protected enum AnyMatch implements Matcher {
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

        AnyMatch(Class type) { ANY_MAPPED.put(type, this); }
    }

    protected enum AllMatch implements Matcher {
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

        AllMatch(Class type) { ALL_MAPPED.put(type, this); }
    }

    protected enum MatchType {
        /**
         * 全匹配
         */
        ALL(ALL_MAPPED, AllMatch.OBJECTS),
        /**
         * 至少一个匹配
         */
        ANY(ANY_MAPPED, AnyMatch.OBJECTS);

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

    protected Matcher getMather(Object value) { return matchType.get(value.getClass()); }

    protected boolean doValidateArray(Object value) { return getMather(value).test(value, this); }

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

    @Override
    protected boolean doValidateValue(Object value) {
        if (value == null) {
            return allowNull;
        } else if (value instanceof Iterable) {
            return doValidateCollect(value);
        } else {
            return doValidateArray(value);
        }
    }
}
