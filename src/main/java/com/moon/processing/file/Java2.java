package com.moon.processing.file;

/**
 * @author benshaoye
 */
public enum Java2 {
    ;

    public static FileClassImpl classOf(String packageName, String simpleName) {
        return new FileClassImpl(packageName, simpleName);
    }

    public static FileEnumImpl enumOf(String packageName, String simpleName) {
        return new FileEnumImpl(packageName, simpleName);
    }

    public static FileInterfaceImpl interfaceOf(String packageName, String simpleName) {
        return new FileInterfaceImpl(packageName, simpleName);
    }

    public static FileClassImpl classOf(String classname) {
        String[] spliced = spliced(classname);
        return classOf(spliced[0], spliced[1]);
    }

    public static FileEnumImpl enumOf(String classname) {
        String[] spliced = spliced(classname);
        return enumOf(spliced[0], spliced[1]);
    }

    public static FileInterfaceImpl interfaceOf(String classname) {
        String[] spliced = spliced(classname);
        return interfaceOf(spliced[0], spliced[1]);
    }

    private static String[] spliced(String classname) {
        int index = classname.lastIndexOf('.');
        if (index < 0) {
            return new String[]{"", classname};
        }
        String pkg = classname.substring(0, index);
        String name = classname.substring(index + 1);
        return new String[]{pkg, name};
    }
}
