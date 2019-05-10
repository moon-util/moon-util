package com.moon.core.util.runner;

import com.moon.core.lang.BooleanUtil;
import com.moon.core.lang.SupportUtil;
import com.moon.core.util.IteratorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public final class RunnerSettings {

    protected Supplier<List> arrCreator;
    protected Supplier<Map> objCreator;

    private final Map<String, Object> callers = new HashMap<>();

    public RunnerSettings() { this(ArrayList::new, HashMap::new); }

    public RunnerSettings(Supplier<List> arrCreator, Supplier<Map> objCreator) {
        this.arrCreator = arrCreator;
        this.objCreator = objCreator;
    }

    /**
     * 获取静态方法执行类
     *
     * @param name
     * @return
     */
    public final Object getCaller(String name) { return this.callers.get(name); }

    public final Supplier<List> getArrCreator() { return arrCreator; }

    public final Supplier<Map> getObjCreator() { return objCreator; }

    public RunnerSettings setArrCreator(Supplier<List> arrCreator) {
        this.arrCreator = arrCreator;
        return this;
    }

    public RunnerSettings setObjCreator(Supplier<Map> objCreator) {
        this.objCreator = objCreator;
        return this;
    }

    public RunnerSettings addFunction(RunnerFunction runner) {
        this.callers.put(checkName(runner.functionName()), runner);
        return this;
    }

    public RunnerSettings addFunctions(List<RunnerFunction> runners) {
        IteratorUtil.forEach(runners, runner -> addFunction(runner));
        return this;
    }

    public RunnerSettings addFunction(String namespace, RunnerFunction fun) {
        this.callers.put(toNsName(namespace, fun.functionName()), fun);
        return this;
    }

    public RunnerSettings addFunctions(String namespace, List<RunnerFunction> runners) {
        IteratorUtil.forEach(runners, fun -> addFunction(namespace, fun));
        return this;
    }

    public RunnerSettings addCaller(Class clazz) { return addCaller(clazz.getSimpleName(), clazz); }

    public RunnerSettings addCallers(Class... classes) {
        for (Class type : classes) {
            addCaller(type);
        }
        return this;
    }

    public RunnerSettings addCaller(String name, Class staticCallerClass) {
        this.callers.put(name, staticCallerClass);
        return this;
    }

    public RunnerSettings addCallers(Map<String, Class> callers) {
        this.callers.putAll(callers);
        return this;
    }

    public RunnerSettings removeCaller(String name) {
        this.callers.remove(name);
        return this;
    }

    public RunnerSettings removeCallers(String... names) {
        for (String name : names) {
            this.callers.remove(name);
        }
        return this;
    }

    public final static RunnerSettings of() { return new RunnerSettings(); }

    static String toNsName(String ns, String name) { return checkName(ns) + '.' + checkName(name); }

    static String checkName(String name) {
        char curr = name.charAt(0);
        BooleanUtil.requireTrue(SupportUtil.isVar(curr), name);
        for (int i = 1, len = name.length(); i < len; i++) {
            BooleanUtil.requireTrue(
                SupportUtil.isVar(curr = name.charAt(i))
                    || SupportUtil.isNum(curr), name);
        }
        return name;
    }
}
