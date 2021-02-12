package com.moon.processing.file;

import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class JavaField extends JavaAnnotable implements Appender {

    private final String fieldType;
    private final String fieldName;
    private JavaFieldValue value;

    public JavaField(Importer importer, String fieldName, String fieldTypeTemplate, Object... types) {
        super(importer);
        this.fieldType = String2.format(fieldTypeTemplate, types);
        this.fieldName = fieldName;
    }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Modifier2.FOR_FIELD; }

    public void valueOf(Consumer<JavaFieldValue> valueBuilder) {
        JavaFieldValue value = new JavaFieldValue(getImporter());
        valueBuilder.accept(value);
        this.value = value;
    }

    @Override
    public void appendTo(JavaAddr addr) {
        addr.next();
        super.appendTo(addr);
        for (Modifier modifier : getModifiers()) {
            addr.newAdd(modifier.name().toLowerCase()).add(' ');
        }
        addr.add(onImported(fieldType)).add(" ").add(fieldName);
        if (value != null) {
            addr.add(" = ").add(value);
        }
        addr.add(";");
    }
}
