package com.moon.core.awt;

import com.moon.core.io.FileUtil;
import com.moon.core.lang.LangUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
final class OpUtil {

    private OpUtil() {
        noInstanceError();
    }

    /*
     * ----------------------------------------------------------------------------
     * defaults
     * ----------------------------------------------------------------------------
     */

    final static BufferedImage createBuffered(int width, int height, int type) {
        return new BufferedImage(width, height, type);
    }

    final static BufferedImage createBuffered(int width, int height) {
        return createBuffered(width, height, BufferedImage.TYPE_INT_RGB);
    }

    final static BufferedImage empty() {
        return createBuffered(1, 1);
    }

    /*
     * ----------------------------------------------------------------------------
     * I/O
     * ----------------------------------------------------------------------------
     */

    final static BufferedImage read(String imagePath) {
        return LangUtil.apply(imagePath, path -> ImageIO.read(new File(path)));
    }

    final static boolean write(BufferedImage out, String type, String target) {
        try {
            // 写图片
            return ImageIO.write(out, type, new File(target));
        } catch (IOException e) {
            e.printStackTrace();
            FileUtil.deleteAllFiles(target);
            return false;
        }
    }

}
