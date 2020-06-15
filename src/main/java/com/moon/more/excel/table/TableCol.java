package com.moon.more.excel.table;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.PropertyControl;
import com.moon.more.excel.RowFactory;

import java.util.List;

/**
 * @author benshaoye
 */
class TableCol implements Comparable<TableCol> {

    final static short DEFAULT_HEIGHT = -1;

    private final String name;
    private final String[] titles;
    private final short[] rowsHeight4Head;
    private final boolean offsetAll;
    private final int offset;
    private final int order;
    private final PropertyControl control;
    private final GetTransformer transform;

    TableCol(AttrConfig config) {
        Attribute attr = config.getAttribute();
        this.transform = attr.getTransformOrDefault();
        this.control = attr.getValueGetter();
        this.titles = attr.getTitles();
        this.rowsHeight4Head = attr.getHeadHeightArr();
        this.offsetAll = attr.getOffsetAll();
        this.offset = attr.getOffset();
        this.order = attr.getOrder();
        this.name = attr.getName();
    }

    protected PropertyControl getControl() { return control; }

    protected GetTransformer getTransform() { return transform; }

    final void appendTitles4Offset(List<HeadCell> rowTitles, int rowIdx) {
        HeadCell thisCell = null;
        if (!offsetAll && rowIdx + 1 < getHeaderRowsCount()) {
            thisCell = getEnsureTitleAtIdx(rowIdx);
        }
        for (int i = 0; i < offset; i++) {
            rowTitles.add(thisCell);
        }
    }

    void appendTitlesAtRowIdx(List<HeadCell> rowTitles, int rowIdx) {
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
    private final HeadCell getEnsureTitleAtIdx(int rowIdx) {
        // 表头标题
        int length = getHeaderRowsLength();
        int index = rowIdx < length ? rowIdx : length - 1;
        String title = index > -1 && index < length ? getTitles()[index] : null;
        // 表头行高
        short[] values = this.rowsHeight4Head;
        int count = values.length;
        int heightIdx = rowIdx < count ? rowIdx : count - 1;
        short height = heightIdx > -1 && heightIdx < count ? values[heightIdx] : DEFAULT_HEIGHT;
        // 注解不能使用 null 值，故这里将null 值用于特殊用途，比如计算偏移量
        return title != null || height > -1 ? new HeadCell(title, height) : null;
    }

    final int getHeaderRowsLength() { return getTitles().length; }

    /**
     * 这里单独写成一个获取 length 的方法是为了后面支持 ColumnGroup 时用计算的方式获取最大长度
     *
     * @return
     */
    int getHeaderRowsCount() { return getHeaderRowsLength(); }

    private final String[] getTitles() { return titles; }

    final CellFactory toCellFactory(RowFactory rowFactory, IntAccessor indexer) {
        indexer.increment(offset);
        return rowFactory.index(indexer.getAndIncrement());
    }

    private final void skip(IntAccessor indexer) { indexer.increment(offset + 1); }

    void render(IntAccessor indexer, RowFactory factory, Object data) {
        if (data == null) {
            skip(indexer);
        } else {
            CellFactory cellFactory = toCellFactory(factory, indexer);
            transform.doTransform(cellFactory, control.control(data));
        }
    }

    @Override
    public final int compareTo(TableCol o) { return this.order - o.order; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TableCol: ");
        sb.append(name).append("; Titles: ");
        sb.append(StringUtil.defaultIfEmpty(JoinerUtil.join(getTitles()), "<空>"));
        return sb.toString();
    }
}
