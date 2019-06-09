package com.moon.core.util.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.reflect.FieldUtil;
import com.moon.core.util.runner.RunnerSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.reflect.FieldUtil.getAccessibleField;
import static com.moon.core.util.runner.core.Constants.YUAN_L;
import static com.moon.core.util.runner.core.Constants.YUAN_R;
import static com.moon.core.util.runner.core.ParseUtil.nextVal;
import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
final class ParseInvoker {

    private ParseInvoker() { noInstanceError(); }

    final static AsRunner tryParseInvoker(
        char[] chars, IntAccessor indexer, int len, RunnerSettings settings, String methodName, AsValuer prevValuer
    ) {
        final int cache = indexer.get();
        final boolean isStatic = prevValuer instanceof DataLoader;
        if (nextVal(chars, indexer, len) == YUAN_L) {
            if (nextVal(chars, indexer, len) == YUAN_R) {
                // 无参方法调用
                return InvokeArgs0.parse(prevValuer, methodName, isStatic);
            } else {
                // 带有参数的方法调用
                return parseHasParams(chars, indexer.decrement(), len, settings, prevValuer, methodName, isStatic);
            }
        } else {
            // 静态字段检测
            indexer.set(cache);
            return tryParseStaticField(prevValuer, methodName, isStatic);
        }
    }

    /**
     * 带有参数的方法调用
     */
    private final static AsRunner parseHasParams(
        char[] chars,
        IntAccessor indexer,
        int len,
        RunnerSettings settings,
        AsValuer prev,
        String name,
        boolean isStatic
    ) {
        AsRunner[] params = ParseParams.parse(chars, indexer, len, settings);
        if (params.length > 1) {
            return parseMultiParamCaller(params, prev, name, isStatic);
        } else {
            // return InvokeArgs1.parse(prev, name, isStatic, params[0]);
            return parseOnlyParamCaller(params,prev,name,isStatic);
        }
    }

    /**
     * 多参数调用的方法（未实现）
     *
     * @param params   参数
     * @param prev     静态方法的 class 或实例方法的{@link AsGetter}
     * @param name     方法名{@link Method#getName()}
     * @param isStatic 是否是静态方法
     *
     * @return
     */
    private final static AsRunner parseMultiParamCaller(
        AsRunner[] params, AsValuer prev, String name, boolean isStatic
    ) {
        if (isStatic) {

        } else {

        }
        throw new UnsupportedOperationException();
    }

    /**
     * 带有一个参数的方法
     */
    private final static AsRunner parseOnlyParamCaller(
        AsRunner[] valuers, AsValuer prev, String name, boolean isStatic
    ) {
        if (isStatic) {
            // 静态方法
            Class sourceType = ((DataLoader) prev).getValue();
            return InvokeOneEnsure.of(valuers[0], sourceType, name);
        } else {
            // 成员方法
            return new InvokeOne(prev, valuers[0], name);
        }
    }

    /**
     * 尝试解析静态字段，如果不是静态字段调用返回 null
     */
    private final static AsValuer tryParseStaticField(
        AsValuer prevValuer, String name, boolean isStatic
    ) {
        if (isStatic) {
            // 静态字段
            Class sourceType = ((DataLoader) prevValuer).getValue();
            Field field = requireNonNull(getAccessibleField(sourceType, name));
            return DataConst.get(FieldUtil.getValue(field, sourceType));
        }
        return null;
    }
}
