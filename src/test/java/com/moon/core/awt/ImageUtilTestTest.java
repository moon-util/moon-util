package com.moon.core.awt;

import com.moon.core.lang.StringUtil;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ImageUtilTestTest {

    String dirPath;
    String name;
    String path;
    String base64;

    @Test
    void testToBase64() {
        path = "E:/Pictures/000/cover-{}.jpg";
        String[] paths = {};
        for (int i = 0; i < paths.length; i++) {
            String out = StringUtil.format(path, StringUtil.padStart(i + 1, 3, '0'));
            ImageUtil.joiner().verticalJoin(out, paths[i]);
        }
    }
}