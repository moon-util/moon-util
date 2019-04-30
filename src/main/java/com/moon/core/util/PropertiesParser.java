package com.moon.core.util;

import com.moon.core.enums.Const;
import com.moon.core.io.FileUtil;
import com.moon.core.lang.IntUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.interfaces.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.moon.core.lang.StringUtil.*;

/**
 * properties 文件解析器，以下是解析规则：
 * <p>
 * 与 spring.profiles.include、spring.profiles.active 相似，解析器引入了——引入文件、活跃文件、引用分割符的概念；
 * <p>
 * 这里的 spring.profiles 即是命名空间（namespace），这个解析器也引入了同样含义的命名空间，默认是 moon，
 * 当然也可以自定义这个命名空间，但是这里要求命名空间必须是<strong>非空白字符串</strong>；
 * <p>
 * 在命名空间下有：
 * 三个关键的键值映射 “import”、“active”、“delimiters”；
 * 两个关键字 “name:”、“path:”；
 * 和一个符号 “:”。
 * <p>
 * 分别对应：namespace.import、namespace.active、namespace.delimiters;
 * <p>
 * 默认是：moon.import、moon.active、moon.delimiters;
 * <p>
 * 【注意】这里是 import 而不是 include。
 * <p>
 * 以下以解析 properties 文件 moon.properties 为例：
 * <p>
 * <strong>【若 1 】</strong>：基本
 * <p>
 * moon.import=pre
 * <p>
 * moon.active=dev
 * <p>
 * 在解析 moon.properties 前将先依次解析：moon-pre.properties、moon-dev.properties，
 * <p>
 * 如果 import、active 文件里还有对应的 import、active 字段，将递归解析，然后再返回；
 * <p>
 * <strong>【若 2 】</strong>：可分别在 import、active 上重新定义解析命名空间（namespace），使用英文逗号（:）
 * <p>
 * moon.import=pre:spring
 * <p>
 * moon.active=dev:other
 * <p>
 * 在解析 moon.properties 前将先依次解析：moon-pre.properties、moon-dev.properties，
 * <p>
 * 并且使用各自的命名空间：spring、other；
 * <p>
 * <strong>【若 3 】</strong>：可指定为全名
 * <p>
 * moon.import=name:application-pre:spring
 * <p>
 * moon.active=name:application-dev
 * <p>
 * 在解析 moon.properties 前将先依次解析：application-pre.properties、application-dev.properties，
 * <p>
 * 同样支持在后面用冒号自定义命名空间；
 * <p>
 * <strong>【若 4 】</strong>：可指定为绝对路径
 * <p>
 * 在指定全名中，如果 moon.properties 实际上不是根目录，而是 otherDir/moon.properties，
 * 最后解析的将是 otherDir/application-pre.properties，但是这里可以自定义完整路径
 * <p>
 * moon.import=path:anotherDir/application-pre:spring
 * <p>
 * moon.import=path:anotherDir/application-dev
 * <p>
 * 在解析 moon.properties 前将先依次解析：anotherDir/application-pre.properties、anotherDir/application-dev.properties，
 * <p>
 * 若在文件中指定了 moon.delimiters 字段，则要求是用英文逗号分割的两个非空白字符串（超出的将忽略），用来包裹取值的键，
 * 通常对应的值在 moon.import 或 moon.active 中，也可以在当前文件中，并且不分顺序，递归引用（但不能循环引用），如：
 * <p>
 * moon.delimiters=${,}
 * other.key=${from.moon.active.key}
 * current.key=${other.key}
 * <p>
 * 同时如果当前文件定义了 delimiters 字段，此字段的值将会向 active 和 import 传递，不需要重新设置，
 * 当然，active、import 文件里还是可以进一步设置此 delimiters 字段，在进行递归解析的时候，将继续传递。
 * <p>
 * 最后返回所有键值对，如果存在相同键，active 文件覆盖 import 文件，当前文件覆盖 active 文件
 *
 * @author benshaoye
 */
public class PropertiesParser implements Parser<PropertiesHashMap> {

    private final static PropertiesHashMap EMPTY_MAP = EmptyHashMap.DEFAULT;

