package com.moon.core.util.console;

import com.moon.core.enums.Const;
import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.*;
import com.moon.core.util.Console.Level;
import com.moon.core.util.console.ConsoleControl.Classify;
import com.moon.core.util.json.JSON;
import com.moon.core.util.json.JSONArray;
import com.moon.core.util.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.moon.core.lang.ClassUtil.forName;
import static com.moon.core.lang.ClassUtil.requireExtendsOf;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.annotation.AnnotatedUtil.get;
import static com.moon.core.util.FilterUtil.nullableLast;
import static com.moon.core.util.OptionalUtil.computeOrDefault;
import static com.moon.core.util.OptionalUtil.elseGetIfNull;

/**
 * @author benshaoye
 */
final class ConsoleSettingsUtil {
    private ConsoleSettingsUtil() {
        noInstanceError();
    }

    public static final ConsoleSettings parseByStackTraces(StackTraceElement[] traces, Class topClass) {
        StackTraceElement element;
        boolean found = false;
        Class clazz;
        List<ConsoleSettings> controls = null;
        for (int i = 0; i < traces.length; i++) {
            if ((clazz = forName((element = traces[i]).getClassName())) == Console.class
                && "<clinit>".equals(element.getMethodName())) {
                // 忽略对初始化 Console.out 的检查
                return ConsoleGlobalControl.GLOBAL.getSettings();
            }
            if (found || (found = topClass.isAssignableFrom(clazz))) {
                ConsoleControl control = get(clazz, ConsoleControl.class);
                if (control != null) {
                    ConsoleSettings parsed = parseConsoleControl(control);
                    if (control.isDefault()) {
                        if (controls == null) {
                            controls = new ArrayList<>();
                        }
                        controls.add(parsed);
                    } else if (controls == null || controls.isEmpty()) {
                        return parsed;
                    } else {
                        controls.add(parsed);
                        return mergeSettings(controls);
                    }
                }
            }
        }
        return computeOrDefault(controls, ConsoleSettingsUtil::mergeSettings, ConsoleGlobalControl.GLOBAL.getSettings());
    }

    private static final ConsoleSettings mergeSettings(List<ConsoleSettings> consoleSettings) {
        final ConsoleSettings[] array = consoleSettings.toArray(new ConsoleSettings[consoleSettings.size()]);
        ConsoleSettings defaultSet = ListUtil.requireGetLast(consoleSettings);
        ConsoleSettings DEFAULT = ConsoleDefault.SETTINGS;

        boolean isHtml = DEFAULT.isHtml();
        if (isHtml == defaultSet.isHtml()) {
            final boolean finalIsHtml = isHtml;
            isHtml = computeOrDefault(
                nullableLast(array, settings ->
                    settings.isHtml() != finalIsHtml), find -> find.isHtml(), isHtml);
        } else {
            isHtml = defaultSet.isHtml();
        }

        boolean isAsync = DEFAULT.isAsync();
        if (isAsync == defaultSet.isAsync()) {
            final boolean finalIsAsync = isAsync;
            isAsync = computeOrDefault(
                nullableLast(array, settings ->
                    settings.isAsync() != finalIsAsync), find -> find.isAsync(), isAsync);
        } else {
            isAsync = defaultSet.isAsync();
        }

        Level level = DEFAULT.getLowestLevel();
        if (level == defaultSet.getLowestLevel()) {
            final Level finalLevel = level;
            level = computeOrDefault(
                nullableLast(array, settings ->
                    settings.getLowestLevel() != finalLevel), find -> find.getLowestLevel(), level);
        } else {
            level = defaultSet.getLowestLevel();
        }

        Classify[] classifies = DEFAULT.getClassifies();
        if (defaultSet.getClassifies().length == 0) {
            classifies = computeOrDefault(
                nullableLast(array, settings ->
                    settings.getClassifies().length > 0), find -> find.getClassifies(), classifies);
        } else {
            classifies = defaultSet.getClassifies();
        }

        String filenameFormat = DEFAULT.getFilenameFormat();
        if (defaultSet.getFilenameFormat().length() == 0) {
            filenameFormat = computeOrDefault(
                nullableLast(array, settings ->
                    settings.getFilenameFormat().length() > 0), find -> find.getFilenameFormat(), filenameFormat);
        } else {
            filenameFormat = defaultSet.getFilenameFormat();
        }

        String pattern = DEFAULT.getPattern();
        if (defaultSet.getPattern().length() == 0) {
            pattern = computeOrDefault(
                nullableLast(array, settings ->
                    settings.getPattern().length() > 0), find -> find.getPattern(), pattern);
        } else {
            pattern = defaultSet.getPattern();
        }

        return new ConsoleSettings(isHtml, isAsync, level, pattern, classifies, filenameFormat);
    }

