package com.moon.data.jdbc.processing;

import com.moon.data.jdbc.annotation.Provided;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

/**
 * @author benshaoye
 */
final class Assert {

    private Assert() {}

    static void assertProvidedMethod(ExecutableElement elem, Provided provided) {
        List<? extends VariableElement> params = elem.getParameters();
        if (params != null && !params.isEmpty()) {
            String str = "被 @Provided 注解的方法 '{name}' 不应有参数";
            throw new IllegalStateException(Replacer.name.replace(str, elem));
        }
        if (StringUtils.isBlank(provided.value())) {
            String name = StringUtils.getSimpleName(elem);
            if (!name.startsWith(Const.GET)) {
                String str = "方法 {name} 名应以 'get' 开头的 'getter' 方法或指定 @Provided.value";
                throw new IllegalStateException(Replacer.name.replace(str, name));
            }
        }
    }
}
