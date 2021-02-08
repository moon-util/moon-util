package com.moon.processor.create;

import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author benshaoye
 */
abstract class BaseDeclared {

    private final Importer importer;

    private final Set<Modifier> modifiers = new TreeSet<>();

    public BaseDeclared(Importer importer) {
        this.importer = importer;
    }

    public Importer getImporter() { return importer; }

    public BaseDeclared onImported(Class<?> importClass) {
        importer.onImported(importClass);
        return this;
    }

    public BaseDeclared onImported(String classname) {
        importer.onImported(classname);
        return this;
    }

    public BaseDeclared addModifierWith(Modifier modifier) {
        if (getAllowedModifiers().contains(modifier)) {
            modifiers.add(modifier);
            afterModifierAdded(modifier);
        }
        return this;
    }

    public Set<Modifier> getModifiers() { return modifiers; }

    public void afterModifierAdded(Modifier modifierAdded) {
    }

    public abstract Set<Modifier> getAllowedModifiers();
}
