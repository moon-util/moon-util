package com.moon.processor.file;

import com.moon.processor.holder.ConstManager;

import javax.lang.model.element.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.joining;

/**
 * @author benshaoye
 */
abstract class DeclModifier<T extends DeclModifier<T>> implements ImporterAware {

    private final Set<Modifier> modifiers = new TreeSet<>();
    private final ConstManager importer;
    private String modifiersDeclared;
    private DeclComment comment;

    DeclModifier(ConstManager importer) { this.importer = importer; }

    private void resetModifiersDeclared() {
        this.modifiersDeclared = modifiers.stream().map(m -> m.name().toLowerCase()).collect(joining(" ")) + " ";
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

    public ConstManager getImporter() { return importer; }

    @Override
    public ConstManager getImportable() { return importer; }

    /**
     * 目前是给表字段列举专用，也没考虑其他情况
     *
     * @param comments 注释
     */
    public final void commentOf(List<String> comments) {
        this.comment = new DeclComment(CommentType.BLOCK, comments);
    }

    public final DeclComment getComment() { return comment; }

    public final List<String> getCommentScripts() {
        return comment == null ? Collections.emptyList() : comment.getScripts();
    }

    public final String getModifiersDeclared() { return modifiersDeclared == null ? "" : modifiersDeclared; }

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
        return using ? withModifier(modifier) : returning();
    }

    public final T withPublic() { return with(Modifier.PUBLIC); }

    public final T withProtected() { return with(Modifier.PROTECTED); }

    public final T withPrivate() { return with(Modifier.PRIVATE); }

    public final T withFinal() { return withModifier(Modifier.FINAL); }

    public final T withStatic() { return withModifier(Modifier.STATIC); }

    public final boolean isFinal() { return modifiers.contains(Modifier.FINAL); }

    public final boolean isStatic() { return modifiers.contains(Modifier.STATIC); }
}
