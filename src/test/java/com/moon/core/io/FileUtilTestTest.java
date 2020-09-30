package com.moon.core.io;

import com.moon.core.util.IteratorUtil;
import com.moon.core.util.MapUtil;
import com.moon.runner.RunnerUtil;
import com.moon.poi.excel.annotation.style.HeadStyle;
// import com.moon.poi.excel.annotation.style.ClassnameForHead;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
class FileUtilTestTest {

    // @ClassnameForHead
    // @ClassnameForHead
    // @ClassnameForHead
    @HeadStyle
    String path;
    File file, dir;
    Object data;

    void outFreeSpace(String local) {
        try {
            file = new File(local);
            System.out.println(file.getFreeSpace() + "b");
            System.out.println((file.getFreeSpace() >> 10) + "kb");
            System.out.println((file.getFreeSpace() >> 10 >> 10) + "mb");
            System.out.println((file.getFreeSpace() >> 10 >> 10 >> 10) + "gb");
            Assertions.assertTrue(FileUtil.exists(local));
            System.out.println(FileUtil.length(local));
        } catch (Throwable t) {

        }
    }

    @Test
    void testExists() {
        outFreeSpace("a:");
        outFreeSpace("a:DriverA");

        Object ret = RunnerUtil.run("{'1':5,'6':0,'2':0,'8':0,'3':0,}");
        assertEquals(MapUtil.sizeByObject(ret), 5);
        ret = RunnerUtil.run("['1']+'1'", ret);
        assertEquals(ret, "51");
    }

    @Test
    void testCopyToDirectory() {
        try {

        } finally {

        }
        try {

        } catch (Exception e){

        }
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
            "0000", "1111", "2222", "3333", "4444",
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

    @Test
    void testFormatFilename() throws Exception {
        String filename = "filename.xlsx";
        assertEquals("filename.xlsx", FileUtil.formatFilename(filename, "XLSX"));
        assertEquals("filename.xls.XLSX", FileUtil.formatFilename("filename.xls", "XLSX"));
    }

    @Test
    void testFormatFilepath() throws Exception {
    }
}