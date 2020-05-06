package com.moon.more.excel.parse;

/**
 * @author benshaoye
 */
class ParseUtil {

    private static class SetterParser extends Parser {

        private SetterParser() { super(Creator.asSetter()); }
    }

    private static class GetterParser extends Parser {

        private GetterParser() { super(Creator.asGetter()); }
    }

    final static GetterParser GETTER = new GetterParser();
    final static SetterParser SETTER = new SetterParser();

    private ParseUtil() {}

    public static DetailGet parseGetter(Class<?> targetClass) {
        return (DetailGet) GETTER.doParse(targetClass);
    }

    public static DetailSet parseSetter(Class<?> targetClass) {
        return (DetailSet) SETTER.doParse(targetClass);
    }
}
