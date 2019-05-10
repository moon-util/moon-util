package com.moon.core.util.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.runner.RunnerFunction;
import com.moon.core.util.runner.RunnerSettings;

import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.runner.core.Constants.DOT;
import static com.moon.core.util.runner.core.Constants.YUAN_L;
import static com.moon.core.util.runner.core.ParseGetter.parseVar;
import static com.moon.core.util.runner.core.ParseUtil.*;

/**
 * @author benshaoye
 */
class ParseCall {
    private ParseCall() { noInstanceError(); }

    /**
     * 静态方法调用或者函数调用
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @return
     */
    final static AsRunner parse(
        char[] chars, IntAccessor indexer, int len, RunnerSettings settings
    ) {
        AsRunner runner;
        Object runnerCache;
        int curr = nextVal(chars, indexer, len);
        if (ParseUtil.isVar(curr)) {
            String runnerName = parseVar(chars, indexer, len, curr).toString();
            runnerCache = tryLoaderOrSimpleFn(chars,
                indexer, len, settings, runnerName);
            if (runnerCache == null) {
                runnerCache = ensureLoadNsFn(chars,
                    indexer, len, settings, runnerName);
            }
            if (runnerCache instanceof RunnerFunction) {
                RunnerFunction fn = (RunnerFunction) runnerCache;
                fn = tryParseNsCaller(chars, indexer, len, settings, fn);
                runner = parseFunCaller(chars, indexer, len, settings, fn);
            } else {
                runner = (AsRunner) runnerCache;
            }
        } else {
            runner = throwErr(chars, indexer);
            // 为更多符号留位置，比如动态变化的类，等
        }
        return Objects.requireNonNull(runner);
    }

    private final static AsRunner parseFunCaller(
        char[] chars, IntAccessor indexer, int len,
        RunnerSettings settings, RunnerFunction fn
    ) {
        int curr = nextVal(chars, indexer, len);
        assertTrue(curr == YUAN_L, chars, indexer);
        AsRunner[] runs = ParseParams.parse(chars, indexer, len, settings);
        switch (runs.length) {
            case 0:
                return valueOf(fn);
            case 1:
                return valueOf(fn, runs[0]);
            case 2:
                return valueOf(fn, runs[0], runs[1]);
            case 3:
                return valueOf(fn, runs[0], runs[1], runs[2]);
            default:
                return new FunctionMulti(fn, runs);
        }
    }

    /**
     * 尝试解析具有命名空间的函数
     * <p>
     * 最开始解析出来的是函数，这一步仍然可能是函数
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @param fn
     * @return
     */
    private final static RunnerFunction tryParseNsCaller(
        char[] chars, IntAccessor indexer, int len, RunnerSettings settings, RunnerFunction fn
    ) {
        int curr = nextVal(chars, indexer, len);
        if (curr == DOT) {
            curr = nextVal(chars, indexer, len);
            assertTrue(ParseUtil.isVar(curr), chars, indexer);
            String name = ParseGetter.parseVar(chars, indexer, len, curr).toString();
            final String funcName = IGetFun.toName(fn.functionName(), name);
            Object caller = null;
            if (settings != null) {
                caller = settings.getCaller(funcName);
            }
            if (caller == null) {
                caller = IGetFun.tryLoad(funcName);
            }
            Objects.requireNonNull(caller);
            assertTrue(caller instanceof RunnerFunction, chars, indexer);
            return (RunnerFunction) caller;
        }
        indexer.decrement();
        return fn;
    }

    private static class FunctionMulti extends FunctionNone {
        private final AsRunner[] runners;

        FunctionMulti(RunnerFunction fn, AsRunner[] runners) {
            super(fn);
            this.runners = runners;
        }

        @Override
        public Object run(Object data) {
            AsRunner[] runners = this.runners;
            int length = runners.length;
            Object[] params = new Object[length];
            for (int i = 0; i < length; i++) {
                params[i] = runners[i].run(data);
            }
            return fn.apply(params);
        }
    }

    final static AsRunner valueOf(
        RunnerFunction fn, AsRunner runner, AsRunner runner0, AsRunner runner1
    ) {
        if (runner.isConst() && runner0.isConst() && fn.isChangeless()) {
            return DataConst.get(fn.apply(runner.run(), runner0.run(), runner1.run()));
        }
        return new FunctionThree(fn, runner, runner0, runner1);
    }

    private static class FunctionThree extends FunctionTwo {
        protected final AsRunner runner1;

        FunctionThree(RunnerFunction fn, AsRunner runner, AsRunner runner0, AsRunner runner1) {
            super(fn, runner, runner0);
            this.runner1 = runner1;
        }

