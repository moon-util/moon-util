package com.moon.more.excel.table;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.annotation.FieldTransformer;

/**
 * @author benshaoye
 */
final class TableColTransformCached extends TableColTransform {

    private final FieldTransformer colTransformer;

    TableColTransformCached(
        AttrConfig config, Class<FieldTransformer> transformerCls
    ) {
        super(config, transformerCls);
        this.colTransformer = ClassUtil.newInstance(transformerCls);
    }

    @Override
    void render(IntAccessor indexer, RowFactory factory, Object data) {
        // TODO 暂不支持数据为 null 的情况
        Object value = getControl().control(data);
        Object cellVal = colTransformer.transform(value);
        getTransform().doTransform(toCellFactory(factory, indexer), cellVal);
    }
}
