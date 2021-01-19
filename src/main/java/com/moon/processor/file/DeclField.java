package com.moon.processor.file;

import com.moon.processor.holder.ConstManager;
import com.moon.processor.holder.Importable;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.moon.processor.file.Formatter2.onlyColonTail;

/**
 * @author benshaoye
 */
public class DeclField extends DeclModifier<DeclField> implements Importable {

    private final static String[] STRINGS = {};
    private final List<DeclMarked> annotations = new ArrayList<>();
    private DeclMethod getter, setter;
    private final String name, typeClassname;
    private String value;

    DeclField(ConstManager importer, String name, String typePattern, Object... types) {
        super(importer);
        String classname = Formatter2.toFormatted(typePattern, types);
        this.typeClassname = classname;
        this.name = name;
        this.withPrivate();
    }

    public DeclField markedOf(Function<? super ConstManager, ? extends DeclMarked> consumer) {
        return markedOf(consumer.apply(getImportable()));
    }

    public DeclField markedOf(DeclMarked marked) {
        this.annotations.add(marked);
        return this;
    }

    public DeclMarked markedOf(Class<?> annotationClass) {
        DeclMarked annotation = new DeclMarked(getImportable(), annotationClass);
        markedOf(annotation);
        return annotation;
    }

    public DeclMethod getGetterMethod() {
        DeclMethod getter = this.getter;
        if (getter == null) {
            return DeclMethod.EMPTY;
        }
        return getter.withPublic().returning(name).with(Modifier.STATIC, isStatic());
    }

    public DeclMethod getSetterMethod() {
        DeclMethod setter = this.setter;
        if (setter == null || isFinal()) {
            return DeclMethod.EMPTY;
        }
        return setter.scriptOf("this.{} = {}", name, name).withPublic().with(Modifier.STATIC, isStatic());
    }

    /**
     * 主要用于添加注解等，不要处理 setter 内部逻辑实现
     *
     * @param getterBuilder
     *
     * @return
     */
    public DeclField withGetterMethod(Consumer<DeclMethod> getterBuilder) {
        if (getterBuilder != null) {
            DeclMethod getter = DeclMethod.ofGetter(getImportable(), name, typeClassname);
            getterBuilder.accept(getter);
            this.getter = getter;
        }
        return this;
    }

    /**
     * 主要用于添加注解等，不要处理 setter 内部逻辑实现
     *
     * @param setterBuilder
     *
     * @return
     */
    public DeclField withSetterMethod(Consumer<DeclMethod> setterBuilder) {
        if (setterBuilder != null) {
            DeclMethod setter = DeclMethod.ofSetter(getImportable(), name, typeClassname);
            setterBuilder.accept(setter);
            this.setter = setter;
        }
        return this;
    }

    public DeclField valueOf(String pattern, Object... values) {
        this.value = Formatter2.toFormatted(pattern, values);
        return this;
    }

    private String getDeclareField() {
        return String2.format("{}{} {}", getModifiersDeclared(), onImported(typeClassname), name);
    }

    private String[] toScriptsOfBlock() {
        String value = this.value;
        String declare = getDeclareField();
        if (value != null && Formatter2.isOverLength(declare, value)) {
            return new String[]{onlyColonTail(name + " = " + value)};
        }
        return STRINGS;
    }

    public String[] getInstanceBlock() { return isStatic() ? STRINGS : toScriptsOfBlock(); }

    public String[] getStaticBlock() { return isStatic() ? toScriptsOfBlock() : STRINGS; }

    public List<String> getDeclareFieldScripts() {
        List<String> scripts = new ArrayList<>(getCommentScripts());
        this.annotations.forEach(annotation -> scripts.addAll(annotation.getScripts()));
        String value = this.value;
        String declare = getDeclareField();
        if (value == null || Formatter2.isOverLength(declare, value)) {
            scripts.add(onlyColonTail(declare));
        } else {
            scripts.add(onlyColonTail(declare + " = " + value));
        }
        return scripts;
    }

}
