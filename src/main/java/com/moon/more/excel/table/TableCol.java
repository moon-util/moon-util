package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.PropertyControl;
import com.moon.more.excel.RowFactory;

import java.util.List;

/**
 * @author benshaoye
 */
class TableCol implements Comparable<TableCol> {

    private final String[] titles;
    private final int offset;
    private final int order;
    private final PropertyControl control;
    private final Transformer transform;

    TableCol(AttrConfig config) {
        Attribute attr = config.getAttribute();
        this.transform = attr.getTransformOrDefault();
        this.control = attr.getValueGetter();
        this.titles = attr.getTitles();
        this.offset = attr.getOffset();
        this.order = attr.getOrder();
    }

    protected PropertyControl getControl() { return control; }

    protected Transformer getTransform() { return transform; }

    final void appendTitles4Offset(List<String> rowTitles, int rowIdx) {
        String thisTitle = rowIdx + 1 < getHeaderRowsCount() ? getEnsureTitleAtIdx(rowIdx) : null;
        for (int i = 0; i < offset; i++) {
            rowTitles.add(thisTitle);
        }
    }

    void appendTitlesAtRowIdx(List<String> rowTitles, int rowIdx) {
        appendTitles4Offset(rowTitles, rowIdx);
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
    final String getEnsureTitleAtIdx(int rowIdx) {
        int titlesCount = getHeaderRowsCount();
        int index = rowIdx < titlesCount ? rowIdx : titlesCount - 1;
        if (index > -1 && index < titlesCount) {
            return getTitles()[index];
        }
        // TODO 注解不能使用 null 值，null 值以后可用于特殊用途，比如偏移
        return null;
    }

    /**
     * 这里单独写成一个获取 length 的方法是为了后面支持 ColumnGroup 时用计算的方式获取最大长度
     *
     * @return
     */
    int getHeaderRowsCount() { return getTitles().length; }

    private String[] getTitles() { return titles; }

    final CellFactory toCellFactory(RowFactory rowFactory, IntAccessor indexer) {
        indexer.increment(offset);
        return rowFactory.index(indexer.getAndIncrement());
    }

    private final void skip(IntAccessor indexer) {
        indexer.increment(offset + 1);
    }

    void render(IntAccessor indexer, RowFactory factory, Object data) {
        if (data == null) {
            CellFactory cellFactory = toCellFactory(factory, indexer);
            transform.doTransform(cellFactory, control.control(data));
        } else {
            skip(indexer);
        }
    }

    @Override
    public final int compareTo(TableCol o) { return this.order - o.order; }
}
