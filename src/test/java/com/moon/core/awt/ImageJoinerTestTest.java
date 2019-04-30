package com.moon.core.awt;

import com.moon.core.io.FileUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.IteratorUtil;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author benshaoye
 */
class ImageJoinerTestTest {

    @Test
    void testExtensionName() {
    }

    @Test
    void testGetSuffix() {
    }

    @Test
    void testVerticalJoin() {
        ThrowUtil.ignoreThrowsRun(() -> {
            String path = "D:/test";
            String[] values = IteratorUtil.map(FileUtil.traverseDirectory(path),
                File::getAbsolutePath).toArray(new String[0]);
            ImageJoiner.JPEG.horizontalJoin("D:/test/output.jpeg", values);
        }, true);
        new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    }

    @Test
    void testHorizontalJoin() {
    }

    @Test
    void testJoin() {
    }
}