package com.moon.core.script;

import com.moon.core.io.IOUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ScriptUtil {

    private ScriptUtil() { noInstanceError(); }

    public final static Object runJSCode(String code) {
        try {
            return newJSEngine().eval(code);
        } catch (ScriptException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public final static Object runJSFile(File js) {
        try {
            return newJSEngine().eval(IOUtil.getBufferedReader(js));
        } catch (ScriptException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public final static ScriptEngine newJSEngine() { return new ScriptEngineManager().getEngineByName("JavaScript"); }
}
