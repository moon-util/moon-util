package com.moon.mapping.processing;

import java.util.Collection;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum ConvertStrategy {
    /**
     * 正向映射
     */
    FORWARD {
        @Override
        public Map<String, Map<String, PropertyAttr>> getMethodAttrMap(BaseDefinition definition) {
            return definition.getGetterAttrMap();
        }

        @Override
        public String forMethod(
            MapMethodFactory factory, String thisClass, String thatClass, Collection<String> fields
        ) { return factory.unsafeForward(thisClass, thatClass, fields); }

        @Override
        public boolean isIgnore(PropertyAttr attr) { return attr.isIgnoreForward(); }
    },
    /**
     * 反向映射
     */
    BACKWARD {
        @Override
        public Map<String, Map<String, PropertyAttr>> getMethodAttrMap(BaseDefinition definition) {
            return definition.getSetterAttrMap();
        }

        @Override
        public String forMethod(
            MapMethodFactory factory, String thisClass, String thatClass, Collection<String> fields
        ) { return factory.unsafeBackward(thisClass, thatClass, fields); }

        @Override
        public boolean isIgnore(PropertyAttr attr) { return attr.isIgnoreBackward(); }
    };

    public abstract Map<String, Map<String, PropertyAttr>> getMethodAttrMap(BaseDefinition definition);

    public abstract String forMethod(
        MapMethodFactory factory, String thisClass, String thatClass, Collection<String> fields
    );

    public abstract boolean isIgnore(PropertyAttr attr);
}
