package com.moon.mapping.processing;

/**
 * @author moonsky
 */
enum ModeStrategy {
    DECLARE {
        @Override
        public String getSetterType(PropertyModel model) { return model.getSetterDeclareType(); }

        @Override
        public String getGetterType(PropertyModel model) { return model.getGetterDeclareType(); }
    },
    FINALLY {
        @Override
        public String getSetterType(PropertyModel model) { return model.getSetterFinalType(); }

        @Override
        public String getGetterType(PropertyModel model) { return model.getGetterFinalType(); }
    };

    public abstract String getSetterType(PropertyModel model);

    public abstract String getGetterType(PropertyModel model);
}
