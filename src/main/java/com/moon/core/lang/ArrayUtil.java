package com.moon.core.lang;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 * @date 2018/9/12
 */
public final class ArrayUtil {

    private ArrayUtil() { noInstanceError(); }

    private final static <T> T ifNonNull(T value, Consumer<T> consumer) {
        if (value != null) { consumer.accept(value); }
        return value;
    }

    public static String stringify(Object arr) {
        if (arr == null) { return null; }
        if (arr instanceof Object[]) {return Arrays.toString((Object[]) arr);}
        if (arr instanceof int[]) {return Arrays.toString((int[]) arr);}
        if (arr instanceof long[]) {return Arrays.toString((long[]) arr);}
        if (arr instanceof double[]) {return Arrays.toString((double[]) arr);}
        if (arr instanceof byte[]) {return Arrays.toString((byte[]) arr);}
        if (arr instanceof char[]) {return Arrays.toString((char[]) arr);}
        if (arr instanceof short[]) {return Arrays.toString((short[]) arr);}
        if (arr instanceof boolean[]) {return Arrays.toString((boolean[]) arr);}
        return arr.toString();
    }

    /*
     * ----------------------------------------------------------------
     * with
     * ----------------------------------------------------------------
     */

    public static <T> T[] with(T... values) { return values; }

    public static char[] with(char... values) { return values; }

    public static byte[] with(byte... values) { return values; }

    public static short[] with(short... values) { return values; }

    public static int[] with(int... values) { return values; }

    public static long[] with(long... values) { return values; }

    public static float[] with(float... values) { return values; }

    public static double[] with(double... values) { return values; }

    public static boolean[] with(boolean... values) { return values; }

    /*
     * ----------------------------------------------------------------
     * sort
     * ----------------------------------------------------------------
     */

    public static <T> T[] sort(T... values) { return ifNonNull(values, Arrays::sort); }

    public static char[] sort(char... values) { return ifNonNull(values, Arrays::sort); }

    public static byte[] sort(byte... values) { return ifNonNull(values, Arrays::sort); }

    public static short[] sort(short... values) { return ifNonNull(values, Arrays::sort); }

    public static int[] sort(int... values) { return ifNonNull(values, Arrays::sort); }

    public static long[] sort(long... values) { return ifNonNull(values, Arrays::sort); }

    public static float[] sort(float... values) { return ifNonNull(values, Arrays::sort); }

    public static double[] sort(double... values) { return ifNonNull(values, Arrays::sort); }

    /*
     * ----------------------------------------------------------------
     * sort
     * ----------------------------------------------------------------
     */

    public static <T> T[] sort(Comparator<? super T> comparator, T... values) {
        if (values != null) { Arrays.sort(values, comparator); }
        return values;
    }


    /*
     * ----------------------------------------------------------------
     * get array type
     * ----------------------------------------------------------------
     */

    public final static Class getArrayType(Class componentType) { return Array.newInstance(componentType, 0).getClass(); }

    /*
     * ----------------------------------------------------------------
     * length
     * ----------------------------------------------------------------
     */

