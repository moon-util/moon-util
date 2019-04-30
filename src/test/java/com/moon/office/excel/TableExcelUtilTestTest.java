package com.moon.office.excel;

import com.moon.office.excel.core.TableExcel;
import com.moon.office.excel.core.TableCell;
import com.moon.office.excel.core.TableRow;
import com.moon.office.excel.core.TableSheet;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class TableExcelUtilTestTest {

    @TableExcel(value = {
        @TableSheet(delimiters = {}, value = {
            @TableRow(var = "($item, $index, $key, $first, $last) in data.employees", value = {
                @TableCell(value = "{{$index}}", delimiters = {"{{", "}}"}),
                @TableCell(value = "姓名：{{$item.name}}", delimiters = {"{{", "}}"}),
                @TableCell(value = "年龄：{{$item.age}}", delimiters = {"{{", "}}"}),
                @TableCell(value = "性别：{{$item.sex}}", delimiters = {"{{", "}}"}),
                @TableCell(value = "地址：{{$item.address}}", delimiters = {"{{", "}}"}),
            }),
            @TableRow(var = "$member = data.footer", value = {
                @TableCell(var = "$item in $member", value = "$item"),
            }),
        }),
    })
    @Test
    void testRender() {

    }
}