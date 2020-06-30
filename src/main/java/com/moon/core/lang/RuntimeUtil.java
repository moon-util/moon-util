package com.moon.core.lang;

import com.moon.core.io.IOUtil;

import java.io.IOException;
import java.io.InputStream;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class RuntimeUtil {

    private RuntimeUtil() { noInstanceError(); }

    static {
        try {
            Process process = Runtime.getRuntime().exec("ipconfig -all");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