    private final static String DEFAULT_NAMESPACE = "moon";
    private final static String NAME = "name:";
    private final static String PATH = "path:";
    private final static String COLON = ":";
    private final static int TWO = 2;

    private final static String SUFFIX = ".properties";

    private final String currentNamespace;
    private final String currentImportName;
    private final String currentActiveName;
    private final String currentDelimitersName;
    /**
     * delimiters 是否可以从当前文件向 import、active 引用文件传递
     * 默认不可传递
     */
    private final boolean bubbleDelimiters;
    /**
     * 包括：{@link #currentImportName}、{@link #currentActiveName}、{@link #currentDelimitersName}
     */
    private final Set<String> excludeKeys;
    /**
     * 防止循环引用
     */
    private final Map<String, PropertiesHashMap> parsedSources;

    /*
     * ----------------------------------------------------------------------------
     * constructor
     * ----------------------------------------------------------------------------
     */

    protected PropertiesParser() { this(DEFAULT_NAMESPACE); }

    protected PropertiesParser(String namespace) { this(namespace, false); }

    protected PropertiesParser(String namespace, boolean bubbleDelimiters) { this(namespace, bubbleDelimiters, new HashMap<>()); }

    protected PropertiesParser(
        String namespace, boolean bubbleDelimiters,
        Map<String, PropertiesHashMap> parsedSources
    ) {
        this.currentNamespace = namespace = requireNotEmpty(trimToNull(namespace));
        this.currentDelimitersName = namespace + ".delimiters";
        this.currentImportName = namespace + ".import";
        this.currentActiveName = namespace + ".active";
        this.bubbleDelimiters = bubbleDelimiters;
        this.parsedSources = parsedSources;
        excludeKeys = SetUtil.ofHashSet(
            currentDelimitersName,
            currentImportName,
            currentActiveName
        );
    }

    /**
     * 实现
     *
     * @param namespace
     * @param bubbleDelimiters
     * @param parsedSources
     * @return
     */
    protected PropertiesParser getParser(
        String namespace, boolean bubbleDelimiters,
        Map<String, PropertiesHashMap> parsedSources
    ) { return new PropertiesParser(namespace, bubbleDelimiters, parsedSources); }

    /**
     * 解析
     *
     * @param sourcePath
     * @return
     */
    protected PropertiesHashMap getProps(String sourcePath) {
        return new PropertiesHashMap(PropertiesUtil.get(sourcePath));
    }

    /*
     * ----------------------------------------------------------------------------
     * public method
     * ----------------------------------------------------------------------------
     */

    public Properties resolveProperties(String propertiesSource) { return MapUtil.putAll(new Properties(), parse(propertiesSource)); }

    @Override
    public PropertiesHashMap parse(final String propertiesSource) { return parse(propertiesSource, DELIMITERS); }

    public PropertiesGroup parseAsGroup(String propertiesSource) { return PropertiesGroup.of(parse(propertiesSource)); }

    /*
     * ----------------------------------------------------------------------------
     * parse core
     * ----------------------------------------------------------------------------
     */

    private PropertiesHashMap parse(final String propertiesSource, IDelimiters defaultDelimiters) {
        if (parsedSources.containsKey(propertiesSource)) {
            return parsedSources.getOrDefault(propertiesSource, EMPTY_MAP);
        } else {
            parsedSources.put(propertiesSource, null);
        }
        final String sourcePath = FileUtil.formatPath(propertiesSource);
        final String activeName = activeName(sourcePath);
        final String activePath = activePath(sourcePath);

        PropertiesHashMap currentProps = getProps(sourcePath);
        IDelimiters delimiters = parseDelimiters(currentProps,
            bubbleDelimiters ? defaultDelimiters : DELIMITERS);
        PropertiesHashMap importProps = parseImport(currentProps, activePath, activeName, delimiters);
        PropertiesHashMap activeProps = parseActive(currentProps, activePath, activeName, delimiters);

        PropertiesHashMap properties = computeProps(currentProps, importProps, activeProps, delimiters);

        parsedSources.put(propertiesSource, properties);
        return properties;
    }

    /*
     * -------------------------------------------------------------------------
     * compute result properties
     * -------------------------------------------------------------------------
     */

