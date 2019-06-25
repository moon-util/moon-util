package com.moon.office.excel;

import com.moon.core.io.FileUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomUtil;
import com.moon.office.excel.creator.ExcelWriter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author benshaoye
 */
class ExcelWriterTestTest {

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

        ExcelWriter.ofXlsx().createSheet(sheetWriter -> {
            sheetWriter.createRow(0, rowWriter -> {

            });
            IteratorUtil.forEach(users, (user, index) -> {
                sheetWriter.createRow(index, rowWriter -> {

                });
            });
        });
    }

    @Test
    void testStringLength() {
        String regex = "^\\s*(\\w[\\w\\d]{0,255})\\s*(\\((.*)?\\))?.*";
        String str = "  selectAll(   sdafsdfasdfasdfc )asdfegsdfgvbdfgvbsdfgv";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);


        System.out.println(matcher.matches());

        MatchResult result = matcher.toMatchResult();
        int count = result.groupCount();
        for (int i = 0; i < count; i++) {
            System.out.println(">>>" + result.group(i) + "<<<");
        }
        System.out.println(matcher.toMatchResult().groupCount());
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