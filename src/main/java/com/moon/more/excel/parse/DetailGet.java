package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
class DetailGet extends Detail<DefinedGet> {

    public DetailGet(
        List<DefinedGet> getters, DetailRoot root, DefinedGet starting, DefinedGet ending
    ) { super(getters, root, starting, ending); }
}
