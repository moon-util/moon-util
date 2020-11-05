package com.moon.mapping;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class MappingUtilTestTest {

    public static class UserDetailEntity {

        private String username;

        private String password;
    }

    public static class UserVO {

        private String username;

        private String password;
    }

    @Test
    void testOverride() throws Exception {
        BeanMapping<UserDetailEntity, UserVO> mapping = MappingUtil.thisPrimary();
        // UserVO vo = mapping.newThat(new UserDetailEntity());
    }
}