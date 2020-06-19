package com.moon.more.excel.table;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.PropertyControl;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.annotation.StyleBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
class TableCol implements Comparable<TableCol> {

    private final static int MAX = Integer.MAX_VALUE / 2;

    final static short DEFAULT_HEIGHT = -1;

    private final String name;
    private final String defaultClassname;
    private final String[] titles;
    private final short[] rowsHeight4Head;
    private final int offsetHeadRowsCnt;
    // 列宽，如果当前是实体组合列，则为 null
    private final Integer width;
    private final int offset;
    private final int order;
    private final PropertyControl control;
    private final GetTransformer transform;
    private final boolean fillSkipped;

    TableCol(AttrConfig config) {
        Attribute attr = config.getAttribute();
        this.transform = attr.getTransformOrDefault();
        this.control = attr.getValueGetter();
        this.titles = attr.getTitles();
        this.rowsHeight4Head = attr.getHeadHeightArr();

        this.order = attr.getOrder();
        this.name = attr.getName();
        this.width = attr.getColumnWidth();

        this.offset = attr.getOffsetVal();
        this.offsetHeadRowsCnt = Math.min(attr.getOffsetHeadRows(), MAX);
        this.fillSkipped = attr.getOffsetFillSkipped();
        this.defaultClassname = StyleUtil.classname(config.getTargetClass(), name);
    }

    protected final PropertyControl getControl() { return control; }

    protected final GetTransformer getTransform() { return transform; }

    protected final int getOffset() { return offset; }

    protected final boolean isFillSkipped() { return fillSkipped; }

    int getDepth() { return 1; }

    /*
     * 收集样式
     */

    void collectStyleMap(Map<Class, Map<String, StyleBuilder>> definitions, Map sourceMap) {}

    /*
     * 列宽
     */

    void appendColumnWidth(List<Integer> columnsWidth) {
        int dftWidth = DEFAULT_HEIGHT;
        for (int i = 0; i < offset; i++) {
            columnsWidth.add(dftWidth);
        }
        columnsWidth.add(width == null ? dftWidth : width);
    }

    /*
     * 表头标题
     */

    void appendTitlesAtRowIdx(List<HeadCell> rowTitles, int rowIdx) {
        appendTitles4Offset(rowTitles, rowIdx);
        rowTitles.add(headCellAtIdx(rowIdx));
    }

    final void appendTitles4Offset(List<HeadCell> rowTitles, int rowIdx) {
        HeadCell thisCell = HeadCell.EMPTY;
        if (rowIdx + offsetHeadRowsCnt < getHeaderRowsCount()) {
            thisCell = headCellAtIdx(rowIdx);
        }
        for (int i = 0; i < offset; i++) {
            rowTitles.add(thisCell);
        }
    }

    private final HeadCell headCellAtIdx(int rowIdx) {
        return new HeadCell(titleAtIdx(rowIdx), rowHeightAtIdx(rowIdx), fillSkipped);
    }

    private final short rowHeightAtIdx(int rowIdx) {
        // 表头行高
        short[] values = this.rowsHeight4Head;
        int count = values.length;
        int heightIdx = rowIdx < count ? rowIdx : count - 1;
        return inRange(heightIdx, count) ? values[heightIdx] : DEFAULT_HEIGHT;
    }

    private final String titleAtIdx(int rowIdx) {
        // 表头标题
        int length = getHeaderRowsLength();
        int index = rowIdx < length ? rowIdx : length - 1;
        return inRange(index, length) ? titles[index] : null;
    }

    private static boolean inRange(int index, int length) { return index > -1 && index < length; }

    /*
     * 行列数计算
     */

    /**
     * 自身表头所占行数
     *
     * @return
     */
    final int getHeaderRowsLength() { return titles.length; }

    /**
     * 整“列”表头所占行数
     * <p>
     * 这里单独写成一个获取 length 的方法是为了后面支持 ColumnGroup 时用计算的方式获取最大长度
     *
     * @return
     */
    int getHeaderRowsCount() { return getHeaderRowsLength(); }

    /**
     * 横跨单元格列数
     *
     * @return
     */
    int getCrossColsCount() { return offset + 1; }

    /**
     * 执行偏移
     *
     * @param factory
     * @param indexer
     */
    private final void doOffsetCells(RowFactory factory, IntAccessor indexer) {
        int offset = this.offset;
        if (fillSkipped) {
            int start = indexer.get();
            for (int i = 0; i < offset; i++) {
                factory.index(i + start);
            }
        }
        indexer.increment(offset);
    }

    /**
     * 当前单元格实际有意义位置的单元格
     *
     * @param rowFactory
     * @param indexer
     *
     * @return
     */
    final CellFactory indexedCell(RowFactory rowFactory, IntAccessor indexer) {
        doOffsetCells(rowFactory, indexer);
        return rowFactory.index(indexer.getAndIncrement());
    }

    /**
     * 跳过当前单元格（用在数据 null 时）
     *
     * @param factory
     * @param indexer
     */
    final void skip(RowFactory factory, IntAccessor indexer) {
        if (fillSkipped) {
            indexedCell(factory, indexer);
        } else {
            indexer.increment(offset + 1);
        }
    }

    void render(TableProxy proxy) {
        if (proxy.isSkipped()) {
            proxy.skip(getOffset(), isFillSkipped());
        } else {
            // proxy.renderThisCell(offset, fillSkipped, getControl(), getTransform());
            CellFactory cellFactory = proxy.indexedCell(getOffset(), isFillSkipped());
            Object thisData = proxy.getThisData(getControl());
            transform.doTransform(cellFactory, thisData);
        }
    }

    void render(IntAccessor indexer, RowFactory factory, Object data) {
        if (data == null) {
            skip(factory, indexer);
        } else {
            CellFactory cellFactory = indexedCell(factory, indexer);
            transform.doTransform(cellFactory, control.control(data));
        }
    }

    @Override
    public final int compareTo(TableCol o) { return this.order - o.order; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TableCol: ");
        sb.append(name).append("; Titles: ");
        sb.append(StringUtil.defaultIfEmpty(JoinerUtil.join(titles), "<空>"));
        return sb.toString();
    }
}
