package com.moon.core.util.iterator;

import com.moon.core.lang.StringUtil;

import java.util.Iterator;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public class CharsIterator
    extends BaseArrayIterator
    implements Iterator<Character> {

    private final char[] array;

    public CharsIterator(CharSequence string) { this(StringUtil.toCharArray(string)); }

    public CharsIterator(char[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Character next() { return this.array[index++]; }
}
