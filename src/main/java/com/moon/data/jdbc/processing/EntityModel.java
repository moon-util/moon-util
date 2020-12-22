package com.moon.data.jdbc.processing;

/**
 * @author benshaoye
 */
final class EntityModel {

    private final boolean useStrict;

    public EntityModel(boolean useStrict) {
        this.useStrict = useStrict;
    }

    public boolean isUseStrict() { return useStrict; }
}
