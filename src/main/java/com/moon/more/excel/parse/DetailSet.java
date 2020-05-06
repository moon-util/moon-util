package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
class DetailSet extends Detail<DefinedSet> {

    public DetailSet(
        List<DefinedSet> setters, DetailRoot root, DefinedSet starting, DefinedSet ending
    ) { super(setters, root, starting, ending); }
}
