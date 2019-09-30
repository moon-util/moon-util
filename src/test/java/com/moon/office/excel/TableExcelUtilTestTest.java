package com.moon.office.excel;

import com.moon.core.util.RandomStringUtil;
import com.moon.office.excel.core.TableExcel;
import com.moon.office.excel.core.TableCell;
import com.moon.office.excel.core.TableRow;
import com.moon.office.excel.core.TableSheet;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author benshaoye
 */
class TableExcelUtilTestTest {

    public static boolean isInteger(String string) {
        int len = string == null ? 0 : string.length();
        if (len > 0) {
            int i = 0;

            char ch = string.charAt(i++);
            if (ch > 57 || ch < 48) {
                if (ch != 45 || len == i) {
                    return false;
                }
            }

            while (i < len) {
                ch = string.charAt(i++);
                if (ch > 57 || ch < 48) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    @Test
    void testName() {
        for (int i = 0; i < 10; i++) {
            System.out.println(isInteger(RandomStringUtil.nextDigit(1)));
        }
    }

    @TableExcel(value = {
        @TableSheet(delimiters = {}, value = {
            @TableRow(var = "($item, $index, $key, $first, $last) in data.employees", value = {
                @TableCell(value = "{{$index}}", delimiters = {"{{", "}}"}),
                @TableCell(value = "姓名：{{$item.name}}", delimiters = {"{{", "}}"}),
                @TableCell(value = "年龄：{{$item.age}}", delimiters = {"{{", "}}"}),
                @TableCell(value = "性别：{{$item.sex}}", delimiters = {"{{", "}}"}),
                @TableCell(value = "地址：{{$item.address}}", delimiters = {"{{", "}}"}),
            }), @TableRow(var = "$member = data.footer", value = {
            @TableCell(var = "$item in $member", value = "$item"),
        }),
        }),
    })
    @Test
    void testRender() {

    }
}