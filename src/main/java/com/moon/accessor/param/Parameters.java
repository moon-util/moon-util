package com.moon.accessor.param;

import java.util.ArrayList;

/**
 * @author benshaoye
 */
public final class Parameters extends ArrayList<ParameterSetter> {

    public Parameters() { }

    public static Parameters of() { return new Parameters(); }
}
