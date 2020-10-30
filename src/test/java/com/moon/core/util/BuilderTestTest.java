package com.moon.core.util;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
class BuilderTestTest {

    @Data
    public static class GirlFriend {

        private String name;
        private int age;
        private int bust;
        private int waist;
        private int hips;
        private List<String> hobbies = new ArrayList<>();
        private String birthday;
        private String address;
        private String mobile;
        private String email;
        private String hairColor;

        public void addHobby(String hobby) {
            hobbies.add(hobby);
        }

        public void setDetail(String address, String mobile) {
            this.address = address;
            this.mobile = mobile;
        }
    }

    @Test
    void testOf() {
        GirlFriend friend = Builder.of(GirlFriend::new)//
            .with(GirlFriend::setName, "张三")//
            .with(GirlFriend::setAge, 25)//
            .with(GirlFriend::setDetail, "杭州一中", "13712345678")//
            .with(GirlFriend::addHobby, "旅游")//
            .build();
        System.out.println(friend);
    }
}