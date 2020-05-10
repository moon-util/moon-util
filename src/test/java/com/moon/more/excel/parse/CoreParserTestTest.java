package com.moon.more.excel.parse;

import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author benshaoye
 */
class CoreParserTestTest extends ParseUtil {

    @Test
    void testDoParseAsCol() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new Top());
            });
        }).write2Filepath("D:/test.xlsx");
    }

    public static class Top {

        @TableColumn
        private String name = RandomStringUtil.nextChinese(2, 5);

        @TableColumn
        List<String> middleList;

        public Top() {
            List<String> middleList = new ArrayList<>();
            this.middleList = middleList;
            for (int i = 0; i < RandomUtil.nextInt(5, 10); i++) {
                middleList.add(RandomStringUtil.nextLetter(4, 6));
            }
        }
    }

    public static class Middle {

        @TableColumn
        private List<String> names;

        @TableColumn
        private Bottom bottom;

        @TableColumn
        private int value;

        List<Bottom> bottomList;
    }

    public static class Bottom {

        @TableColumn
        String name;

        @TableColumn
        List<String> values;
    }

    @Test
    void testDuplicateList() {
        assertThrows(Exception.class, () -> {
            try {
                parse(DuplicateIterated.class);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                System.out.println();
                throw e;
            }
        });
        assertThrows(Exception.class, () -> {
            try {
                parse(Duplicated.class);
            } catch (RuntimeException e) {

                System.out.println(e.getMessage());
                System.out.println();
                throw e;
            }
        });
    }

    public static class Duplicated {

        @TableColumn
        List<String> names;

        @TableColumnFlatten
        Flatted flatted;
    }

    private static class Flatted {

        @TableColumn
        List<String> values;
    }

    public static class DuplicateIterated {

        @TableColumn
        List<String> names;

        @TableColumn
        List<String> values;
    }
}