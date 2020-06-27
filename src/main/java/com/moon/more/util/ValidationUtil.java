package com.moon.more.util;

import com.moon.core.util.DependencyUtil;
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
 * @author benshaoye
 */
public final class ValidationUtil extends ValidateUtil {

    private ValidationUtil() { }

    private static Validator getValidator() {
        return HibernateValidator6_1_18.FACTORY.getValidator();
    }

    public static <T> Object validate(T data, Class... groups) {
        return getValidator().validate(data, groups);
    }
}

@SuppressWarnings("all")
class HibernateValidator6_1_18 {

    final static ValidatorFactory FACTORY;

    static {
        ValidatorFactory factory = null;
        try {
            ProviderSpecificBootstrap<HibernateValidatorConfiguration> bootstrap =

                Validation.byProvider(HibernateValidator.class);

            HibernateValidatorConfiguration configuration = bootstrap.configure();

            factory = configuration.buildValidatorFactory();
        } catch (Throwable e) {
            // TODO 请确保正确引入了 hibernate-validator 相关依赖
            DepUtil.checkJavaxElDependency(e);
        }
        FACTORY = factory;
    }
}

class DepUtil {

    final static String ValidationEx = "javax.validation.ValidationException";

    static void checkJavaxElDependency(Throwable e) {
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
