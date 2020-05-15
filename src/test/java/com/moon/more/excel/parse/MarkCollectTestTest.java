package com.moon.more.excel.parse;

import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.WorkbookFactory;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class MarkCollectTestTest extends ParseUtil {

    public static class SimpleBodyExport {

        private String name = "张三";

        private final int index;

        public SimpleBodyExport(int index) {
            this.index = index;
        }
    }

    WorkbookFactory render() {
        List<SimpleBodyExport> exports = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            exports.add(new SimpleBodyExport(i));
        }
        return ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(exports);
            });
        });
    }

    @Test
    void testExportSingle() throws Exception {

    }

    public static class DuplicatedColumn {

        @TableColumn
        @TableColumnFlatten
        public String name;
        public String value;
    }

    public static class DuplicatedColumn1 {

        @TableColumn
        public String name;
        public String value;

        @TableColumnFlatten
        public String getName() {
            return name;
        }
    }

    /**
     * 不能重复注解 TableColumnFlatten TableColumn
     *
     * @throws Exception
     */
    @Test
    void testDuplicatedTableColumn() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                parse(DuplicatedColumn1.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
        assertThrows(Exception.class, () -> {
            try {
                parse(DuplicatedColumn.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    /*
     最多只能有一个集合字段
     */

    public static class Listable1 {

        public List<String> names;
        public List<String> values;
    }

    @Test
    void testListable1() throws Exception {
        Renderer renderer = parse(Listable1.class);
        assertNotNull(renderer);
        assertTrue(renderer instanceof MarkColumnGroup);

        MarkColumnGroup group = (MarkColumnGroup) renderer;
        assertEquals(0, group.getColumns().length);
    }

    public static class Listable2 {

        @TableColumn
        public List<String> names;
        @TableColumn
        public List<String> values;
    }

    @Test
    void testListable2() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(Listable2.class);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    public static class Listable3Child {

        @TableColumn
        public List<String> names;
        @TableColumn
        public List<String> values;
    }

    public static class Listable3 {

        @TableColumn
        public List<String> names;
        @TableColumnFlatten
        public Listable3Child child;
    }

    @Test
    void testListable3() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(Listable3.class);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    public static class Listable4Child {

        @TableColumn
        public List<String> names;
        @TableColumn
        public String value;
    }

    public static class Listable4 {

        @TableColumn
        public List<String> names;
        @TableColumnFlatten
        public Listable4Child child;
    }

    @Test
    void testListable4() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(Listable4.class);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    public static class Listable5Grandson {

        @TableColumn
        public List<Integer> scores;
        @TableColumn
        public String value;
    }

    public static class Listable5Child {

        @TableColumn
        public int names;
        @TableColumn
        public String value;
        @TableColumnFlatten
        public Listable5Grandson grandson;
    }

    public static class Listable5 {

        @TableColumn
        public List<String> names;
        @TableColumnFlatten
        public Listable5Child child;
    }

    @Test
    void testListable5() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(Listable5.class);
                System.out.println();
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }
}