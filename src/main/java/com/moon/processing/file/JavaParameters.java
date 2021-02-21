package com.moon.processing.file;

import com.moon.processing.decl.Generic2;
import com.moon.processor.holder.Importer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author benshaoye
 */
public class JavaParameters extends BaseImportable implements Appender {

    private final static int WRAP_THRESHOLD = 4;

    protected final Map<String, JavaParameter> parameters = new LinkedHashMap<>();
    private boolean hasAnnotation = false;

    JavaParameters(Importer importer) { super(importer); }

    public JavaParameter add(String name, Class<?> parameterType) {
        return add(name, parameterType.getCanonicalName());
    }

    public JavaParameter add(String name, String typeTemplate, Object... types) {
        JavaParameter parameter = new JavaParameter(getImporter(), name, typeTemplate, types);
        hasAnnotation = hasAnnotation || parameter.hasAnnotations();
        parameters.put(name, parameter);
        return parameter;
    }

    public JavaParameter get(String parameterName) {
        return parameters.get(parameterName);
    }

    public JavaParameter getFirstByTypeSimplify(String type) {
        String typeSimplify = Generic2.typeSimplify(type);
        for (JavaParameter value : parameters.values()) {
            if (Objects.equals(value.getTypeSimplify(), typeSimplify)) {
                return value;
            }
        }
        throw new IllegalStateException("Can not find parameter of type: " + type);
    }

    public String getSignature() {
        StringBuilder builder = new StringBuilder();
        final int lastIdx = parameters.size() - 1;
        int index = 0;
        for (Map.Entry<String, JavaParameter> entry : parameters.entrySet()) {
            builder.append(entry.getValue().getTypeSimplify());
            if ((index++) < lastIdx) {
                builder.append(',');
            }
        }
        return builder.toString();
    }

    /**
     * 这个主要是用于修改参数的注解、注释等
     * 不能修改参数类型、参数名称
     * <p>
     *
     * @return
     */
    public JavaParameters asUnmodifiable() { return new Unmodifiable(this); }

    private boolean hasAnnotation() {
        for (Map.Entry<String, JavaParameter> parameterEntry : getParameters().entrySet()) {
            if (parameterEntry.getValue().hasAnnotations()) {
                return true;
            }
        }
        return false;
    }

    private Map<String, JavaParameter> getParameters() { return parameters; }

    @Override
    public void appendTo(JavaAddr addr) {
        Map<String, JavaParameter> params = getParameters();
        final int size = params.size(), lastIdx = size - 1;
        int index = 0;
        if (hasAnnotation()) {
            addr.start();
            for (JavaParameter value : params.values()) {
                value.appendTo(addr);
                if ((index++) < lastIdx) {
                    addr.add(", ");
                }
            }
            addr.newEnd("");
        } else if (size > WRAP_THRESHOLD) {
            addr.start();
            for (JavaParameter param : params.values()) {
                addr.newAdd(onImported(param.getType())).add(" ").add(param.getName());
                if ((index++) < lastIdx) {
                    addr.add(", ");
                }
            }
            addr.newEnd("");
        } else {
            for (JavaParameter param : params.values()) {
                addr.add(onImported(param.getType())).add(" ").add(param.getName());
                if ((index++) < lastIdx) {
                    addr.add(", ");
                }
            }
        }
    }

    private static class Unmodifiable extends JavaParameters {

        Unmodifiable(JavaParameters parameters) {
            super(parameters.getImporter());
            this.parameters.putAll(parameters.parameters);
        }

        @Override
        public JavaParameter add(String name, Class<?> parameterType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JavaParameter add(String name, String typeTemplate, Object... types) {
            throw new UnsupportedOperationException();
        }
    }
}
