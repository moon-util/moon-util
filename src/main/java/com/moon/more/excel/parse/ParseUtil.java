package com.moon.more.excel.parse;

/**
 * @author benshaoye
 */
public abstract class ParseUtil {

    private static class SetterParser extends Parser {

        private SetterParser() { super(Creator.asSetter()); }
    }

    private static class GetterParser extends Parser {

        private GetterParser() { super(Creator.asGetter()); }
    }

    final static GetterParser GETTER = new GetterParser();
    final static SetterParser SETTER = new SetterParser();

    static PropertiesGroupGet parseGetter(Class<?> targetClass) { return (PropertiesGroupGet) GETTER.doParse(targetClass); }

    protected static MarkedColumnGroup parse(Class targetClass) { return GETTER.doParseAsCol(targetClass); }

    static PropertiesGroupSet parseSetter(Class<?> targetClass) { return (PropertiesGroupSet) SETTER.doParse(targetClass); }
}
