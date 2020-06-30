package com.moon.core.lang.ref;

import java.util.WeakHashMap;

/**
 * @author moonsky
 */
public class WeakLocation<X, Y, Z> extends DefaultLocation<X, Y, Z> {

    public WeakLocation() { super(WeakHashMap::new); }

    public final static <X, Y, Z> WeakLocation<X, Y, Z> of() { return new WeakLocation<>(); }

    public final static <X, Y, Z> WeakLocation<X, Y, Z> ofManaged() { return new WeakLocation<>(); }
}
