package com.moon.office.excel;

import com.moon.core.io.FileUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomUtil;
import com.moon.office.excel.creator.ExcelCreator;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author benshaoye
 */
class ExcelCreatorTestTest {

    public static class User {

        String name = "张三";
        int age = RandomUtil.nextInt(18, 22);
        String address = "北京市西城区";
        String phone = "13755556666";
        String gender = RandomUtil.nextBoolean() ? "男" : "女";
    }

    @Test
    void testOf() {
        String path = "D:/now-test-excel.xlsx";
        FileUtil.deleteAllFiles(path);
        List<User> users = ListUtil.ofArrayList(new User(), new User(), new User(), new User(), new User());
        ExcelCreator.ofXlsx().createSheet(sheet -> {
            sheet.createRow(row -> row.cell().set("").merge(2, 5));
            sheet.createRow(row -> {
                row.createCell("序号");
                row.createCell("姓名");
                row.createCell("年龄");
                row.createCell("性别");
                row.createCell("地址");
                row.createCell("电话");
            });
            IteratorUtil.forEach(users, user -> sheet.createRow(row -> {
                row.createCell(row.getCurrentRowIndex());
                row.createCell(user.name);
                row.createCell(user.age);
                row.createCell(user.gender);
                row.createCell(user.address);
                row.createCell(user.phone);
            }));
        }).write(FileUtil.getOutputStream(path));
    }

    @Test
    void testStringLength() {
    }


    @Test
    void testUseSheet() {
        int i = 0;
        for (int j = 0; j < 50; j++) {
            int value = i;
            i = i + 1;
            i = value;
        }
        System.out.println(i);
    }

    @Test
    void testCreate() {
        String str = "1";
        int hash = str.hashCode();
        System.out.println(hash);
        int endVal = hash % 31;
        System.out.println(endVal);
        char endCh = (char) endVal;
        System.out.println(endCh);
    }
}