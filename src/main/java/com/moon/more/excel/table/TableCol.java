package com.moon.more.excel.table;

import com.moon.more.excel.CellFactory;
import com.moon.more.excel.PropertyControl;

import java.util.List;

/**
 * @author benshaoye
 */
class TableCol implements Comparable<TableCol> {

    private final String[] titles;
    private final int titlesCount;
    private final int order;
    private final PropertyControl control;
    private final Transformer transform;

    TableCol(Attribute attr) {
        this.transform = attr.getTransformOrDefault();
        this.order = attr.getOrder();
        this.titlesCount = attr.getTitles().length;
        this.control = attr.getValueGetter();
        this.titles = attr.getTitles();
    }

    protected PropertyControl getControl() { return control; }

    protected Transformer getTransform() { return transform; }

    void appendTitlesAtRowIdx(List<String> rowTitles, int rowIdx) {
        rowTitles.add(getEnsureTitleAtIdx(rowIdx));
    }

    /**
     * 为了用计算的方式获取指定行的标题
     * <p>
     * ensure： 确保，一定会返回非空值，不足的自动用最后一个标题补上
     *
     * @param rowIdx
     *
     * @return
     */
    private String getEnsureTitleAtIdx(int rowIdx) {
        int index = rowIdx < titlesCount ? rowIdx : titlesCount - 1;
        if (index > -1 && index < getHeaderRowsCount()) {
            return getTitles()[index];
        }
        return null;
    }

    /**
     * 这里单独写成一个获取 length 的方法是为了后面支持 ColumnGroup 时用计算的方式获取最大长度
     *
     * @return
     */
    int getHeaderRowsCount() { return getTitles().length; }

    String[] getTitles() { return titles; }

    void render(CellFactory factory, Object data) {
        transform.doTransform(factory, control.control(data));
    }

    @Override
    public int compareTo(TableCol o) {
        return this.order - o.order;
    }
}
