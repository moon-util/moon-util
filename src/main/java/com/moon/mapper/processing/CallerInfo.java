package com.moon.mapper.processing;

import java.util.List;

import static com.moon.mapper.processing.StringUtils.bracketed;

/**
 * @author moonsky
 */
final class CallerInfo {

    private final ImportManager importManager;
    /**
     * 类全名
     */
    private final String caller;
    /**
     * 方法名
     */
    private final String method;

    private final String[] parametersType;

    public CallerInfo(
        ImportManager importManager, String caller, String method, List<String> parametersType
    ) {
        this.parametersType = parametersType.toArray(new String[parametersType.size()]);
        this.importManager = importManager;
        this.caller = caller;
        this.method = method;
    }

    private String getCallerMethod() {
        String clsName = importManager.onImported(this.caller);
        return String.join(".", clsName, this.method);
    }

    private String getVarName(int index) {
        return index > 0 ? ("{var" + (index) + "}") : "{var}";
    }

    public String toString(String... varsName) {
        String[] vars = new String[parametersType.length];
        final int l1 = vars.length, l2 = varsName.length;
        if (l2 < l1) {
            int index = 0;
            for (; index < l2; index++) {
                String name = varsName[index];
                vars[index] = name == null ? getVarName(index) : name;
            }
            for (; index < l1; index++) {
                vars[index] = getVarName(index);
            }
        } else {
            for (int i = 0; i < l1; i++) {
                String name = varsName[i];
                vars[i] = name == null ? getVarName(i) : name;
            }
        }
        String args = bracketed(String.join(",", vars));
        return getCallerMethod() + args;
    }

    @Override
    public String toString() {
        String[] params = this.parametersType;
        String[] vars = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            vars[i] = getVarName(i);
        }
        String args = bracketed(String.join(",", vars));
        return getCallerMethod() + args;
    }
}
