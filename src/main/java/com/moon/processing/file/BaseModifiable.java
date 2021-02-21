package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author benshaoye
 */
abstract class BaseModifiable extends BaseImportable {

    private final Set<Modifier> modifiers = new TreeSet<>();

    public BaseModifiable(Importer importer) { super(importer); }

    /**
     * 应用修饰符
     *
     * @param modifier
     *
     * @return
     */
    public BaseModifiable addModifierWith(Modifier modifier) {
        if (getAllowedModifiers().contains(modifier)) {
            Modifier2.useModifier(modifiers, modifier);
        }
        return this;
    }

    public BaseModifiable remove(Modifier modifier) {
        modifiers.remove(modifier);
        return this;
    }

    public BaseModifiable withPrivate() { return addModifierWith(Modifier.PRIVATE); }

    public BaseModifiable withProtected() { return addModifierWith(Modifier.PROTECTED); }

    public BaseModifiable withPublic() { return addModifierWith(Modifier.PUBLIC); }

    public BaseModifiable withFinal() { return addModifierWith(Modifier.FINAL); }

    public BaseModifiable withStatic() { return addModifierWith(Modifier.STATIC); }

    public BaseModifiable withConst() { return withFinal().withStatic(); }

    public boolean isStatic() { return modifiers.contains(Modifier.STATIC); }

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
