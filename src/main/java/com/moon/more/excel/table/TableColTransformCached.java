package com.moon.more.excel.table;

import com.moon.core.lang.ClassUtil;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.annotation.TableColumnTransformer;

/**
 * @author benshaoye
 */
final class TableColTransformCached extends TableColTransform {

    private final TableColumnTransformer colTransformer;

    TableColTransformCached(
        Attribute attr, Class<TableColumnTransformer> transformerCls
    ) {
        super(attr, transformerCls);
        this.colTransformer = ClassUtil.newInstance(transformerCls);
    }

    @Override
    void render(CellFactory factory, Object data) {
        Object value = getControl().control(data);
        Object cellVal = colTransformer.transform(value);
        getTransform().doTransform(factory, cellVal);
    }
}
