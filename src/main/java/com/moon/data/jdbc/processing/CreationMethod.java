package com.moon.data.jdbc.processing;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.moon.data.jdbc.processing.StringUtils.nextLine;

/**
 * @author benshaoye
 */
final class CreationMethod {

    private final ExecutableElement method;

    public CreationMethod(ExecutableElement method) { this.method = method; }

    public String toString(int indent, Importer importer) {
        String space = StringUtils.indent(indent);
        String methodName = StringUtils.getSimpleName(method);
        TypeMirror returnType = method.getReturnType();
        StringBuilder builder = new StringBuilder();
        nextLine(builder.append(space).append("@Override"));
        String type = importer.onImported(returnType);
        String declare = "public {type} {name}({params}) {";
        Map<String, String> parameters = toDeclareParameters(importer, method);
        Replacer.Group group = Replacer.of(Replacer.type, Replacer.name, Replacer.params);
        builder.append(space).append(group.replace(declare, type, methodName, toString(parameters)));
        // TODO impl
        toDefaultReturnScript(builder, indent, returnType);
        return builder.toString();
    }

    private static LinkedHashMap<String, String> toDeclareParameters(Importer importer, ExecutableElement elem) {
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
        List<? extends VariableElement> vars = elem.getParameters();
        if (vars == null || vars.isEmpty()) {
            return parameters;
        }
        for (VariableElement var : vars) {
            String name = StringUtils.getSimpleName(var);
            String type = importer.onImported(var.asType());
            parameters.put(name, type);
        }
        return parameters;
    }

    private static String toString(Map<String, String> parameters) {
        if (parameters.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (index++ > 0) {
                builder.append(", ");
            }
            builder.append(entry.getValue());
            builder.append(" ").append(entry.getKey());
        }
        return builder.toString();
    }

    private static void toDefaultReturnScript(StringBuilder builder, int indent, TypeMirror returnType) {
        String space = StringUtils.indent(indent);
        TypeKind kind = returnType.getKind();
        if (kind == TypeKind.VOID) {
            builder.append(" }");
            return;
        }
        if (kind == TypeKind.BOOLEAN) {
            toReturnScript(builder, space, "false");
        } else if (kind.isPrimitive()) {
            toReturnScript(builder, space, "0");
        } else {
            toReturnScript(builder, space, null);
        }
    }

    private static void toReturnScript(StringBuilder builder, String space, String value) {
        StringUtils.newLine(builder);
        builder.append(space).append(space).append("return ").append(value).append(";");
        StringUtils.newLine(builder);
        builder.append(space).append("}");
    }
}
