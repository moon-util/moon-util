package com.moon.more.excel.parse;

import com.moon.more.excel.Renderer;

/**
 * @author benshaoye
 */
public abstract class ParseUtil {

    private static class SetterParser extends CoreParser {

        private SetterParser() { super(Creator.asSetter()); }
    }

    private static class GetterParser extends CoreParser {

        private GetterParser() { super(Creator.asGetter()); }
    }

    final static GetterParser GETTER = new GetterParser();
    final static SetterParser SETTER = new SetterParser();

    static PropertiesGroupGet parseGetter(Class<?> targetClass) { return (PropertiesGroupGet) GETTER.doParse(targetClass); }

    protected static Renderer parse(Class targetClass) { return GETTER.doParseAsCol(targetClass); }

    static PropertiesGroupSet parseSetter(Class<?> targetClass) { return (PropertiesGroupSet) SETTER.doParse(targetClass); }
}
