package com.moon.spring.jpa.web;

import com.moon.core.lang.StringUtil;
import com.moon.more.data.registry.EntityRegistry;
import com.moon.more.data.registry.EntityRegistryException;
import com.moon.more.data.registry.LayerEnum;
import com.moon.spring.data.BaseDataAccessor;
import com.moon.spring.data.DataAccessor;
import com.moon.spring.jpa.JpaRecordable;
import com.moon.spring.jpa.service.DataService;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public class DataController<T extends JpaRecordable<String>> extends BaseDataAccessor<String, T> {


    protected DataController(Class<? extends DataService> classname) { super(classname, LayerEnum.SERVICE); }

    protected DataController() { this(null); }

    private static void debug(Type type, Class clazz) {
        // if (log.isDebugEnabled()) {
        //     Object tName = type instanceof Class ? ((Class) type).getSimpleName() : type;
        //     log.debug("来自：{}，重复注册：{}", tName, clazz);
        // }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Class domainClass = this.domainClass;
        if (domainClass != null) {
            try {
                registryVo2Entity(domainClass);
            } catch (EntityRegistryException e) {
                if (rawClass == getClass()) {
                    debug(rawClass, domainClass);
                    return;
                } else if (rawClass == DataController.class) {
                    Type supertype = rawClass.getGenericSuperclass();
                    if (supertype instanceof ParameterizedType) {
                        // ParameterizedType type = (ParameterizedType) supertype;
                        // if (log.isDebugEnabled()) {
                        //     debug(type.getRawType(), domainClass);
                        // }
                    }
                    return;
                }
                debug(getClass(), domainClass);
                // ignore
            }
        }
    }

    @Override
    protected DataAccessor<String, T> getDefaultAccessor() { return getService(); }

    /**
     * 目标服务
     *
     * @return
     */
    protected DataService<T> getService() { return null; }


    /* registry -------------------------------------------------------- */

    protected final <T> void registryVo2Entity(Class<T> type) {
        registryVo2Entity(type, () -> {
            try {
                return type.newInstance();
            } catch (Exception e) {
                throw new BaseSettingsException("不能创建实例：" + type);
            }
        });
    }

    protected final <T> void registryVo2Entity(Supplier<T> defaultValueSupplier) {
        registryVo2Entity(domainClass, defaultValueSupplier);
    }

    protected final <T> void registryVo2Entity(Class<T> type, Supplier<T> defaultValueSupplier) {
        registryVo2Entity(type, defaultValueSupplier, this::getService);
    }

    protected final <T> void registryVo2Entity(
        Class<T> type, Supplier<T> defaultEntitySupplier, Supplier<? extends DataService> serviceSupplier
    ) {
        EntityRegistry.registry(type, id -> {
            if (StringUtil.isEmpty(id)) {
                return defaultEntitySupplier.get();
            } else {
                Optional optional = serviceSupplier.get().findById(id);
                return optional.orElseGet(defaultEntitySupplier);
            }
        });
    }

    protected final T getOrNewEntityById(String id) { return getByRegistered(id); }

    protected final <E> E getByRegistered(String id) { return (E) getByRegistered(domainClass, id); }

    protected static final <E> E getByRegistered(Class<E> type, String id) {
        return EntityRegistry.getByRegistered(type, id);
    }
}
