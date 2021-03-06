package com.moon.core.util;

import com.moon.core.time.DateUtil;
import com.moon.core.util.validator.ResidentID18Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author moonsky
 */
class ResidentID18ValidatorTestTest {

    String value;

    @Test
    void testAgeOfDuration() {
        value = "1968-07-09 00:00:00";
        value = "37010219680709292X";
        ResidentID18Validator validator = ResidentID18Validator.of(value);
        Date date = validator.getBirthday();
        Assertions.assertEquals(DateUtil.format(date), "1968-07-09 00:00:00");
        Assertions.assertTrue(validator.isValid());
        Assertions.assertTrue(validator.isFemale());
        Assertions.assertFalse(validator.isMale());

        value = "02051";
        validator = ResidentID18Validator.of(value);
        Assertions.assertTrue(validator.isInvalid());
        Assertions.assertFalse(validator.isMale());
        Assertions.assertFalse(validator.isFemale());
        date = validator.getBirthday();
        Assertions.assertNotEquals(DateUtil.format(date), "1968-07-09 00:00:00");

    }

    public static class Company {

        String username;
        String password;
        String companyName;

        public Company() {
        }

        public Company(String username, String password, String companyName) {
            this.username = username;
            this.password = password;
            this.companyName = companyName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }
    }
    //
    // @Test
    // void testWorkbook() {
    //     final String mark = "登录密码";
    //     String path = "";
    //     Workbook workbook;
    //     try {
    //         File file = new File(path);
    //         workbook = new XSSFWorkbook(file);
    //     } catch (Exception e) {
    //         System.out.println(e);
    //         return;
    //     }
    //     Sheet sheet = workbook.getSheetAt(0);
    //     Iterator<Row> iterator = sheet.iterator();
    //     List<Company> companies = new ArrayList<>();
    //     IteratorUtil.forEach(iterator, row -> {
    //         Cell usernameCell = row.getCell(1);
    //         Cell companyNameCell = row.getCell(2);
    //         String username = usernameCell.getStringCellValue();
    //         username = username.contains(mark) ? username.substring(0, username.indexOf(mark)) : username;
    //         String password = username.contains(mark) ? username.substring(username.indexOf(mark) + 4) : "sbgs001";
    //         companies.add(new Company(username, password, companyNameCell.getStringCellValue()));
    //     });
    //     // String json = JSON.toJSONString(companies);
    //     System.out.println(json);
    // }
}