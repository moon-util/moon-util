package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;

import java.util.List;
import java.util.Objects;

import static com.moon.core.util.CollectUtil.toArrayOfEmpty;

/**
 * @author benshaoye
 */
abstract class AbstractMarkGroup<T extends MarkColumn> {

    private final MarkRenderer[] columns;

    private final T rootIndexer;

    private final RowRecord root;

    /**
     * 标记{@link #columns}中是否有索引字段
     * <p>
     * 用于提升性能，避免每次都循环
     *
     * @see CoreParser#transform(PropertiesGroup, IntAccessor)
     */
    @SuppressWarnings("all")
    private final boolean indexed;

    /**
     * @param columns
     * @param iterableAt  如果不为 null，它是 columns 中的一项
     * @param rootIndexer null，或者索引，不属于 columns 中
     * @param root
     * @param indexed     columns 中是否包含一项或多项“索引”
     *
     * @see CoreParser#transform(PropertiesGroup, IntAccessor)
     */
    @SuppressWarnings("all")
    protected AbstractMarkGroup(
        List<T> columns, T rootIndexer, RowRecord root, boolean indexed
    ) {
        this.columns = toArrayOfEmpty(columns, MarkColumn[]::new, MarkRenderer.EMPTY);
        this.root = Objects.requireNonNull(root);
        this.rootIndexer = rootIndexer;
        this.indexed = indexed;
    }

    public MarkRenderer[] getColumns() { return columns; }

    public T getRootIndexer() { return rootIndexer; }

    public RowRecord getRoot() { return root; }

    public boolean isIndexed() { return indexed; }
}
