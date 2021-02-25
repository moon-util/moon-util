package com.moon.processing.holder;

import com.moon.processing.JavaFiler;
import com.moon.processing.JavaWritable;
import com.moon.processing.decl.RecordDeclared;
import com.moon.processing.decl.TypeDeclared;
import com.moon.processing.util.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单的 POJO 对象，暂时仅用于给 Copier、Mapper 用
 *
 * @author benshaoye
 */
public class RecordHolder extends BaseHolder implements JavaWritable {

    private final Map<String, RecordDeclared> recordDeclaredMap = new HashMap<>();

    public RecordHolder(Holders holders) { super(holders); }

    public RecordDeclared with(TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        RecordDeclared declared = recordDeclaredMap.get(classname);
        if (declared != null) {
            return declared;
        }
        TypeDeclared typeDeclared = typeHolder().with(element);
        declared = new RecordDeclared(typeDeclared);
        recordDeclaredMap.put(classname, declared);
        return declared;
    }

    @Override
    public void write(JavaFiler writer) {

    }
}
