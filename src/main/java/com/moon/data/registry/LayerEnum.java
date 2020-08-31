package com.moon.data.registry;


import com.moon.data.BaseAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author moonsky
 */
public enum LayerEnum implements Function<Class, BaseAccessor> {
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

    private final Map<Class, BaseAccessor> cached = new HashMap<>();

    LayerEnum() {}

    public void registry(Class domainClass, BaseAccessor accessor){ cached.put(domainClass, accessor); }

    public BaseAccessor get(Class domainClass){ return apply(domainClass); }

    @Override
    public BaseAccessor apply(Class domainClass){ return cached.get(domainClass); }
}
