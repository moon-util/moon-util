package com.moon.core.util.json;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.util.interfaces.Stringify;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author benshaoye
 */
class JSONStringer implements Stringify {

    JSONStringer() {
    }

    private void stringify(StringBuilder builder, Collection collect) {
        builder.append('[');
        for (Object item : collect) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringifyOfJavaBean(StringBuilder stringer, Object obj) {
        BeanInfoUtil.getFieldDescriptorsMap(obj.getClass()).forEach((name, desc) -> {
            stringer.append('"').append(name).append('"').append(':');
            stringify(stringer, desc.getValue(obj)).append(',');
        });
    }

    private void stringify(StringBuilder builder, CharSequence value) {
        builder.append('"').append(value).append('"');
    }

    private void stringify(StringBuilder builder, Calendar calendar) {
        stringify(builder, calendar.getTime());
    }

    private void stringify(StringBuilder builder, Number value) {
        builder.append(value);
    }

    private void stringify(StringBuilder builder, Boolean value) {
        builder.append(value);
    }

    private void stringify(StringBuilder builder, Character value) {
        builder.append(value);
    }

    private void stringify(StringBuilder builder, Date date) {
        builder.append('"').append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)).append('"');
    }

    private void stringify(StringBuilder builder, Map map) {
        builder.append('{');
        map.forEach((key, value) -> {
            builder.append('"').append(key).append('"').append(':');
            stringify(builder, value).append(',');
        });
        close(builder, '}');
    }

    private StringBuilder stringify(StringBuilder builder, Object obj) {
        if (obj == null) {
            return builder.append("null");
        } else {
            if (obj instanceof Map) {
                stringify(builder, (Map) obj);
            } else if (obj instanceof Collection) {
                stringify(builder, (Collection) obj);
            } else if (obj instanceof Object[]) {
                stringify(builder, (Object[]) obj);
            } else if (obj instanceof boolean[]) {
                stringify(builder, (boolean[]) obj);
            } else if (obj instanceof char[]) {
                stringify(builder, (char[]) obj);
            } else if (obj instanceof byte[]) {
                stringify(builder, (byte[]) obj);
            } else if (obj instanceof short[]) {
                stringify(builder, (short[]) obj);
            } else if (obj instanceof int[]) {
                stringify(builder, (int[]) obj);
            } else if (obj instanceof long[]) {
                stringify(builder, (long[]) obj);
            } else if (obj instanceof float[]) {
                stringify(builder, (float[]) obj);
            } else if (obj instanceof double[]) {
                stringify(builder, (double[]) obj);
            } else if (obj instanceof Date) {
                stringify(builder, (Date) obj);
            } else if (obj instanceof Calendar) {
                stringify(builder, (Calendar) obj);
            } else if (obj instanceof Number) {
                stringify(builder, (Number) obj);
            } else if (obj instanceof Character) {
                stringify(builder, (Character) obj);
            } else if (obj instanceof Boolean) {
                stringify(builder, (Boolean) obj);
            } else if (obj instanceof CharSequence) {
                stringify(builder, (CharSequence) obj);
            } else {
                stringifyOfJavaBean(builder, obj);
            }
        }
        return builder;
    }

    @Override
    public String stringify(Object obj) {
        return stringify(new StringBuilder(), obj).toString();
    }

    private void stringify(StringBuilder builder, Object[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringify(StringBuilder builder, boolean[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringify(StringBuilder builder, char[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringify(StringBuilder builder, byte[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringify(StringBuilder builder, int[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringify(StringBuilder builder, short[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringify(StringBuilder builder, long[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringify(StringBuilder builder, float[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void stringify(StringBuilder builder, double[] arr) {
        builder.append('[');
        for (Object item : arr) {
            stringify(builder, item).append(',');
        }
        close(builder, ']');
    }

    private void close(StringBuilder builder, char value) {
        builder.setCharAt(builder.length() - 1, value);
    }
}
