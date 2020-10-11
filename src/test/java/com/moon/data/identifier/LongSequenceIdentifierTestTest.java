package com.moon.data.identifier;

import com.moon.core.lang.StringUtil;
import com.moon.core.time.DatePatternConst;
import com.moon.core.time.DateUtil;
import com.moon.core.time.Datetime;
import com.moon.poi.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

/**
 * @author moonsky
 */
class LongSequenceIdentifierTestTest {

    enum LibraryEnum {
        厚泽("HOUZE"),
        国旺("GUO_WANG"),
        互联("CT_HU_LIAN"),
        诚通互联("CT_HU_LIAN"),
        ;

        private final String name;

        LibraryEnum(String name) {
            this.name = name;
        }
    }

    static Collection<String> parse(Sheet sheet, String template) {
        Map<String, String> updates = new LinkedHashMap<>();
        Iterator<Row> iterator = sheet.iterator();
        iterator.next();
        iterator.next();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            LibraryEnum library = LibraryEnum.valueOf(ExcelUtil.getStringValue(row.getCell(2)));
            String memberName = ExcelUtil.getStringValue(row.getCell(6));
            String memberCertNo = ExcelUtil.getStringValue(row.getCell(7));
            updates.put(memberCertNo, StringUtil.format(template, library.name, memberName, memberCertNo));
        }
        return updates.values();
    }

    @Test
    void testName() throws Exception {
        String ss = "UPDATE t_cy_member_info_basic SET pay_library='{}' WHERE name='{}' AND cert_no='{}';";
        String af = "UPDATE t_cy_member_info_basic SET af_pay_library='{}' WHERE name='{}' AND cert_no='{}';";

        ExcelUtil.load(new File("D:/", "2020.8.28零工宝.xlsx")).sheet("社保672人", sheetFactory -> {
            // parse(sheetFactory.getSheet(), ss).forEach(System.out::println);
            sheetFactory.forEach(1, rowFactory -> {
                // rowFactory.next
                String library = rowFactory.useCell(2).getValue(StringUtil::stringify);
                String name = rowFactory.useCell(6).getValue(StringUtil::stringify);
                String certNo = rowFactory.useCell(7).getValue(StringUtil::stringify);
                String info = StringUtil.format("Library: {}, Name: {}, CertNo: {}", library, name, certNo);
                System.out.println(info);
            });
        }).sheet("住房372", sheetFactory -> {
            // parse(sheetFactory.getSheet(), af).forEach(System.out::println);
        });
    }

    @Test
    void testNextId() throws Exception {
        long prevId = Long.MIN_VALUE;
        LongSequenceIdentifier identifier = LongSequenceIdentifier.of();
        for (int i = 0; i < 15151514; i++) {
            long thisId = identifier.nextId();
            assert thisId > prevId;
            prevId = thisId;
        }

        System.out.println(String.valueOf(identifier.nextId()).length());
    }

    @Test
    void testStartTime() throws Exception {
        System.out.println(Datetime.of(2020, 9, 1, 0, 0, 0, 0, 0).getTime());
        ;

        final long twepoch = 1288834974657L;
        Date date = DateUtil.toDate(twepoch);
        System.out.println(DateUtil.format(date, DatePatternConst.yyyy_MM_dd_HH_mm_ss_SSS));
    }

    @Test
    void testStartTime1() throws Exception {

        final long twepoch = 1598889600000L;
        Date date = DateUtil.toDate(twepoch);
        System.out.println(DateUtil.format(date, DatePatternConst.yyyy_MM_dd_HH_mm_ss_SSS));
    }
}