    private static final ConsoleSettings parseConsoleControl(ConsoleControl control) {
        return elseGetIfNull(parseByClassOrJSON(control), () -> new ConsoleSettings(control));
    }

    private static final ConsoleSettings parseByClassOrJSON(ConsoleControl consoleControl) {
        return elseGetIfNull(parseByClass(consoleControl),
            () -> parseByJSON(consoleControl));
    }

    private final static Class TOP_CLASS = ConsoleSettingsSupplier.class;

    private static final ConsoleSettings parseByClass(ConsoleControl consoleControl) {
        Class type = requireExtendsOf(consoleControl.value(), TOP_CLASS);
        if (type == TOP_CLASS) {
            return (type = requireExtendsOf(consoleControl.fromImplement(), TOP_CLASS)) == TOP_CLASS
                ? null : ((ConsoleSettingsSupplier) ConstructorUtil.newInstance(type)).get();
        } else {
            return ((ConsoleSettingsSupplier) ConstructorUtil.newInstance(type)).get();
        }
    }

    private static final ConsoleSettings parseByJSON(ConsoleControl consoleControl) {
        String jsonFilename = consoleControl.fromJson();
        if ((jsonFilename = jsonFilename.trim()).length() > 0) {
            File jsonFile = new File(jsonFilename);
            if (jsonFile.exists()) {
                JSON jsonParsed = JSON.parse(jsonFile);
                if (jsonParsed instanceof JSONObject) {
                    return parseByJSONObject((JSONObject) jsonParsed);
                } else {
                    throw new ParseJsonConfigException("Can not parse configuration of: " + jsonFilename);
                }
            } else {
                throw new ParseJsonConfigException("Can not load json setting file: " + jsonFilename);
            }
        }
        return null;
    }

    private final static ConsoleSettings parseByJSONObject(JSONObject json) {
        return new ConsoleSettings(
            json.containsKey("html")
                ? ((Boolean) json.get("html")).booleanValue()
                : false,

            json.containsKey("async")
                ? ((Boolean) json.get("async")).booleanValue()
                : false,

            json.containsKey("lowestLevel")
                ? Level.valueOf(String.valueOf(json.get("lowestLevel")))
                : Console.LOWEST,

            json.containsKey("pattern")
                ? String.valueOf(json.get("pattern"))
                : Const.EMPTY,

            parseClassifiesByJSON(json.get("classifies")),

            json.containsKey("filenameFormat")
                ? String.valueOf(json.get("filenameFormat"))
                : Const.EMPTY
        );
    }

    private final static Classify[] parseClassifiesByJSON(Object classifies) {
        if (classifies == null) {
            classifies = new Classify[0];
        } else if (classifies instanceof String) {
            classifies = new Classify[]{
                Classify.valueOf(String.valueOf(classifies))
            };
        } else if (classifies instanceof JSONArray) {
            JSONArray array = (JSONArray) classifies;
            Classify[] temp = new Classify[array.size()];
            IteratorUtil.forEach(array, (item, idx) ->
                temp[idx] = Classify.valueOf(String.valueOf(item)));
            classifies = temp;
        } else {
            throw new IllegalArgumentException(JSONUtil.stringify(classifies));
        }
        return (Classify[]) classifies;
    }

    static class ParseJsonConfigException extends RuntimeException {
        public ParseJsonConfigException(String message) {
            super(message);
        }
    }
}
