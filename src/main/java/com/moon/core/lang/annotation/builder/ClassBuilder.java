package com.moon.core.lang.annotation.builder;

import java.util.function.Consumer;

/**
 * @author moonsky
 */
public class ClassBuilder {

    public ClassBuilder visitPublic() {
        return this;
    }

    public ClassBuilder visitClass(String classname) {
        return this;
    }

    public ClassBuilder newBody(Consumer<ClassBuilder> builder) {
        builder.accept(this);
        return this;
    }

    public ClassBuilder visitFinal() {
        return this;
    }

    public ClassBuilder visitStatic() {
        return this;
    }

    public ClassBuilder visitSynchronized() {
        return this;
    }

    public ClassBuilder visitVoid() {
        return this;
    }

    public ClassBuilder visitMemberName(String classOrFieldName) {
        return this;
    }
}
