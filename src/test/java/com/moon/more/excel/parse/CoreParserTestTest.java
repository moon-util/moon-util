package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class CoreParserTestTest extends ParseUtil {

    @Test
    void testDoParseAsCol() throws Exception {
        parse(Top.class);
    }

    public static class Top {

        @TableColumnFlatten
        List<Middle> middleList;
    }

    public static class Middle {

        @TableColumnFlatten
        List<Bottom> bottomList;
    }

    public static class Bottom {

        @TableColumn
        String name;

        @TableColumn
        List<String> values;
    }
}