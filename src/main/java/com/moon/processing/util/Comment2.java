package com.moon.processing.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
public enum Comment2 {
    ;

    private final static String AT_COMMENT = "@comment ";

    public static String resolveFirstComment(Element elem) {
        String comment = getComment(getFirstComment(elem), elem, true);
        return String2.isBlank(comment) ? null : comment;
    }

    public static String resolveComment(Element elem) {
        String comment = getComment(getSimpleComment(elem), elem, false);
        return String2.isBlank(comment) ? null : comment;
    }

    private static String getComment(String comment, Element elem, boolean first) {
        switch (elem.getKind()) {
            case FIELD: {
                comment = resolveEnum(comment, elem.asType(), first);
                break;
            }
            case METHOD: {
                ExecutableElement method = (ExecutableElement) elem;
                comment = resolveEnum(comment, method.getReturnType(), first);
                break;
            }
            default:
        }
        return com.moon.accessor.util.String2.withEscaped(comment, '"');
    }

    private static String resolveEnum(String baseComment, TypeMirror elemTyped, boolean first) {
        Element fieldType = Processing2.getTypes().asElement(elemTyped);
        if (Test2.isEnum(fieldType)) {
            return resolveEnumComments(baseComment, (TypeElement) fieldType, first);
        }
        return baseComment;
    }

    public static String getFirstComment(Element elem) {
        Elements utils = Processing2.getUtils();
        String docComment = utils.getDocComment(elem);
        if (String2.isBlank(docComment)) {
            return "";
        }
        String defaultComment = null;
        String[] comments = docComment.split("\n");
        for (String comment : comments) {
            String line = comment.trim();
            if (String2.isNotBlank(line) && defaultComment == null && isNotTagComment(line)) {
                defaultComment = line;
            }
            if (line.startsWith(AT_COMMENT)) {
                return line.substring(AT_COMMENT.length());
            }
        }
        return defaultComment == null ? "" : defaultComment;
    }

    public static String getSimpleComment(Element elem) {
        Elements utils = Processing2.getUtils();
        String docComment = utils.getDocComment(elem);
        if (String2.isBlank(docComment)) {
            return "";
        }
        String[] comments = docComment.split("\n");

        int defaultCmtNextIdx = 0, commentNextIdx = -1;
        final StringBuilder defaultBuilder = new StringBuilder();
        final StringBuilder commentBuilder = new StringBuilder();
        for (int i = 0; i < comments.length; i++) {
            String line = comments[i].trim();
            if (i == defaultCmtNextIdx) {
                if (isNotTagComment(line)) {
                    defaultBuilder.append(line);
                    defaultCmtNextIdx++;
                }
            }
            if (i == commentNextIdx) {
                if (isNotTagComment(line)) {
                    commentBuilder.append(line);
                    commentNextIdx++;
                }
            }
            if (line.startsWith(AT_COMMENT)) {
                commentBuilder.append(line.substring(AT_COMMENT.length()));
                commentNextIdx = i + 1;
            }
        }
        String defaultVal = defaultBuilder.toString().trim();
        String commentVal = commentBuilder.toString().trim();

        return commentVal.isEmpty() ? defaultVal : commentVal;
    }

    private static String resolveEnumComments(String fieldComment, TypeElement elem, boolean first) {
        List<Element> enums = Element2.getEnums(elem);
        List<String> comments = new ArrayList<>(enums.size());
        for (Element anEnum : enums) {
            String enumName = anEnum.getSimpleName().toString();
            if (fieldComment.contains(enumName)) {
                return fieldComment;
            }
            String comment = first ? getFirstComment(anEnum) : getSimpleComment(anEnum);
            if (String2.isBlank(comment)) {
                comments.add(enumName);
            } else {
                comments.add(String2.format("{}: '{}'", enumName, comment));
            }
        }
        StringBuilder builder;
        if (String2.isBlank(fieldComment)) {
            builder = new StringBuilder().append('{');
        } else {
            builder = new StringBuilder(fieldComment).append("; {");
        }
        return builder.append(String.join(", ", comments)).append('}').toString();
    }

    private static boolean isNotTagComment(String line) {
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (Character.isWhitespace(character)) {
                continue;
            }
            return character != '@';
        }
        return true;
    }
}