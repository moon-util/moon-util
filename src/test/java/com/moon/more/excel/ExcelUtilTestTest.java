package com.moon.more.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author benshaoye
 */
class ExcelUtilTestTest {

    @Test
    void testFilesCreateFile() throws IOException {
        Files.createFile(Paths.get("D:/tmp/bb.text"));
    }

    @Test
    void testCreateRow() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row1 = sheet.createRow(0);
        Row row2 = sheet.createRow(0);
        System.out.println(row1 == row2);
    }

    @Test
    void testXlsx() {
        ExcelFactory excelFactory = ExcelUtil.xlsx();
        excelFactory.sheet("招聘进度分析", sheetFactory -> {
            sheetFactory.definitionStyle("header", (style, font) -> {
                font.setColor(IndexedColors.RED.index);
                style.setFont(font);
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setFillBackgroundColor(IndexedColors.BLUE_GREY.index);
            });
            sheetFactory.definitionComment("default", comment -> {
                comment.setString(new XSSFRichTextString("你好啊，小伙子"));
            });
            sheetFactory.row(rowFactory -> {});
            sheetFactory.row(rowFactory -> {
                rowFactory.style("header");

                rowFactory.next("");
                rowFactory.next("");
                rowFactory.next("学历情况", 1, 6);
                rowFactory.next("年龄结构", 1, 4);
                rowFactory.next("政治面貌", 1, 3);
                rowFactory.next("性别结构", 1, 2);
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.style("header");

                rowFactory.next("岗位名称");
                rowFactory.next("申请人数");
                rowFactory.next("硕士研究生");
                rowFactory.next("普通高中");
                rowFactory.next("博士研究生");
                rowFactory.next("初中级以下");
                rowFactory.next("大学本科");
                rowFactory.next("大专");
                rowFactory.next("20岁以下");
                rowFactory.next("31-40（岁）");
                rowFactory.next("21-30（岁）");
                rowFactory.next("40岁以上");
                rowFactory.next("党员");
                rowFactory.next("群众");
                rowFactory.next("团员");
                // rowFactory.next("男性");
                rowFactory.cell().val("男性").commentAs("default");
                rowFactory.cell().val("女性").commentAs("default");
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.cell(cellFactory -> cellFactory.val("女性"));
                rowFactory.cell().val(0).style("header").comment("今天是个好日子");
            });
        }).finish().write2Filepath("D:/test.xlsx");
    }
}