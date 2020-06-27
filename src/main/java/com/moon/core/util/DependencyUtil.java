package com.moon.core.util;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;

/**
 * @author benshaoye
 */
public final class DependencyUtil {

    private final static String TEMPLATE = "" +
        "{indentAll}{indent}<dependency>\n" +
        "{indentAll}{indent}{indent}<groupId>%s</groupId>\n" +
        "{indentAll}{indent}{indent}<artifactId>%s</artifactId>\n" +
        "{indentAll}{indent}{indent}<version>%s</version>\n" +
        "{indentAll}{indent}</dependency>";


    private DependencyUtil() { ThrowUtil.noInstanceError(); }

    public static String dependencyXmlNode(String dependency) {
        return dependencyXmlNode(dependency, 4);
    }

    public static String dependencyXmlNode(String dependency, int indent) {
        return dependencyXmlNode(dependency, indent, 0);
    }

    public static String dependencyXmlNode(String dependency, int indent, int indentAll) {
        String[] parts = dependency.split(":");
        int length = parts.length;
        String groupId, artifactId, version = parts.length > 2 ? parts[2] : null;
        if (length > 0) {
            groupId = parts[0];
        } else {
            throw new IllegalArgumentException("Unknown 'groupId' from: " + dependency);
        }
        if (length > 1) {
            artifactId = parts[1];
        } else {
            throw new IllegalArgumentException("Unknown 'artifactId' from: " + dependency);
        }
        return dependencyXmlNode(groupId, artifactId, version, indent, indentAll);
    }

    public static String dependencyXmlNode(
        String groupId, String artifactId, String version, int indent
    ) { return dependencyXmlNode(groupId, artifactId, version, indent, indent); }

    public static String dependencyXmlNode(
        String groupId, String artifactId, String version, int indent, int indentAll
    ) {
        version = StringUtil.defaultIfEmpty(version, " version? ");
        return String.format(TEMPLATE, groupId, artifactId, version)
            .replaceAll("\\{indentAll\\}", StringUtil.repeat(" ", indentAll))
            .replaceAll("\\{indent\\}", StringUtil.repeat(" ", indent));
    }
}
