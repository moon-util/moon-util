package com.moon.html.node;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * {@code TokenList}是一组空格分隔的标记，如{@link Element#classList()}
 *
 * @author benshaoye
 */
public class TokenList extends LinkedHashSet<String> {

    private final static Consumer<TokenList> SETTER = l -> {};

    private final Consumer<TokenList> setter;

    public TokenList(String token) {
        this(token, SETTER);
    }

    TokenList(String token, Consumer<TokenList> setter) {
        super.addAll(StringUtil.split(token, ' '));
        this.setter = setter;
    }

    public TokenList(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.setter = SETTER;
    }

    public TokenList(int initialCapacity) {
        super(initialCapacity);
        this.setter = SETTER;
    }

    public TokenList() {
        super();
        this.setter = SETTER;
    }

    public TokenList(Collection<? extends String> c) {
        this(c, SETTER);
    }

    public TokenList(Collection<? extends String> c, Consumer<TokenList> setter) {
        super(c);
        this.setter = setter;
    }

    public String value() { return JoinerUtil.join(this, " "); }

    @Override
    public boolean add(String s) {
        return this.onSuccess(s, super::add);
    }

    @Override
    public boolean remove(Object o) {
        return this.onSuccess(o, super::remove);
    }

    @Override
    public void clear() {
        super.clear();
        this.setter.accept(this);
    }

    @Override
    public TokenList clone() {
        return new TokenList(this, this.setter);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.onSuccess(c, super::removeAll);
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        return this.onSuccess(c, super::addAll);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.onSuccess(c, super::retainAll);
    }

    @Override
    public boolean removeIf(Predicate<? super String> filter) {
        return this.onSuccess(filter, super::removeIf);
    }

    private <T> boolean onSuccess(T param, Predicate<T> tester) {
        if (tester.test(param)) {
            this.setter.accept(this);
            return true;
        }
        return false;
    }
}
