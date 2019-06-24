package com.moon.core.io;

import com.moon.core.lang.LangUtil;

import java.io.BufferedWriter;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
final class WriteUtil {

    private WriteUtil() {
        noInstanceError();
    }

    public static void writeWithNewLine(BufferedWriter writer, CharSequence cs) {
        LangUtil.run(() -> {
            writer.newLine();
            writer.write(String.valueOf(cs));
        });
    }
}
