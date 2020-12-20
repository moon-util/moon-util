package com.moon.data.jdbc.processing;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.moon.data.jdbc.processing.StringUtils.nextLine;

/**
 * @author benshaoye
 */
final class MethodModel {

    private final ExecutableElement method;

    public MethodModel(ExecutableElement method) { this.method = method; }

    public String toString(int indent, Importer importer) {
        String space = StringUtils.indent(indent);
        String methodName = StringUtils.getSimpleName(method);
        TypeMirror returnType = method.getReturnType();
        StringBuilder builder = new StringBuilder();
        nextLine(builder.append(space).append("@Override"));
        String type = importer.onImported(returnType);
        String params = toParameters(importer, method);
        String declare = "public {type} {name}({params}) {";
        Replacer.Group group = Replacer.of(Replacer.type, Replacer.name, Replacer.params);
        builder.append(space).append(group.replace(declare, type, methodName, params));
        toDefaultReturnScript(builder, indent, returnType);
        return builder.toString();
    }

    private static String toParameters(Importer importer, ExecutableElement elem) {
        List<? extends VariableElement> vars = elem.getParameters();
        if (vars == null || vars.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < vars.size(); i++) {
            VariableElement var = vars.get(i);
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(importer.onImported(var.asType()));
            builder.append(" ").append(var.getSimpleName());
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