    public static int length(Object[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(boolean[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(double[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(float[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(long[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(int[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(short[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(byte[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(char[] arr) { return arr == null ? 0 : arr.length; }

    /*
     * ----------------------------------------------------------------
     * fill
     * ----------------------------------------------------------------
     */

    public static <T> T[] fill(T[] arr, T value) { return fillFrom(arr, 0, value); }

    public static boolean[] fill(boolean[] arr, boolean value) { return fillFrom(arr, 0, value); }

    public static double[] fill(double[] arr, double value) { return fillFrom(arr, 0, value); }

    public static float[] fill(float[] arr, float value) { return fillFrom(arr, 0, value); }

    public static long[] fill(long[] arr, long value) { return fillFrom(arr, 0, value); }

    public static int[] fill(int[] arr, int value) { return fillFrom(arr, 0, value); }

    public static short[] fill(short[] arr, short value) { return fillFrom(arr, 0, value); }

    public static byte[] fill(byte[] arr, byte value) { return fillFrom(arr, 0, value); }

    public static char[] fill(char[] arr, char value) { return fillFrom(arr, 0, value); }

    /*
     * ----------------------------------------------------------------
     * fill from
     * ----------------------------------------------------------------
     */

    public static <T> T[] fillFrom(T[] arr, int fromIndex, T value) { return fill(arr, fromIndex, length(arr), value); }

    public static boolean[] fillFrom(boolean[] arr, int fromIndex, boolean value) { return fill(arr, fromIndex, length(arr), value); }

    public static double[] fillFrom(double[] arr, int fromIndex, double value) { return fill(arr, fromIndex, length(arr), value); }

    public static float[] fillFrom(float[] arr, int fromIndex, float value) { return fill(arr, fromIndex, length(arr), value); }

    public static long[] fillFrom(long[] arr, int fromIndex, long value) { return fill(arr, fromIndex, length(arr), value); }

    public static int[] fillFrom(int[] arr, int fromIndex, int value) { return fill(arr, fromIndex, length(arr), value); }

    public static short[] fillFrom(short[] arr, int fromIndex, short value) { return fill(arr, fromIndex, length(arr), value); }

    public static byte[] fillFrom(byte[] arr, int fromIndex, byte value) { return fill(arr, fromIndex, length(arr), value); }

    public static char[] fillFrom(char[] arr, int fromIndex, char value) { return fill(arr, fromIndex, length(arr), value); }

    /*
     * ----------------------------------------------------------------
     * fill to； 纯粹为了一个返回值
     * ----------------------------------------------------------------
     */

    public static <T> T[] fill(T[] arr, int fromIndex, int toIndex, T value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static boolean[] fill(boolean[] arr, int fromIndex, int toIndex, boolean value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static double[] fill(double[] arr, int fromIndex, int toIndex, double value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static float[] fill(float[] arr, int fromIndex, int toIndex, float value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static long[] fill(long[] arr, int fromIndex, int toIndex, long value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static int[] fill(int[] arr, int fromIndex, int toIndex, int value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static short[] fill(short[] arr, int fromIndex, int toIndex, short value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static byte[] fill(byte[] arr, int fromIndex, int toIndex, byte value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static char[] fill(char[] arr, int fromIndex, int toIndex, char value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    /*
     * ----------------------------------------------------------------
     * remove index, 删除指定位置数据，后面的数据前移一位； 返回原数组
     * ----------------------------------------------------------------
     */

    public static <T> T[] remove(T[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = null;
        return arr;
    }

    public static boolean[] remove(boolean[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = false;
        return arr;
    }

    public static double[] remove(double[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static float[] remove(float[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static long[] remove(long[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static int[] remove(int[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static short[] remove(short[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static byte[] remove(byte[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static char[] remove(char[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    private static int clearIdxAndGetLastIdx(Object arr, int length, int index) {
        int lastIndex = length - 1;
        System.arraycopy(arr, index + 1, arr, index, lastIndex - index);
        return lastIndex;
    }

    /*
     * ----------------------------------------------------------------
     * splice : 总是返回一个新数组
     * ----------------------------------------------------------------
     */

    public static <T> T[] splice(T[] arr, int fromIndex, int count, T... elements) {
        return (T[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count,
            l -> Array.newInstance(arr.getClass().getComponentType(), l));
    }

    public static boolean[] splice(boolean[] arr, int fromIndex, int count, boolean... elements) {
        return (boolean[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, boolean[]::new);
    }

    public static char[] splice(char[] arr, int fromIndex, int count, char... elements) {
        return (char[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, char[]::new);
    }

    public static byte[] splice(byte[] arr, int fromIndex, int count, byte... elements) {
        return (byte[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, byte[]::new);
    }

    public static short[] splice(short[] arr, int fromIndex, int count, short... elements) {
        return (short[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, short[]::new);
    }

    public static int[] splice(int[] arr, int fromIndex, int count, int... elements) {
        return (int[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, int[]::new);
    }

    public static long[] splice(long[] arr, int fromIndex, int count, long... elements) {
        return (long[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, long[]::new);
    }

    public static float[] splice(float[] arr, int fromIndex, int count, float... elements) {
        return (float[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, float[]::new);
    }

    public static double[] splice(double[] arr, int fromIndex, int count, double... elements) {
        return (double[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, double[]::new);
    }

    private static Object spliceArray(
        Object arr, int arrLen, Object elements, int elementsLen, int fromIndex, int count, IntFunction creator
    ) {
        if (fromIndex < 0) {
            throw new IllegalArgumentException("Invalid value of fromIndex: " + count);
        }
        if (count < 0) {
            throw new IllegalArgumentException("Invalid value of count: " + count);
        }
        Object value = creator.apply(arrLen + elementsLen - count);
        System.arraycopy(arr, 0, value, 0, fromIndex);
        System.arraycopy(elements, 0, value, fromIndex, elementsLen);
        if ((count = fromIndex + count) < arrLen) {
            System.arraycopy(arr, count, value, fromIndex + elementsLen, arrLen - count);
        }
        return value;
    }

    /*
     * ----------------------------------------------------------------
     * to primitives
     * ----------------------------------------------------------------
     */

    public static boolean[] toPrimitives(Boolean[] value) { return transformArray(value, boolean[]::new); }

    public static char[] toPrimitives(Character[] value) { return transformArray(value, char[]::new); }

    public static byte[] toPrimitives(Byte[] value) { return transformArray(value, byte[]::new); }

    public static short[] toPrimitives(Short[] value) { return transformArray(value, short[]::new); }

    public static int[] toPrimitives(Integer[] value) { return transformArray(value, int[]::new); }

    public static long[] toPrimitives(Long[] value) { return transformArray(value, long[]::new); }

    public static float[] toPrimitives(Float[] value) { return transformArray(value, float[]::new); }

    public static double[] toPrimitives(Double[] value) { return transformArray(value, double[]::new); }

    /*
     * ----------------------------------------------------------------
     * to objects
     * ----------------------------------------------------------------
     */

    public static Boolean[] toObjects(boolean[] value) { return transformArray(value, Boolean[]::new); }

    public static Character[] toObjects(char[] value) { return transformArray(value, Character[]::new); }

    public static Byte[] toObjects(byte[] value) { return transformArray(value, Byte[]::new); }

    public static Short[] toObjects(short[] value) { return transformArray(value, Short[]::new); }

    public static Integer[] toObjects(int[] value) { return transformArray(value, Integer[]::new); }

    public static Long[] toObjects(long[] value) { return transformArray(value, Long[]::new); }

    public static Float[] toObjects(float[] value) { return transformArray(value, Float[]::new); }

    public static Double[] toObjects(double[] value) { return transformArray(value, Double[]::new); }

    private static <T> T transformArray(Object value, IntFunction<T> creator) {
        if (value == null) { return null; }
        final int length = Array.getLength(value);
        T arr = creator.apply(length);
        System.arraycopy(value, 0, arr, 0, length);
        return arr;
    }
}
