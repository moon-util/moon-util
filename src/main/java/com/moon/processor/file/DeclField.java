package com.moon.processor.file;

import com.moon.processor.manager.Importable;
import com.moon.processor.manager.Importer;
import com.moon.processor.utils.Log2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.moon.processor.file.Formatter2.onlyColonTail;

/**
 * @author benshaoye
 */
public class DeclField extends DeclModifier<DeclField> implements Importable {

    private final static String[] STRINGS = {};
    private final List<DeclAnnotation> annotations = new ArrayList<>();
    private Boolean canGetter, canSetter;
    private final String name, type;
    private String value;

    public DeclField(Importer importer, String name, String typePattern, Object... types) {
        super(importer);
        this.type = importer.onImported(Formatter2.toFormatted(typePattern, types));
        this.name = name;
        this.withPrivate();
    }

    public DeclAnnotation annotatedOf(Class<?> annotationClass) {
        DeclAnnotation annotation = new DeclAnnotation(getImportable(), annotationClass);
        this.annotations.add(annotation);
        return annotation;
    }

    public DeclMethod getGetterMethod() {
        if (!Boolean.TRUE.equals(canGetter)) {
            return DeclMethod.EMPTY;
        }
        DeclMethod getter = DeclMethod.ofGetter(getImportable(), name, type);
        return getter.returning(name).with(Modifier.STATIC, isStatic());
    }

    public DeclMethod getSetterMethod() {
        if (!Boolean.TRUE.equals(canSetter) || isFinal()) {
            return DeclMethod.EMPTY;
        }
        DeclMethod setter = DeclMethod.ofSetter(getImportable(), name, type);
        return setter.with(Modifier.STATIC, isStatic());
    }

    public DeclField withGetterUsed(Boolean value) {
        this.canGetter = value;
        return this;
    }

    public DeclField withSetterUsed(Boolean value) {
        this.canSetter = value;
        return this;
    }

    public DeclField withGetterUsed() { return withGetterUsed(Boolean.TRUE); }

    public DeclField withSetterUsed() { return withSetterUsed(Boolean.TRUE); }

    public DeclField withGetterUnused() { return withGetterUsed(Boolean.FALSE); }

    public DeclField withSetterUnused() { return withSetterUsed(Boolean.FALSE); }

    public DeclField valueOf(String pattern, Object... values) {
        this.value = Formatter2.toFormatted(pattern, values);
        return this;
    }

    private String getDeclareField() {
        return String2.format("{} {} {}", getModifiersDeclared(), onImported(type), name);
    }

    private String[] toScriptsOfBlock() {
        String value = this.value;
        String declare = getDeclareField();
        if (Formatter2.isOverLength(declare, value)) {
            return new String[]{onlyColonTail(name + " = " + value)};
        }
        return STRINGS;
    }

    public String[] getInstanceBlock() { return isStatic() ? STRINGS : toScriptsOfBlock(); }

    public String[] getStaticBlock() { return isStatic() ? toScriptsOfBlock() : STRINGS; }

    public List<String> getDeclareFieldScripts() {
        List<String> scripts = new ArrayList<>();
        this.annotations.forEach(annotation -> scripts.addAll(annotation.getScripts()));
        String value = this.value;
        String declare = getDeclareField();
        if (Formatter2.isOverLength(declare, value)) {
            scripts.add(onlyColonTail(declare));
        } else {
            scripts.add(onlyColonTail(declare + " = " + value));
        }
        return scripts;
    }

}
