package com.moon.processing.file;

import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import java.util.Objects;

/**
 * @author benshaoye
 */
public class JavaFieldValue extends BaseImportable {

    private final String classname;

    private String value;

    public JavaFieldValue(Importer importer, String classname) {
        super(importer);
        this.classname = classname;
    }

    public void valueOf(Object value) { this.value = String.valueOf(value); }

    public void stringOf(String value) { valueOf('"' + value + '"'); }

    public void ofFormatted(String template, Object... values) {
        valueOf(String2.format(template, values));
    }

    public void staticRef(String classname, String staticRef) {
        this.value = String2.format("{}.{}", onImported(classname), staticRef);
    }

    public void enumMemberRef(String enumClass, String enumValue, String memberRef) {
        if (Objects.equals(classname, enumClass)) {
            thisEnumRef(enumValue, memberRef);
        } else {
            this.value = String2.format("{}.{}.{}", onImported(classname), enumValue, memberRef);
        }
    }

    public void thisEnumRef(String enumValue, String memberRef) {
        this.value = String2.format("{}.{}", enumValue, memberRef);
    }

    @Override
    public String toString() { return value == null ? "null" : value; }
}
