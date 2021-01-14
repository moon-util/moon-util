package com.moon.processor.file;

import com.moon.processor.manager.Importable;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.joining;

/**
 * @author benshaoye
 */
abstract class DeclModifier<T extends DeclModifier<T>> implements ImporterAware {

    private final Set<Modifier> modifiers = new TreeSet<>();
    private final Importable importer;
    private String modifiersDeclared;

    public DeclModifier(Importable importer) { this.importer = importer; }

    private void resetModifiersDeclared() {
        this.modifiersDeclared = modifiers.stream().map(m -> m.name().toLowerCase()).collect(joining(" "));
    }

    private void removeAccessLevels() {
        Set<Modifier> modifiers = this.modifiers;
        modifiers.remove(Modifier.PRIVATE);
        modifiers.remove(Modifier.PROTECTED);
        modifiers.remove(Modifier.PUBLIC);
    }

    private T returning() { return (T) this; }

    private T withModifier(Modifier modifier) {
        modifiers.add(modifier);
        resetModifiersDeclared();
        return returning();
    }

    public Importable getImporter() { return importer; }

    @Override
    public Importable getImportable() { return importer; }

    public final String getModifiersDeclared() { return modifiersDeclared; }

    public final T remove(Modifier modifier) {
        modifiers.remove(modifier);
        resetModifiersDeclared();
        return returning();
    }

    public final T clearAccessLevels() {
        removeAccessLevels();
        resetModifiersDeclared();
        return returning();
    }

    /**
     * 只限部分: PUBLIC、PROTECTED、PRIVATE、FINAL、STATIC
     *
     * @param modifier
     *
     * @return
     */
    private T with(Modifier modifier) {
        removeAccessLevels();
        modifiers.add(modifier);
        resetModifiersDeclared();
        return returning();
    }

    public final T with(Modifier modifier, boolean using) {
        return using ? with(modifier) : returning();
    }

    public final T withPublic() { return with(Modifier.PUBLIC); }

    public final T withProtected() { return with(Modifier.PROTECTED); }

    public final T withPrivate() { return with(Modifier.PRIVATE); }

    public final T withFinal() { return withModifier(Modifier.FINAL); }

    public final T withStatic() { return withModifier(Modifier.STATIC); }

    public final boolean isFinal() { return modifiers.contains(Modifier.FINAL); }

    public final boolean isStatic() { return modifiers.contains(Modifier.STATIC); }
}
