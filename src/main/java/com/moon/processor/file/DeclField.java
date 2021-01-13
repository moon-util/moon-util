package com.moon.processor.file;

import com.moon.processor.manager.Importable;
import com.moon.processor.manager.Importer;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

/**
 * @author benshaoye
 */
public class DeclField implements Importable {

    private final static String[] STRINGS = {};

    private final Set<Modifier> modifiers = new TreeSet<>();
    private String modifiersDeclared;
    private DeclMethod getter, setter;
    private final Importer importer;
    private final String name, type;
    private String value;

    public DeclField(Importer importer, String name, String typePattern, Object... types) {
        this.type = Formatter2.toTypedFormatted(importer, typePattern, types);
        this.importer = importer;
        this.name = name;
        this.withPrivate();
    }

    private void resetModifiersDeclared() {
        this.modifiersDeclared = modifiers.stream().map(m -> m.name().toLowerCase()).collect(joining(" "));
    }

    private void removeAccessLevels() {
        Set<Modifier> modifiers = this.modifiers;
        modifiers.remove(Modifier.PRIVATE);
        modifiers.remove(Modifier.PROTECTED);
        modifiers.remove(Modifier.PUBLIC);
    }

    private DeclField withModifier(Modifier modifier) {
        modifiers.add(modifier);
        resetModifiersDeclared();
        return this;
    }

    @Override
    public String onImported(Class<?> classname) {
        return importer.onImported(classname);
    }

    @Override
    public String onImported(String classname) {
        return importer.onImported(classname);
    }

    public DeclField remove(Modifier modifier) {
        modifiers.remove(modifier);
        resetModifiersDeclared();
        return this;
    }

    public DeclField clearAccessLevels() {
        removeAccessLevels();
        resetModifiersDeclared();
        return this;
    }

    /**
     * 只限部分: PUBLIC、PROTECTED、PRIVATE、FINAL、STATIC
     *
     * @param modifier
     *
     * @return
     */
    private DeclField with(Modifier modifier) {
        removeAccessLevels();
        modifiers.add(modifier);
        resetModifiersDeclared();
        return this;
    }

    public DeclField withPublic() { return with(Modifier.PUBLIC); }

    public DeclField withProtected() { return with(Modifier.PROTECTED); }

    public DeclField withPrivate() { return with(Modifier.PRIVATE); }

    public DeclField withFinal() { return withModifier(Modifier.FINAL); }

    public DeclField withStatic() { return withModifier(Modifier.STATIC); }

    public boolean isFinal() { return modifiers.contains(Modifier.FINAL); }

    public boolean isStatic() { return modifiers.contains(Modifier.STATIC); }

    public DeclMethod getGetter() {
        return getter == null ? DeclMethod.EMPTY : getter.returnTypeof(type).returning(name);
    }

    public DeclMethod getSetter() {
        if (setter == null || isFinal()) {
            return DeclMethod.EMPTY;
        }
        return setter.scriptOf("this.{} = {}", name, name);
    }

    public DeclField withGetterConfigured() {
        DeclMethod getter = this.getter;
        if (getter == null) {
            getter = DeclMethod.ofGetter(importer, name, type);
            this.getter = getter;
        }
        return this;
    }

    public DeclField withSetterConfigured() {
        DeclMethod setter = this.setter;
        if (setter == null) {
            setter = DeclMethod.ofSetter(importer, name, type);
            this.setter = setter;
        }
        return this;
    }

    public DeclField valueOf(String pattern, Object... values) {
        this.value = Formatter2.toFormatted(importer, pattern, values);
        return this;
    }

    private String getDeclareField() {
        return String2.format("{} {} {}", modifiersDeclared, type, name);
    }

    private String[] toScriptsOfBlock() {
        String value = this.value;
        String declare = getDeclareField();
        if (Formatter2.isOverLength(declare.length() + value.length())) {
            return new String[]{name + " = " + value};
        }
        return STRINGS;
    }

    public String[] getInstanceBlock() {
        return isStatic() ? STRINGS : toScriptsOfBlock();
    }

    public String[] getStaticBlock() {
        return isStatic() ? toScriptsOfBlock() : STRINGS;
    }

    public String getDeclareFieldScript() {
        String value = this.value;
        String declare = getDeclareField();
        if (Formatter2.isOverLength(declare.length() + value.length())) {
            return declare;
        }
        return declare + " = " + value;
    }
}