    final PropertiesHashMap computeProps(
        PropertiesHashMap props, PropertiesHashMap imports,
        PropertiesHashMap actives, IDelimiters delimiters
    ) {
        PropertiesHashMap computed = new PropertiesHashMap(props.size());
        PropertiesHashMap presents = new PropertiesHashMap(8);
        for (Map.Entry<String, String> entry : props.entrySet()) {
            String key = entry.getKey(), value = entry.getValue();
            if (!excludeKeys.contains(key)) {
                value = recursiveGetValue(key, value, delimiters,
                    presents, computed, props, imports, actives);
                presents.clear();
            }
            computed.put(key, value);
        }

        return new PropertiesHashMap(imports, actives, computed);
    }

    private final static String recursiveGetValue(
        String key, String value, IDelimiters delimiters,
        PropertiesHashMap presents, PropertiesHashMap computed,
        PropertiesHashMap props, PropertiesHashMap imports,
        PropertiesHashMap actives
    ) {
        if (presents.containsKey(key)) {
            throw new StackOverflowError("The stack overflow of key: " + key);
        } else {
            presents.put(key, null);
        }
        String start = delimiters.getStart(), end = delimiters.getEnd();
        if ((value = trimToNull(value)) != null && value.startsWith(start) && value.endsWith(end)) {
            String importKey = trimToNull(value.substring(start.length(), value.indexOf(end)));
            String importValue = getValue(importKey, computed, props, imports, actives);
            return recursiveGetValue(importKey, importValue, delimiters,
                presents, computed, props, imports, actives);
        }
        return value;
    }

    private final static String getValue(
        String key, PropertiesHashMap computed,
        PropertiesHashMap props, PropertiesHashMap imports,
        PropertiesHashMap actives) {
        String value = getValue(computed, key);
        value = value == null ? getValue(props, key) : value;
        value = value == null ? getValue(imports, key) : value;
        value = value == null ? getValue(actives, key) : value;
        return value;
    }

    private final static String getValue(PropertiesHashMap map, String key) {
        String value = map.get(key);
        return value == null && map.containsKey(key) ? Const.EMPTY : value;
    }

    /*
     * -------------------------------------------------------------------------
     * parse delimiters
     * -------------------------------------------------------------------------
     */

    private IDelimiters parseDelimiters(PropertiesHashMap props, IDelimiters defaultDelimiters) {
        String delimitersValue = trimToNull(props.get(currentDelimitersName));
        if (delimitersValue != null) {
            try {
                String[] delimiters = delimitersValue.split(",");
                return new Delimiters(delimiters[0], delimiters[1]);
            } catch (Throwable e) {
                throw new IllegalArgumentException("无效分隔符（delimiters）" +
                    "，必须是用英文逗号（,）分割的两个非空字符串，但是配置的是：" + delimitersValue);
            }
        }
        return defaultDelimiters;
    }

    /*
     * -------------------------------------------------------------------------
     * parse includes
     * -------------------------------------------------------------------------
     */

    private final PropertiesHashMap parseActive(
        PropertiesHashMap props, String activePath,
        String activeName, IDelimiters delimiters) {
        return parseInclude(props, currentActiveName, activePath, activeName, delimiters);
    }

    private final PropertiesHashMap parseImport(
        PropertiesHashMap props, String activePath,
        String activeName, IDelimiters delimiters) {
        return parseInclude(props, currentImportName, activePath, activeName, delimiters);
    }

    private PropertiesHashMap parseInclude(
        PropertiesHashMap props, String includeTargetName,
        String activePath, String activeName, IDelimiters delimiters) {
        PropertiesHashMap includeProps = EMPTY_MAP;
        String includeName = StringUtil.trimToNull(props.get(includeTargetName));
        if (StringUtil.isNotEmpty(includeName)) {
            includeProps = parseIncludeProps(
                includeName, activePath, activeName, delimiters);
        }
        return includeProps;
    }

