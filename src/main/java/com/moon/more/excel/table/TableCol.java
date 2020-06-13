package com.moon.more.excel.table;

import com.moon.core.lang.ObjectUtil;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.PropertyControl;

/**
 * @author benshaoye
 */
class TableCol {

    private final String[] titles;
    private final int titlesCount;
    private final PropertyControl control;
    private final Transformer transform;

    TableCol(Attribute attr) {
        this.transform = ObjectUtil.defaultIfNull(attr.getTransformForGet(), TransformForGet.DEFAULT);
        this.titlesCount = attr.getTitles().length;
        this.control = attr.getValueGetter();
        this.titles = attr.getTitles();
    }

    protected PropertyControl getControl() { return control; }

    protected Transformer getTransform() { return transform; }

    private String indexOfTitle(int idx) {
        return idx < titlesCount ? getTitles()[idx] : null;
    }

    public String[] getTitles() { return titles; }

    void renderHead(CellFactory factory, int rowIdx) {
        factory.val(indexOfTitle(rowIdx));
    }

    void render(CellFactory factory, Object data) {
        transform.doTransform(factory, control.control(data));
    }
}
