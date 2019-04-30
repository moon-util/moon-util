package com.moon.office.excel;

import com.moon.core.io.FileUtil;
import com.moon.core.lang.ArrayUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.RandomStringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ColumnDescriptionTestTest {

    static class User {
        private String name;

        private String sex;

        public User(String name, String sex) {
            this.name = name;
            this.sex = sex;
        }
    }

    @Test
    void testSettings() {
        User[] users = ArrayUtil.with(
            new User("张三", "男"),
            new User("王五", "男"),
            new User("李四", "男")
        );
        IntAccessor accessor = IntAccessor.of();
        Workbook workbook = new HSSFWorkbook();
        ColumnDescription.settingWith(User.class)
            .add("序号", user -> accessor.incrementAndGet())
            .add("姓名", user -> user.name)
            .add("性别", user -> user.sex)
            .operate(workbook.createSheet())
            .setData(users, 0)
        ;
    }

    @Test
    void testOf() {
        User[] users = ArrayUtil.with(
            new User("张三", "男"),
            new User("王五", "男"),
            new User("李四", "男")
        );

        IntAccessor accessor = IntAccessor.of();
        ColumnDescription<User>[] descriptions = ArrayUtil.with(
            ColumnDescription.of("序号", user -> accessor.incrementAndGet()),
            ColumnDescription.of("姓名", user -> user.name),
            ColumnDescription.of("性别", user -> user.sex)
        );

        IntAccessor rowAccessor = IntAccessor.of();
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row row = sheet.createRow(rowAccessor.getAndIncrement());
        IteratorUtil.forEach(descriptions, (config, index) -> {
            row.createCell(index).setCellValue(config.getTitle());
            config.setColumnWidth(sheet, index);
        });

        IteratorUtil.forEach(users, (user, i) -> {
            Row innerRow = sheet.createRow(rowAccessor.getAndIncrement());
            IteratorUtil.forEach(descriptions, (config, index) ->
                config.setCellValue(innerRow.createCell(index), user));
        });

        String path = "d:/" + RandomStringUtil.nextLower() + ".xls";
        ThrowUtil.ignoreThrowsRun(() -> workbook.write(FileUtil.getOutputStream(path)));
        FileUtil.deleteAllFiles(path);
    }
}