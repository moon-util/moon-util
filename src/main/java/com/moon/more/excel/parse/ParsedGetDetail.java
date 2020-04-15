package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
class ParsedGetDetail extends ParsedDetail<DefinedGet> {

    public ParsedGetDetail(
        List<DefinedGet> getters, ParsedRootDetail root, DefinedGet starting, DefinedGet ending
    ) { super(getters, root, starting, ending); }
}
