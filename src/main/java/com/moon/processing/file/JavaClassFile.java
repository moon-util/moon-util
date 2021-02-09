package com.moon.processing.file;

/**
 * @author benshaoye
 */
public class JavaClassFile extends JavaInterfaceFile {

    public JavaClassFile(String packageName, String simpleName) {
        super(packageName, simpleName);
    }

    @Override
    public String toString() {
        JavaAddr addr = new JavaAddr();
        return addr.toString();
    }
}
