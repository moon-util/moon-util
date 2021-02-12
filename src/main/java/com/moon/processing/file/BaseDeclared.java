package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author benshaoye
 */
abstract class BaseDeclared extends BaseImportable {

    private final Set<Modifier> modifiers = new TreeSet<>();

    public BaseDeclared(Importer importer) { super(importer); }

    @Override
    public BaseDeclared onImported(Class<?> importClass) {
        super.onImported(importClass);
        return this;
    }

    @Override
    public BaseDeclared onImported(String classname) {
        super.onImported(classname);
        return this;
    }

    /**
     * 应用修饰符
     *
     * @param modifier
     *
     * @return
     */
    public BaseDeclared addModifierWith(Modifier modifier) {
        if (getAllowedModifiers().contains(modifier)) {
            Modifier2.useModifier(modifiers, modifier);
        }
        return this;
    }

    public BaseDeclared remove(Modifier modifier) {
        modifiers.remove(modifier);
        return this;
    }

    public BaseDeclared withPrivate() { return addModifierWith(Modifier.PRIVATE); }

    public BaseDeclared withProtected() { return addModifierWith(Modifier.PROTECTED); }

    public BaseDeclared withPublic() { return addModifierWith(Modifier.PUBLIC); }

    public BaseDeclared withFinal() { return addModifierWith(Modifier.FINAL); }

    public BaseDeclared withStatic() { return addModifierWith(Modifier.STATIC); }

    public BaseDeclared withConst() { return withFinal().withStatic(); }

    /**
     * 获取最终的所有修饰符
     *
     * @return
     */
    public Set<Modifier> getModifiers() { return modifiers; }

    /**
     * 获取当前元素所有允许应用的修饰符
     *
     * @return
     */
    public abstract Set<Modifier> getAllowedModifiers();
}
