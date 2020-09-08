package com.moon.data.web;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.logger.Logger;
import com.moon.core.util.logger.LoggerUtil;
import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;
import com.moon.data.accessor.DataAccessor;
import com.moon.data.accessor.DataAccessorImpl;
import com.moon.data.registry.LayerEnum;
import com.moon.data.registry.RecordDuplicateRegistryException;
import com.moon.data.registry.RecordRegistry;
import com.moon.data.service.DataService;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public abstract class DataController<T extends Record<ID>, ID> extends DataAccessorImpl<T, ID> {

    private final static Logger logger = LoggerUtil.getLogger();

    protected DataController() { this(null); }

    protected DataController(Class<? extends BaseAccessor<T, ID>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected DataController(Class<? extends BaseAccessor<T, ID>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @PostConstruct
    public void postConstruct() {
        Class domainClass = this.getDomainClass();
        if (domainClass != null) {
            try {
                registryVo2Entity(domainClass);
            } catch (RecordDuplicateRegistryException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(StringUtil.format("已取消重复注册实例: {}", domainClass));
                }
            }
        }
    }

    @Override
    protected DataAccessor<T, ID> provideDefaultAccessor() { return getService(); }

    @Override
    protected LayerEnum provideThisLayer() { return LayerEnum.CONTROLLER; }

    @Override
    protected LayerEnum pullingAccessLayer() { return LayerEnum.SERVICE; }

    /**
     * 目标服务
     *
     * @return
     */
    protected DataService<T, ID> getService() {
        BaseAccessor accessor = getAccessor();
        if (accessor instanceof DataService) {
            return (DataService) accessor;
        }
        return null;
    }


    /* registry -------------------------------------------------------- */

    protected final <T> void registryVo2Entity(Class<T> type) {
        registryVo2Entity(type, () -> {
            try {
                return type.newInstance();
            } catch (Exception e) {
                throw new RecordDowngradeBuildException("不能创建实例：" + type);
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
        RecordRegistry.registry(type, id -> {
            if (id == null) {
                return defaultEntitySupplier.get();
            } else if (id instanceof CharSequence && StringUtil.isEmpty((CharSequence) id)) {
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
        return RecordRegistry.getByRegistered(type, id);
    }
}
