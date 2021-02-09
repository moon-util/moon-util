package com.moon.processing.file;

/**
 * @author benshaoye
 */
public enum JavaFile2 {
    ;

    public static JavaClassFile classOf(String packageName, String simpleName) {
        return new JavaClassFile(packageName, simpleName);
    }

    public static JavaEnumFile enumOf(String packageName, String simpleName) {
        return new JavaEnumFile(packageName, simpleName);
    }

    public static JavaInterfaceFile interfaceOf(String packageName, String simpleName) {
        return new JavaInterfaceFile(packageName, simpleName);
    }

    public static JavaClassFile classOf(String classname) {
        String[] spliced = spliced(classname);
        return classOf(spliced[0], spliced[1]);
    }

    public static JavaEnumFile enumOf(String classname) {
        String[] spliced = spliced(classname);
        return enumOf(spliced[0], spliced[1]);
    }

    public static JavaInterfaceFile interfaceOf(String classname) {
        String[] spliced = spliced(classname);
        return interfaceOf(spliced[0], spliced[1]);
    }

    private static String[] spliced(String classname) {
        int index = classname.lastIndexOf('.');
        if (index < 0) {
            return new String[]{"", classname};
        }
        String pkg = classname.substring(index);
        String name = classname.substring(index + 1);
        return new String[]{pkg, name};
    }
}
