package com.moon.more.excel;

import org.apache.poi.ss.usermodel.*;
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
            sheetFactory.row(rowFactory -> {});
            sheetFactory.row(rowFactory -> {
                rowFactory.style("header");

                rowFactory.cell("");
                rowFactory.cell("");
                rowFactory.cell("学历情况", 1, 6);
                rowFactory.cell("年龄结构", 1, 4);
                rowFactory.cell("政治面貌", 1, 3);
                rowFactory.cell("性别结构", 1, 2);
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.style("header");

                rowFactory.cell("岗位名称");
                rowFactory.cell("申请人数");
                rowFactory.cell("硕士研究生");
                rowFactory.cell("普通高中");
                rowFactory.cell("博士研究生");
                rowFactory.cell("初中级以下");
                rowFactory.cell("大学本科");
                rowFactory.cell("大专");
                rowFactory.cell("20岁以下");
                rowFactory.cell("31-40（岁）");
                rowFactory.cell("21-30（岁）");
                rowFactory.cell("40岁以上");
                rowFactory.cell("党员");
                rowFactory.cell("群众");
                rowFactory.cell("团员");
                rowFactory.cell("男性");
                rowFactory.cell("女性");
            }).definitionStyle("title", style -> {
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.cell(cellFactory -> cellFactory.val("女性"));
                rowFactory.cell().val(0).style("header");
            });
        }).finish().write2Filepath("D:/test.xlsx");
    }
}