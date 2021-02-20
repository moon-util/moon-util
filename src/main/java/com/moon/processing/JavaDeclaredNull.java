package com.moon.processing;

/**
 * @author benshaoye
 */
public enum JavaDeclaredNull implements JavaDeclarable {
    /** NULL */
    NULL;

    @Override
    public String getClassname() { return null; }

    @Override
    public String getJavaContent() { return null; }
}
