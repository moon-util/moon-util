package com.moon.processing.file;

import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class JavaMethod extends JavaBlockCommentable {

    private final String name;
    private final JavaParameters parameters;
    private final String signature;
    private final boolean inInterface;
    private final List<LineScripter> scripters = new ArrayList<>();
    private boolean propertyMethod;
    private LineScripter returning;
    private String returnType;

    public JavaMethod(
        Importer importer, String name, JavaParameters parameters, boolean inInterface
    ) {
        super(importer);
        this.name = name;
        this.parameters = parameters.asUnmodifiable();
        this.inInterface = inInterface;
        this.signature = String.format("%s(%s)", name, parameters.getSignature());
    }

    public JavaMethod withPropertyMethod() { return withPropertyMethod(true); }

    public JavaMethod withPropertyMethod(boolean propertyMethod) {
        this.propertyMethod = propertyMethod;
        return this;
    }

    public String getName() { return name; }

    public boolean isPropertyMethod() { return propertyMethod; }

    public JavaMethod typeOf(Class<?> returnType) {
        return typeOf(returnType.getCanonicalName());
    }

    public JavaMethod typeOf(String typeTemplate, Object... types) {
        this.returnType = Formatter.with(typeTemplate, types);
        return this;
    }

    /**
     * 这里获取的{@link JavaParameters}不可增减参数、修改参数类型、名称
     * <p>
     * 只可以添加注解、注释以及获取参数信息等
     *
     * @return
     */
    public JavaParameters getParameters() { return parameters; }

    public String getUniqueKey() { return signature; }

    public JavaMethod override() {
        annotationOf(Override.class);
        return this;
    }

    public JavaMethod returnTypeFormatted(String template, Object... values) {
        return template == null ? returnNull() : returning(Formatter.with(template, values));
    }

    public JavaMethod returnFormatted(String template, Object... values){
        if (template == null) {
            return returnNull();
        }
        return returning(String2.format(template, values));
    }

    public JavaMethod returnStringify(String template, Object... values) {
        if (template == null) {
            return returnNull();
        }
        String formatted = Formatter.with(template, values);
        return returning(new StringBuilder().append('"').append(formatted).append('"'));
    }

    public JavaMethod returnNull() { return returning("null"); }

    public JavaMethod returning(Object script) {
        String stringify = "return " + script.toString().trim();
        this.returning = new LineScripterImpl(stringify);
        return this;
    }

    public JavaMethod nextFormatted(String template, Object... values) {
        return nextScript(String2.format(template, values));
    }

    public JavaMethod nextScript(String script) {
        scripters.add(new LineScripterImpl(script));
        return this;
    }

    public JavaMethod nextScript(String script, Consumer<LineScripter> using) {
        LineScripter scripter = new LineScripterImpl(script);
        using.accept(scripter);
        scripters.add(scripter);
        return this;
    }

    public JavaMethod nextFormatted(String template, Object v1, Consumer<LineScripter> using) {
        return nextFormatted(using, template, arr(v1));
    }

    public JavaMethod nextFormatted(String template, Object v1, Object v2, Consumer<LineScripter> using) {
        return nextFormatted(using, template, arr(v1, v2));
    }

    public JavaMethod nextFormatted(String template, Object v1, Object v2, Object v3, Consumer<LineScripter> using) {
        return nextFormatted(using, template, arr(v1, v2, v3));
    }

    public JavaMethod nextFormatted(
        String template, Object v1, Object v2, Object v3, Object v4, Consumer<LineScripter> using
    ) {
        return nextFormatted(using, template, arr(v1, v2, v3, v4));
    }

    public JavaMethod nextFormatted(
        String template, Object v1, Object v2, Object v3, Object v4, Object v5, Consumer<LineScripter> using
    ) {
        return nextFormatted(using, template, arr(v1, v2, v3, v4, v5));
    }

    public JavaMethod nextFormatted(Consumer<LineScripter> using, String template, Object... values) {
        return nextScript(String2.format(template, values), using);
    }

    private static Object[] arr(Object... values) { return values; }

    @Override
    public void appendTo(JavaAddr addr) {
        super.appendTo(addr.next());
        addr.next().padAdd("");
        // 修饰符
        for (Modifier modifier : getModifiers()) {
            addr.add(modifier.name().toLowerCase()).add(' ');
        }

        // 签名 & start
        addr.add(getReturnType()).add(" ").add(name).add("(");
        getParameters().appendTo(addr);
        addr.add(")").add(" {");

        if (scripters.isEmpty()) {
            appendOnly(addr, returning);
        } else if (scripters.size() == 1 && returning == null) {
            appendOnly(addr, scripters.get(0));
        } else {
            // body
            addr.start();
            // 收集无序脚本
            List<LineScripter> unsorted = new ArrayList<>();
            for (LineScripter scripter : scripters) {
                if (scripter.isSorted()) {
                    scripter.appendTo(addr);
                } else {
                    unsorted.add(scripter);
                }
            }
            unsorted.sort((o1, o2) -> o2.length() - o1.length());
            for (LineScripter scripter : unsorted) {
                scripter.appendTo(addr);
            }
            if (returning != null) {
                returning.appendTo(addr);
            }
            // close
            addr.newEnd("}");
        }
    }

    private static void appendOnly(JavaAddr addr, LineScripter scripter) {
        if (scripter != null) {
            String script = scripter.getLineScript();
            if (addr.willOverLength(script)) {
                addr.start();
                scripter.appendTo(addr);
                addr.newEnd("}");
                return;
            } else {
                addr.add(' ').add(scripter.getLineScript()).scriptEnd();
            }
        }
        addr.add(" }");
    }

    private String getReturnType() {
        String type = this.returnType;
        return type == null ? "void" : onImported(type);
    }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Modifier2.FOR_METHOD; }
}