        @Override
        public Object run(Object data) { return fn.apply(runner.run(data), runner0.run(), runner1.run()); }
    }

    final static AsRunner valueOf(RunnerFunction fn, AsRunner runner, AsRunner runner0) {
        if (runner.isConst() && runner0.isConst() && fn.isChangeless()) {
            return DataConst.get(fn.apply(runner.run(), runner0.run()));
        }
        return new FunctionTwo(fn, runner, runner0);
    }

    private static class FunctionTwo extends FunctionOne {
        protected final AsRunner runner0;

        FunctionTwo(RunnerFunction fn, AsRunner runner, AsRunner runner0) {
            super(fn, runner);
            this.runner0 = runner0;
        }

        @Override
        public Object run(Object data) { return fn.apply(runner.run(data), runner0.run()); }
    }

    final static AsRunner valueOf(RunnerFunction fn, AsRunner runner) {
        if (runner.isConst() && fn.isChangeless()) {
            Object value = runner.run();
            return DataConst.get(fn.apply(value));
        }
        return new FunctionOne(fn, runner);
    }

    private static class FunctionOne extends FunctionNone {
        protected final AsRunner runner;

        FunctionOne(RunnerFunction fn, AsRunner runner) {
            super(fn);
            this.runner = runner;
        }

        @Override
        public Object run(Object data) { return fn.apply(runner.run(data)); }
    }

    final static AsRunner valueOf(RunnerFunction fn) {
        return fn.isChangeless() ? DataConst.get(fn.apply()) : new FunctionNone(fn);
    }

    private static class FunctionNone implements AsInvoker {
        protected final RunnerFunction fn;

        FunctionNone(RunnerFunction fn) { this.fn = fn; }

        @Override
        public Object run(Object data) { return fn.apply(); }
    }

    /**
     * 在{@link #tryLoaderOrSimpleFn(char[], IntAccessor, int, RunnerSettings, String)}
     * 加载失败后，这里解析具有命名空间的函数
     * 同样，settingWith 优先于内置函数
     * <p>
     * 这一步解析不出来一定会抛出异常
     * <p>
     * 在尝试加载简单函数或静态方法失败后
     * 到这儿只能加载含有命名空间的函数
     * 否则抛出异常
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @param runnerName
     * @return
     */
    private final static Object ensureLoadNsFn(
        char[] chars, IntAccessor indexer, int len,
        RunnerSettings settings, String runnerName
    ) {
        Object callerTemp = null;
        int curr = nextVal(chars, indexer, len);
        if (curr == Constants.DOT) {
            curr = nextVal(chars, indexer, len);
            String name = parseVar(chars, indexer, len, curr).toString();
            name = IGetFun.toName(runnerName, name);
            if (settings != null) {
                callerTemp = settings.getCaller(name);
            }
            if (callerTemp == null) {
                callerTemp = IGetFun.tryLoad(name);
            }
            assertTrue(callerTemp instanceof RunnerFunction, chars, indexer);
            return callerTemp;
        }
        return throwErr(chars, indexer);
    }

    /**
     * 尝试解析函数或静态方法类
     * <p>
     * settingWith 优先于默认，函数优先于静态方法
     * 优先从 settingWith 加载函数或静态方法
     * 不能加载情况下尝试加载内置函数或方法
     * 这一步如果是加载函数的话，只加载简单函数，即不含有命名空间的函数
     * <p>
     * 均加载失败返回 null，失败的意思是一定是一个具有命名空间的函数，否则就是异常
     * <p>
     * 加载异常将抛出
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @param runnerName
     * @return
     */
    private final static Object tryLoaderOrSimpleFn(
        char[] chars, IntAccessor indexer, int len,
        RunnerSettings settings, String runnerName
    ) {
        if (settings == null) {
            return tryLoadDefault(runnerName);
        }
        Object caller = settings.getCaller(runnerName);
        if (caller == null) {
            return tryLoadDefault(runnerName);
        } else if (caller instanceof RunnerFunction) {
            return caller;
        } else if (caller instanceof Class) {
            return new DataLoader((Class) caller);
        }
        return throwErr(chars, indexer);
    }

    /**
     * 加载内置函数或静态方法类
     *
     * @param runnerName
     * @return
     */
    private static Object tryLoadDefault(String runnerName) {
        Object caller = IGetFun.tryLoad(runnerName);
        if (caller != null) {
            return caller;
        }
        caller = IGetLoad.tryLoad(runnerName);
        if (caller != null) {
            return new DataLoader((Class) caller);
        }
        return null;
    }
}
