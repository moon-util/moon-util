package com.moon.core.lang;

import com.moon.core.enums.Const;
import com.moon.core.util.interfaces.ValueSupplier;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.moon.core.util.CollectUtil.size;
import static com.moon.core.util.IteratorUtil.forEach;

/**
 * @author benshaoye
 */
public class StringJoiner
    implements Supplier<String>, ValueSupplier<String>, Appendable, CharSequence, Cloneable, Serializable {

    private static final long serialVersionUID = 6428348081105594320L;

    private final static String EMPTY = Const.EMPTY;
    private final static int DFT_LEN = 16;

    private Function<Object, String> stringifier;

    private String prefix;
    private String delimiter;
    private String suffix;

    private String useForNull;
    private boolean requireNonNull;

    private StringBuilder container;

    private int itemCount;

    private final static Function defaultStringifier() { return String::valueOf; }

    private static final String emptyIfNull(CharSequence str) { return str == null ? EMPTY : str.toString(); }

    /*
     * -------------------------------------------------------
     * constructor
     * -------------------------------------------------------
     */

    public StringJoiner(CharSequence delimiter) { this(delimiter, null, null); }

    public StringJoiner(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        this(defaultStringifier(), prefix, delimiter, suffix, Const.NULL_STR, false, prefix);
    }

    private StringJoiner(
        Function<Object, String> stringifier,
        CharSequence prefix,
        CharSequence delimiter,
        CharSequence suffix,
        CharSequence useForNull,
        boolean requireNonNull,
        CharSequence value
    ) {
        this.useForNull = useForNull == null ? null : useForNull.toString();
        this.setStringifier(stringifier).setPrefix(prefix).setDelimiter(delimiter).setSuffix(suffix)
            .requireNonNull(requireNonNull).clear(value);
    }

    public final static StringJoiner of(CharSequence delimiter) {return new StringJoiner(delimiter);}

    public final static StringJoiner of(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return new StringJoiner(delimiter, prefix, suffix);
    }

    /*
     * -------------------------------------------------------
     * getter and setter
     * -------------------------------------------------------
     */

    public StringJoiner skipNulls() {
        this.useForNull = null;
        return this;
    }

    public StringJoiner useEmptyForNull() { return useForNull(EMPTY); }

    public StringJoiner useForNull(CharSequence cs) {
        this.useForNull = String.valueOf(cs);
        return this;
    }

    public StringJoiner requireNonNull() { return requireNonNull(true); }

    public StringJoiner requireNonNull(boolean requireNonNull) {
        this.requireNonNull = requireNonNull;
        return this;
    }

    public StringJoiner setDefaultStringifier() { return setStringifier(defaultStringifier()); }

    public StringJoiner setStringifier(Function<Object, String> stringifier) {
        this.stringifier = Objects.requireNonNull(stringifier);
        return this;
    }

    public Function<Object, String> getStringifier() { return stringifier; }

    public StringJoiner setPrefix(CharSequence prefix) {
        String old = this.prefix, now = this.prefix = emptyIfNull(prefix);
        if (old != null) {
            container.replace(0, old.length(), now);
        }
        return this;
    }

    public String getPrefix() { return prefix; }

    public StringJoiner setSuffix(CharSequence suffix) {
        this.suffix = emptyIfNull(suffix);
        return this;
    }

    public String getSuffix() { return suffix; }

    public StringJoiner setDelimiter(CharSequence delimiter) {
        this.delimiter = emptyIfNull(delimiter);
        return this;
    }

    public String getDelimiter() { return delimiter; }

    /*
     * -------------------------------------------------------
     * inner methods
     * -------------------------------------------------------
     */

    private final StringJoiner counter() {
        itemCount++;
        return this;
    }

    private final String stringify(Object item) { return stringifier.apply(item); }

    private final <T> T isAllowNull(T item) {
        return requireNonNull ? (item == null ? ThrowUtil.unchecked(null) : item) : item;
    }

    private <T> StringJoiner ifChecked(T val, Consumer<T> consumer) {
        if ((val = isAllowNull(val)) == null) {
            if (useForNull != null) {
                return add(useForNull);
            }
        } else {
            consumer.accept(val);
        }
        return this;
    }

    private StringJoiner addStringify(Object item) { return addDelimiter().append(stringify(item)).counter(); }

    /*
     * -------------------------------------------------------
     * join array
     * -------------------------------------------------------
     */

    public <T> StringJoiner join(T... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public StringJoiner join(boolean... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public StringJoiner join(char... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public StringJoiner join(byte... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public StringJoiner join(short... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public StringJoiner join(int... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public StringJoiner join(long... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public StringJoiner join(float... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public StringJoiner join(double... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    /*
     * -------------------------------------------------------
     * join collect
     * -------------------------------------------------------
     */

    public StringJoiner join(Collection collect) {
        return ensure(size(collect)).ifChecked(collect, values -> forEach(values, this::add));
    }

    public StringJoiner join(Iterable iterable) { return ifChecked(iterable, values -> forEach(values, this::add)); }

    public StringJoiner join(Iterator iterator) { return ifChecked(iterator, values -> forEach(values, this::add)); }

    public StringJoiner join(Enumeration enumeration) {
        return ifChecked(enumeration, values -> forEach(values, this::add));
    }

    public <K, V> StringJoiner join(Map<K, V> map, BiFunction<? super K, ? super V, CharSequence> merger) {
        return ifChecked(map, values -> values.forEach((key, value) -> add(merger.apply(key, value))));
    }

    /*
     * -------------------------------------------------------
     * merge
     * -------------------------------------------------------
     */

    public StringJoiner merge(StringJoiner joiner) { return add(joiner); }

    public StringJoiner merge(java.util.StringJoiner joiner) { return add(joiner); }

    /*
     * -------------------------------------------------------
     * adds
     * -------------------------------------------------------
     */

    private StringJoiner addDelimiter() { return itemCount > 0 ? appendDelimiter() : this; }

    public StringJoiner add(CharSequence csq) { return ifChecked(csq, this::addStringify); }

    public StringJoiner add(Object csq) { return ifChecked(csq, this::addStringify); }

    public StringJoiner add(char value) { return addDelimiter().append(value).counter(); }

    public StringJoiner add(int value) { return addDelimiter().append(value).counter(); }

    public StringJoiner add(long value) { return addDelimiter().append(value).counter(); }

    public StringJoiner add(float value) { return addDelimiter().append(value).counter(); }

    public StringJoiner add(double value) { return addDelimiter().append(value).counter(); }

    public StringJoiner add(boolean value) { return addDelimiter().append(value).counter(); }

    /*
     * -------------------------------------------------------
     * append to other
     * -------------------------------------------------------
     */

    public <A extends Appendable> A appendTo(A appender) {
        Objects.requireNonNull(appender);
        try {
            appender.append(toString());
        } catch (IOException e) {
            ThrowUtil.runtime(e);
        }
        return appender;
    }

    /*
     * -------------------------------------------------------
     * append: 始终忠诚的在最后追加一个值
     * -------------------------------------------------------
     */

    public StringJoiner appendDelimiter() { return append(delimiter); }

    public StringJoiner append(Object value) {
        container.append(value);
        return this;
    }

    @Override
    public StringJoiner append(CharSequence csq) {
        container.append(csq);
        return this;
    }

    @Override
    public StringJoiner append(CharSequence csq, int start, int end) {
        container.append(csq, start, end);
        return this;
    }

    @Override
    public StringJoiner append(char value) {
        container.append(value);
        return this;
    }

    public StringJoiner append(int value) {
        container.append(value);
        return this;
    }

    public StringJoiner append(long value) {
        container.append(value);
        return this;
    }

    public StringJoiner append(float value) {
        container.append(value);
        return this;
    }

    public StringJoiner append(double value) {
        container.append(value);
        return this;
    }

    public StringJoiner append(boolean value) {
        container.append(value);
        return this;
    }

    /*
     * -------------------------------------------------------
     * overrides
     * -------------------------------------------------------
     */

    private StringJoiner clear(CharSequence defaultValue) {
        this.container = defaultValue == null ? new StringBuilder() : new StringBuilder(defaultValue);
        this.itemCount = 0;
        return this;
    }

    private StringJoiner ensure(int size) { return ensureCapacity(size * DFT_LEN); }

    public StringJoiner ensureCapacity(int minCapacity) {
        container.ensureCapacity(minCapacity);
        return this;
    }

    public StringJoiner clear() { return clear(prefix); }

    public int contentLength() { return container.length() - prefix.length(); }

    @Override
    public int length() { return container.length() + suffix.length(); }

    @Override
    public StringJoiner clone() {
        return new StringJoiner(stringifier, prefix, delimiter, suffix, useForNull, requireNonNull, container);
    }

    @Override
    public char charAt(int index) {
        int contentLen = container.length();
        return index < contentLen ? container.charAt(index) : suffix.charAt(index - contentLen);
    }

    @Override
    public StringJoiner subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValue() { return get(); }

    @Override
    public String get() { return toString(); }

    @Override
    public String toString() { return new StringBuilder(container).append(suffix).toString(); }
}
