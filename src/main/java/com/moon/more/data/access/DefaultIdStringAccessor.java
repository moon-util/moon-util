package com.moon.more.data.access;

import com.moon.more.data.registry.LayerEnum;
import com.moon.more.model.id.StringIdSupplier;

/**
 * @author benshaoye
 */
public abstract class DefaultIdStringAccessor<T extends StringIdSupplier> extends DefaultAccessor<String, T>
    implements BaseIdStringAccessor<T> {

    protected DefaultIdStringAccessor(LayerEnum accessorLayer, Class rawClass) { super(accessorLayer, rawClass); }

    protected DefaultIdStringAccessor(Class serviceBeanType, LayerEnum accessorLayer, Class rawClass) {
        super(serviceBeanType, accessorLayer, rawClass);
    }

    protected DefaultIdStringAccessor(LayerEnum accessorLayer) { super(accessorLayer); }

    protected DefaultIdStringAccessor(Class serviceBeanType, LayerEnum accessorLayer) {
        super(serviceBeanType, accessorLayer);
    }
}
