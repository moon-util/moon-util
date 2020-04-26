package com.moon.more.data.registry;


import com.moon.spring.data.RecordAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public enum LayerEnum implements Function<Class, RecordAccessor> {
    // dao
    REPOSITORY,
    MAPPER,

    // service
    SERVICE,

    // controller
    CONTROLLER,

    // other
    SUPPLIER,
    ;

    private final Map<Class, RecordAccessor> cached = new HashMap<>();

    LayerEnum() {}

    public void registry(Class domainClass, RecordAccessor accessor){
        cached.put(domainClass, accessor);
    }

    public RecordAccessor get(Class domainClass){ return apply(domainClass); }

    @Override
    public RecordAccessor apply(Class domainClass){ return cached.get(domainClass); }
}
