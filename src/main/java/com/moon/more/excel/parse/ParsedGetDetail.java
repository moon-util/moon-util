package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
class ParsedGetDetail extends ParsedDetail<DefinedGet> {

    public ParsedGetDetail(
        List<DefinedGet> getters, RootDetail root, DefinedGet starting, DefinedGet ending
    ) { super(getters, root, starting, ending); }
}
