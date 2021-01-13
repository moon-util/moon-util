package com.moon.processor.file;

import com.moon.processor.manager.Importable;
import com.moon.processor.manager.Importer;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.toList;

/**
 * @author benshaoye
 */
public class DeclField implements Importable {

    private final static String[] STRINGS = {};

    private Set<Modifier> modifiers = new TreeSet<>();
    private String modifiersDeclared;
    private Boolean getter, setter;
    private final Importer importer;
    private final String name;
    private String type;
    private String value;

    public DeclField(Importer importer, String name) {
        this.importer = importer;
        this.name = name;
        this.withPrivate();
    }

    private void resetModifiersDeclared() {
        this.modifiersDeclared = String.join(" ",
            modifiers.stream().map(m -> m.name().toLowerCase()).collect(toList()));
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

    public DeclField withGetterUsed(boolean used) {
        this.getter = used;
        return this;
    }

    public DeclField withSetterUsed(boolean used) {
        this.setter = used;
        return this;
    }

    public DeclField withGetterUsed() { return withGetterUsed(true); }

    public DeclField withSetterUsed() { return withSetterUsed(true); }

    public DeclField withGetterUnused() { return withGetterUsed(false); }

    public DeclField withSetterUnused() { return withSetterUsed(false); }

    public boolean isFinal(){ return modifiers.contains(Modifier.FINAL); }

    public boolean isStatic() { return modifiers.contains(Modifier.STATIC); }

    public boolean isHasSetter() {
        return !isFinal() && Boolean.TRUE.equals(setter);
    }

    public boolean isHasGetter() { return Boolean.TRUE.equals(getter); }

    public DeclField withFieldType(String pattern, Object... types) {
        this.type = Formatter2.toTypedFormatted(importer, pattern, types);
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
            String[] scripts = {name + " = " + value};
            return scripts;
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
