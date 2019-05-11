package com.moon.core.lang.ref;

import java.util.HashMap;

/**
 * @author benshaoye
 */
public class HashLocation<X, Y, Z> extends DefaultLocation<X, Y, Z> {

    public HashLocation() { super(HashMap::new); }

    public final static <X, Y, Z> HashLocation<X, Y, Z> of() { return new HashLocation<>(); }

    public final static <X, Y, Z> HashLocation<X, Y, Z> ofManaged() { return new HashLocation<>(); }
}
