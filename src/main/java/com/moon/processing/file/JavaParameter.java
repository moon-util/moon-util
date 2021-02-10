package com.moon.processing.file;

import com.moon.processing.decl.Generic2;
import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * @author benshaoye
 */
public class JavaParameter extends JavaAnnotable {

    /**
     * 参数类型，类全名/泛型全名
     * <p>
     * java.lang.String, T, E
     */
    private final String type;
    /**
     * 简化的类型，
     * type: java.util.List&lt;java.lang.String>
     * typeSimplify: java.util.List
     * <p>
     * 方法签名的重载判断依据是根据简化类型判断的
     */
    private final String typeSimplify;
    /**
     * 参数名
     */
    private final String name;

    public JavaParameter(Importer importer, String name, String typeTemplate, Object... types) {
        super(importer);
        this.type = Type2.format(typeTemplate, types);
        this.name = name;
        this.typeSimplify = Generic2.typeSimplify(type);
    }

    public String getType() { return type; }

    public String getTypeSimplify() { return typeSimplify; }

    public String getName() { return name; }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Modifier2.FOR_PARAMETER; }
}
