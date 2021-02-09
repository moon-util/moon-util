package com.moon.processing.file;

import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * @author benshaoye
 */
public class JavaField extends JavaAnnotable {

    private final String fieldType;
    private final String fieldName;

    public JavaField(Importer importer, String fieldName, String fieldTypeTemplate, Object... types) {
        super(importer);
        this.fieldType = String2.format(fieldTypeTemplate, types);
        this.fieldName = fieldName;
    }

    @Override
    public Set<Modifier> getAllowedModifiers() {
        return null;
    }
}
