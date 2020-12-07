package com.moon.data.registry;


import com.moon.core.enums.Level;
import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
public enum LayerEnum implements Function<Class<?>, BaseAccessor<? extends Record<?>, ?>> {
    // dao 数据库层
    REPOSITORY(Level.LOWEST),
    MAPPER(Level.LOWEST),
    DAO(Level.LOWEST),

    // synchronizer 同步器
    SYNCHRONIZER(Level.L1),

    // manager 管理
    MANAGER(Level.L4),

    // service 业务服务
    SERVICE(Level.L7),

    // controller web 接口
    CONTROLLER(Level.HIGHEST),
    ;

    private final Level level;

    private final Map<Class<?>, BaseAccessor<?, ?>> cached = new HashMap<>();

    private final static class Values {

        private final static LayerEnum[] ALL = LayerEnum.values();

        static {
            Arrays.sort(ALL, Comparator.comparing(LayerEnum::getLevel).thenComparing(o -> o));
        }
    }

    LayerEnum(Level level) { this.level = level; }

    public <ID, T extends Record<ID>> void registry(Class<T> domainClass, BaseAccessor<T, ID> accessor) {
        cached.put(domainClass, accessor);
    }

    Level getLevel() { return level; }

    public <ID, T extends Record<ID>> BaseAccessor<T, ID> get(Class<T> domainClass) { return apply(domainClass); }

    @Override
    public BaseAccessor apply(Class<?> domainClass) { return cached.get(domainClass); }

    static void forEachReversed(Predicate<LayerEnum> consumer) {
        final LayerEnum[] layers = Values.ALL;
        for (int i = layers.length - 1; i > -1; i--) {
            if (!consumer.test(layers[i])) {
                return;
            }
        }
    }
}
