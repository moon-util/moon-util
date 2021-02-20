package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author benshaoye
 */
public class JavaMethod extends JavaBlockCommentable {

    private final String name;
    private final JavaParameters parameters;
    private final String signature;
    private final boolean inInterface;
    private final List<Scripter> scripters = new ArrayList<>();
    private Scripter returning;
    private String returnType;

    public JavaMethod(Importer importer, String name, JavaParameters parameters, boolean inInterface) {
        super(importer);
        this.name = name;
        this.parameters = parameters;
        this.inInterface = inInterface;
        this.signature = String.format("%s(%s)", name, parameters.getSignature());
    }

    public JavaMethod typeOf(Class<?> returnType) {
        return typeOf(returnType.getCanonicalName());
    }

    public JavaMethod typeOf(String typeTemplate, Object... types) {
        this.returnType = Formatter.with(typeTemplate, types);
        return this;
    }

    private JavaParameters getParameters() { return parameters; }

    public String getUniqueKey() { return signature; }

    public JavaMethod override() {
        annotationOf(Override.class);
        return this;
    }

    public JavaMethod returnFormatted(String template, Object... values) {
        return template == null ? returnNull() : returning(Formatter.with(template, values));
    }

    public JavaMethod returnString(String template, Object... values) {
        if (template == null) {
            return returnNull();
        }
        String formatted = Formatter.with(template, values);
        return returning(new StringBuilder().append('"').append(formatted).append('"'));
    }

    public JavaMethod returnNull() { return returning("null"); }

    public JavaMethod returning(Object script) {
        String scriptStr = "return " + script.toString().trim();
        this.returning = addr -> addr.newScript(scriptStr);
        return this;
    }

    public JavaMethod nextScript(String script) {
        scripters.add(addr -> addr.newScript(script));
        return this;
    }

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
        addr.add(")").add(" {").start();

        // body
        for (Scripter scripter : scripters) {
            scripter.addJavaScript(addr);
        }
        if (returning != null) {
            returning.addJavaScript(addr);
        }

        // close
        addr.newEnd("}");
    }

    private String getReturnType() {
        String type = this.returnType;
        return type == null ? "void" : onImported(type);
    }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Modifier2.FOR_METHOD; }
}