    private PropertiesHashMap parseIncludeProps(
        String inputName, String activePath, String activeName, IDelimiters delimiters) {
        String[] inputs = inputName.split(",");
        PropertiesHashMap properties = new PropertiesHashMap(16 * inputs.length);
        for (int i = 0; i < inputs.length; i++) {
            properties.putAll(parseInputName(inputs[i],
                activePath, activeName, delimiters));
        }
        return properties;
    }

    private final PropertiesHashMap parseInputName(
        String inputName, String activePath, String activeName, IDelimiters delimiters) {
        PropertiesHashMap props = EMPTY_MAP;
        String formatted, simpleName = trimToEmpty(inputName);
        if (simpleName.startsWith(NAME)) {
            String sourceDir = activeDir(activePath, activeName);
            String[] strings = simpleName.split(COLON);
            String name = trimToNull(strings[1]);
            formatted = toPropertiesName(sourceDir + requireNotEmpty(name));
            props = parseNSMap(formatted, strings, delimiters);
        } else if (simpleName.startsWith(PATH)) {
            String[] strings = simpleName.split(COLON);
            formatted = toPropertiesName(requireNotEmpty(trimToNull(strings[1])));
            props = parseNSMap(formatted, strings, delimiters);
        } else if (simpleName.contains(COLON)) {
            String[] strings = simpleName.split(COLON);
            String name = trimToNull(strings[0]);
            formatted = toPropertiesName(activePath + '-' + requireNotEmpty(name));
            props = parseNSMap(trimToNull(strings[1]), formatted, delimiters);
        } else if (!simpleName.isEmpty()) {
            formatted = toPropertiesName(activePath + '-' + requireNotEmpty(simpleName));
            props = parseNSMap(null, formatted, delimiters);
        }
        return props;
    }

    /**
     * 自定义解析 delimiters 的功能在这添加，ns 即为包含 delimiters 的数据
     * 解析过程和逻辑待实现
     *
     * @param ns
     * @param formatted
     * @param delimiters
     * @return
     */
    private PropertiesHashMap parseNSMap(
        String ns, String formatted, IDelimiters delimiters) {
        return ns == null || StringUtil.equals(ns, currentNamespace) ? parse(formatted, delimiters)
            : getParser(ns, bubbleDelimiters, this.parsedSources).parse(formatted, delimiters);
    }

    private PropertiesHashMap parseNSMap(
        String formatted, String[] strings, IDelimiters delimiters) {
        return strings.length > TWO ? parseNSMap(trimToNull(strings[2]),
            formatted, delimiters) : parse(formatted, delimiters);
    }

    /*
     * -------------------------------------------------------------------------
     * inner tools
     * -------------------------------------------------------------------------
     */

    private static String activePath(String sourcePath) {
        int end = IntUtil.requireGt(sourcePath.lastIndexOf(SUFFIX), 0);
        return sourcePath.substring(0, end);
    }

    private static String activeName(String sourcePath) {
        int begin = minimumWithZero(sourcePath.lastIndexOf('/'));
        int end = IntUtil.requireGt(sourcePath.lastIndexOf(SUFFIX), begin);
        return sourcePath.substring(incrementIfPositive(begin), end);
    }

    private static String activeDir(String sourceName, String sourceBaseName) {
        return sourceName.substring(0, minimumWithZero(sourceName.indexOf(sourceBaseName)));
    }

    private static int incrementIfPositive(int value) {
        return value > 0 ? value + 1 : value;
    }

    private static int minimumWithZero(int value) {
        return Math.max(value, 0);
    }

    private static String toPropertiesName(String name) {
        return name.endsWith(SUFFIX) ? name : name + SUFFIX;
    }

    /*
     * -------------------------------------------------------------------------
     * delimiters
     * -------------------------------------------------------------------------
     */

    private final static IDelimiters DELIMITERS = new IDelimiters();

    private static class IDelimiters {
        String getStart() { return "${"; }

        String getEnd() {return "}";}
    }

    private final static class Delimiters extends IDelimiters {
        private final String start;
        private final String end;

        Delimiters(String s, String e) {
            start = trimToDefault(s, DELIMITERS.getStart());
            end = trimToDefault(e, DELIMITERS.getEnd());
        }

        @Override
        String getStart() { return start; }

        @Override
        String getEnd() { return end; }
    }
}
