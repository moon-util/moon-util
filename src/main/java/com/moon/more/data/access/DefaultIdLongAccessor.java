package com.moon.more.data.access;

import com.moon.more.data.registry.LayerEnum;
import com.moon.more.model.id.LongIdSupplier;

/**
 * @author benshaoye
 */
public abstract class DefaultIdLongAccessor<T extends LongIdSupplier> extends DefaultAccessor<Long, T>
    implements BaseIdLongAccessor<T> {

    protected DefaultIdLongAccessor(LayerEnum accessorLayer, Class rawClass) { super(accessorLayer, rawClass); }

    protected DefaultIdLongAccessor(Class serviceBeanType, LayerEnum accessorLayer, Class rawClass) {
        super(serviceBeanType, accessorLayer, rawClass);
    }

    protected DefaultIdLongAccessor(LayerEnum accessorLayer) { super(accessorLayer); }

    protected DefaultIdLongAccessor(Class serviceBeanType, LayerEnum accessorLayer) {
        super(serviceBeanType, accessorLayer);
    }
}
