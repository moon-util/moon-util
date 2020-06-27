package com.moon.core.enums;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.util.DependencyUtil;

import static com.moon.core.util.DependencyUtil.dependencyXmlNode;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
public enum Dependencies {
    /**
     * hibernate-validator 表单验证相关依赖
     */
    HIBERNATE_VALIDATOR(//
        "org.hibernate.validator:hibernate-validator",//
        "org.glassfish.web:javax.el",//
        "javax.el:javax.el-api"),
    /**
     * poi 基本依赖，office 2003 以上版本
     */
    XLS("org.apache.poi:poi:4.x+"),
    /**
     * poi 基本依赖，office 2007 以上版本
     */
    XLSX("org.apache.poi:poi:4.x+", "org.apache.poi:poi-ooxml:4.x+"),
    ;

    private final String[] dependencies;

    Dependencies(String... dependencies) {
        this.dependencies = dependencies == null ? Arrays2.STRINGS.empty() : dependencies;
    }

    public String getDependencies() {
        String[] deps = this.dependencies;
        String[] strs = new String[deps.length];
        for (int i = 0; i < deps.length; i++) {
            strs[i] = dependencyXmlNode(deps[i], 4, 4);
        }
        return JoinerUtil.join(strs, "\n");
    }

    public <T> T throwException() {
        String error = "请确保存在相关依赖包：%s; \n\n\t如果你使用的 Maven 管理，请检查：\n%s\n";
        String deps = JoinerUtil.join(dependencies, "; ");
        throw new IllegalStateException(String.format(error, deps, getDependencies()));
    }
}
