package com.moon.spring.data.jpa.factory;

import com.moon.core.lang.ThrowUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author moonsky
 */
final class RepositoryUtil {

    private RepositoryUtil() { ThrowUtil.noInstanceError(); }

    public static boolean isPresentJdbcTemplateBean(JpaRecordRepositoryMetadata metadata) {
        try {
            return isPresentJdbcTemplateBean(metadata.getApplicationContext());
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isPresentJdbcTemplateBean(ApplicationContext ctx) {
        try {
            ctx.getBean(JdbcTemplate.class);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
