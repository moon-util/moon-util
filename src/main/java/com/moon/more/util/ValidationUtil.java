package com.moon.more.util;

import com.moon.core.dep.Dependencies;
import com.moon.core.util.ValidateUtil;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.bootstrap.ProviderSpecificBootstrap;
import java.util.Set;

/**
 * Hibernate Validator 验证器
 *
 * @author benshaoye
 */
public final class ValidationUtil extends ValidateUtil {

    private ValidationUtil() { }

    /**
     * 验证实体类的某个字段值
     *
     * @param beanType   待验证的类
     * @param fieldName  字段名称
     * @param fieldValue 字段值
     * @param groups     分组
     * @param <T>        待验证的类的类型
     *
     * @return 如果验证通过，会返回一个空集合，否则返回的集合非空，里面包含错误信息
     */
    public static <T> Set<ConstraintViolation<T>> validateValue(
        Class beanType, String fieldName, Object fieldValue, Class... groups
    ) {
        try {
            return HibernateValidator6_1_18.getValidator().validateValue(beanType, fieldName, fieldValue, groups);
        } catch (Throwable throwable) {
            throw Dependencies.HIBERNATE_VALIDATOR.getException();
        }
    }

    /**
     * 验证对象指定字段的值
     *
     * @param data      待验证对象
     * @param fieldName 字段名称
     * @param groups    分组
     * @param <T>       待验证对象的类型
     *
     * @return 如果验证通过，会返回一个空集合，否则返回的集合非空，里面包含错误信息
     */
    public static <T> Set<ConstraintViolation<T>> validateField(T data, String fieldName, Class... groups) {
        try {
            return HibernateValidator6_1_18.getValidator().validateProperty(data, fieldName, groups);
        } catch (Throwable throwable) {
            throw Dependencies.HIBERNATE_VALIDATOR.getException();
        }
    }

    /**
     * 验证对象所有字段的值
     *
     * @param data   待验证对象
     * @param groups 分组
     * @param <T>    待验证对象的类型
     *
     * @return 如果验证通过，会返回一个空集合，否则返回的集合非空，里面包含错误信息
     */
    public static <T> Set<ConstraintViolation<T>> validate(T data, Class... groups) {
        try {
            return HibernateValidator6_1_18.getValidator().validate(data, groups);
        } catch (Throwable throwable) {
            throw Dependencies.HIBERNATE_VALIDATOR.getException();
        }
    }

    @SuppressWarnings("all")
    private static final class HibernateValidator6_1_18 {

        private final static Validator VALIDATOR;

        static {
            Validator validator = null;
            try {
                ProviderSpecificBootstrap<HibernateValidatorConfiguration> bootstrap =

                    Validation.byProvider(HibernateValidator.class);

                HibernateValidatorConfiguration configuration = bootstrap.configure();

                ValidatorFactory factory = configuration.buildValidatorFactory();
                validator = factory.getValidator();
            } catch (Throwable e) {
                // ignore
            }
            VALIDATOR = validator;
        }

        public static Validator getValidator() { return VALIDATOR; }
    }
}

/*
class DepUtil {

    final static String ValidationEx = "javax.validation.ValidationException";

    @SuppressWarnings("all")
    static void checkJavaxElDependency(Throwable e) {
        Dependencies.HIBERNATE_VALIDATOR.throwException();
        Throwable cause = e.getCause();
        if (cause instanceof NoClassDefFoundError) {
            String causeMessage = cause.getMessage();
            switch (causeMessage) {
                case "javax/el/ELManager":
                    String dependency = "javax.el:javax.el-api";
                    throw new IllegalArgumentException(getDepNode(dependency));
                default:
                    doThrow(e);
            }
        } else if (cause == null) {
            if (ValidationEx.equals(e.getClass().getName())) {
                String message = e.getMessage();
                if (message != null && message.contains("javax.el.ExpressionFactory")) {
                    String dependency = "org.glassfish.web:javax.el";
                    throw new IllegalArgumentException(getDepNode(dependency));
                }
            }
            doThrow(e);
        } else {
            doThrow(e);
        }
    }

    static void doThrow(Throwable t) {
        throw new IllegalStateException(t);
    }

    static String getDepNode(String dependency) {
        String error = "请确保存在相关依赖：%s; \n\t如果你使用的 Maven 管理依赖，请检查：\n%s";
        return String.format(error, dependency, DependencyUtil.dependencyXmlNode(dependency, 4, 4));
    }
}
 */
