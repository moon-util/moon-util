package com.moon.core.util.zip;

import com.moon.core.io.FileUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.RandomStringUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author benshaoye
 */
class ZipWriterTestTest {

    @Test
    void testAddEntry() {
        String path = "D:/zip-{}.zip";
        String name = StringUtil.format(path, RandomStringUtil.nextLetter(5));
        ZipWriter builder = new ZipWriter(FileUtil.getOutputStream(name));
        builder.addFileEntry(new File("E:/Documents/WeChat Files/xua744531854/FileStorage/File/2019-05/aaaaa"));
        builder.closeWriter();
        FileUtil.deleteAllFiles(path);
    }

    @Test
    void testAddFileEntry() {
    }

    @Test
    void testAddDirectoryEntries() {
    }
}