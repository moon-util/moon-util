package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
class ParsedSetDetail extends ParsedDetail<DefinedSet> {

    public ParsedSetDetail(
        List<DefinedSet> setters, ParsedRootDetail root, DefinedSet starting, DefinedSet ending
    ) { super(setters, root, starting, ending); }
}
