package com.moon.processing.holder;

import com.moon.processing.decl.RecordDeclared;
import com.moon.processing.decl.TypeDeclared;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class RecordHolder {

    private final TypeHolder typeHolder;
    private final Map<String, RecordDeclared> recordDeclaredMap = new HashMap<>();

    public RecordHolder(TypeHolder typeHolder) {this.typeHolder = typeHolder;}

    public RecordDeclared with(TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        RecordDeclared declared = recordDeclaredMap.get(classname);
        if (declared != null) {
            return declared;
        }
        TypeDeclared typeDeclared = typeHolder.with(element);
        declared = new RecordDeclared(typeDeclared);
        recordDeclaredMap.put(classname, declared);
        return declared;
    }
}