package com.moon.more.data.registry;


import com.moon.spring.data.DataAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public enum LayerEnum implements Function<Class, DataAccessor> {
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

    private final Map<Class, DataAccessor> cached = new HashMap<>();

    LayerEnum() {}

    public void registry(Class domainClass, DataAccessor accessor){
        cached.put(domainClass, accessor);
    }

    public DataAccessor get(Class domainClass){ return apply(domainClass); }

    @Override
    public DataAccessor apply(Class domainClass){ return cached.get(domainClass); }
}
