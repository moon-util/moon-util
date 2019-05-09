package com.moon.core.io;

import com.moon.core.util.Console;
import com.moon.core.util.IteratorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author benshaoye
 */
class FileUtilTestTest {

    String path;
    File file, dir;
    Object data;

    void outFreeSpace(String local) {
        try {
            file = new File(local);
            Console.out.println(file.getFreeSpace() + "b");
            Console.out.println((file.getFreeSpace() >> 10) + "kb");
            Console.out.println((file.getFreeSpace() >> 10 >> 10) + "mb");
            Console.out.println((file.getFreeSpace() >> 10 >> 10 >> 10) + "gb");
            Assertions.assertTrue(FileUtil.exists(local));
            Console.out.println(FileUtil.length(local));
        } catch (Throwable t) {

        }
    }

    @Test
    void testExists() {
        outFreeSpace("a:");
        outFreeSpace("a:DriverA");
    }

    @Test
    void testCopyToDirectory() {
    }

    @Test
    void testCopyToFile() {
    }

    @Test
    void testCopyToFile1() {
    }

    @Test
    void testGetOutputStream() {
    }

    @Test
    void testGetOutputStream1() {
    }

    @Test
    void testGetInputStream() {
    }

    @Test
    void testMkdirs() {
    }

    @Test
    void testCreateNewFile() {
    }

    @Test
    void testTraveller() {
    }

    @Test
    void testTraverseDirectory() {
    }

    @Test
    void testTraverseAll() {
    }

    @Test
    void testWriteLinesToBufferedWriter() {
    }

    @Test
    void testWriteLinesToFile() {
        String[] lines = {
            "0000",
            "1111",
            "2222",
            "3333",
            "4444",
        };

        FileUtil.appendLinesToFile("D:/write-lines.txt", lines);
        FileUtil.appendLinesToFile("D:/write-lines.txt", Arrays.asList(lines));
        FileUtil.appendLinesToFile("D:/write-lines.txt", IteratorUtil.of(lines));

        System.out.println();
    }

    @Test
    void testLengthToBit() {
        String dirPath = "E:\\Downloads\\BaiduNetdiskDownload\\套图合集\\";
        String newDirPath = "E:\\Downloads\\BaiduNetdiskDownload\\套图合集\\[YouMi]尤蜜荟\\001-050\\001-010";

        String traverse = dirPath;
        List<File> files = FileUtil.traveller().traverse(traverse);

        String targetDir = "A:\\DriverA\\Downloaded\\scattered\\";
        files.forEach(file -> {
            String absolutePath = file.getAbsolutePath();
            if (absolutePath.endsWith(".rar")) {
                String replaced = targetDir + absolutePath.replace(traverse, "");
                System.out.println(replaced);

                File replacedFile = new File(replaced);
                FileUtil.copyToFile(file, replacedFile);
            }
        });
    }

    @Test
    void testLength() {
    }

    @Test
    void testLengthToB() {
    }

    @Test
    void testLengthToKB() {
    }

    @Test
    void testLengthToMB() {
    }

    @Test
    void testLengthToGB() {
    }

    @Test
    void testLengthToTB() {
    }

    @Test
    void testLengthToPB() {
    }

    @Test
    void testLengthToEB() {
    }

    @Test
    void testLengthToZB() {
    }

    @Test
    void testDeleteAllFiles() {
    }

    @Test
    void testDelete() {
    }

    @Test
    void testFormatPath() {
    }
}