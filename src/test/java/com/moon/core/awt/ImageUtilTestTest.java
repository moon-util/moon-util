package com.moon.core.awt;

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
        dirPath = "E:/Pictures/";
        name = "微信图片_20180913184427.png";
        base64 = ImageUtil.imageToBase64(dirPath, name);
        System.out.println(base64);
        ImageUtil.base64ToImage(base64, dirPath, "newName.png");
    }
}