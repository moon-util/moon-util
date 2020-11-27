package com.moon.mapping.processing;

/**
 * @author moonsky
 */
enum PropertyType {
    DECLARE {
        @Override
        public String getSetterType(MappingModel model) { return model.getSetterDeclareType(); }

        @Override
        public String getGetterType(MappingModel model) { return model.getGetterDeclareType(); }
    },
    FINALLY {
        @Override
        public String getSetterType(MappingModel model) { return model.getSetterFinalType(); }

        @Override
        public String getGetterType(MappingModel model) { return model.getGetterFinalType(); }
    };

    public abstract String getSetterType(MappingModel model);

    public abstract String getGetterType(MappingModel model);
}
