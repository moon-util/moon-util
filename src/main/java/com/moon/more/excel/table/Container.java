package com.moon.more.excel.table;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
final class Container {

    private final Map<String, Marked> annotated = new LinkedHashMap<>();
    private final Map<String, Marked> unAnnotated = new LinkedHashMap<>();
    private final Map<String, Object> style = new HashMap<>();

    public Container() {
    }

    void addStyle(){

    }

    void putMarked(Marked marked){
        Map group = marked.isAnnotated() ? annotated : unAnnotated;
        group.put(marked.getName(), marked);
    }
}
