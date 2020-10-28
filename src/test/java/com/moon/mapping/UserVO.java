package com.moon.mapping;

/**
 * @author moonsky
 */
public class UserVO {

    private String username;

    private String password;

    public UserVO() {
    }

    public static class NonSetterGetterOperator {

        public static void override(UserVO vo, UserDetailEntity detail) {
            System.out.println("====");
        }
    }
}
