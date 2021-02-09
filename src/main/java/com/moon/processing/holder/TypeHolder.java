package com.moon.processing.holder;

import com.moon.processing.decl.TypeDeclared;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class TypeHolder {

    private final NameHolder nameHolder;
    private final Map<String, TypeDeclared> typeDeclaredMap = new HashMap<>();

    public TypeHolder(NameHolder nameHolder) {
        this.nameHolder = nameHolder;
    }

    public TypeDeclared with(TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        TypeDeclared declared = typeDeclaredMap.get(classname);
        if (declared != null) {
            return declared;
        }
        declared = new TypeDeclared(element);
        typeDeclaredMap.put(classname, declared);
        return declared;
    }
}
