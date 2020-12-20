package com.moon.mapper.processing;

import com.moon.mapper.BeanMapper;

import java.util.Map;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
abstract class MappingScripts {

    private static final String beanMappingStructure = "" +
        "package {pkg};" +
        "{imports}" +
        "{Component}" +
        "public class {classname} implements {BeanMapping}<{thisType}, {thatType}> {" +
        "    @Override" +
        "    public {thatType} unsafeForward({thisType} self, {thatType} that) {" +
        "        {MAPPINGS}" +
        "        return that;" +
        "    }" +
        "    @Override" +
        "    public {thisType} unsafeBackward({thisType} self, {thatType} that) {" +
        "        {MAPPINGS}" +
        "        return self;" +
        "    }" +
        "    @Override" +
        "    public {thatType} doForward({thisType} self) {" +
        "        return self == null ? null : unsafeForward(self, new {thatImpl}())" +
        "    }" +
        "    @Override" +
        "    public {thisType} doBackward({thatType} that) {" +
        "        return that == null ? null : unsafeBackward(new {thisImpl}(), that)" +
        "    }" +
        "}";

    /**
     * @see BeanMapper#doForward(Object)
     * @see BeanMapper#doForward(Object, Object)
     */
    final static String unsafeForward = "" +//
        "@Override public Object unsafeForward(Object thisObject, Object thatObject) {" +//
        "    {thatType} that=({thatType})thatObject;" +//
        "    {thisType} self=({thisType})thisObject;" +//
        "    {MAPPINGS}" +//
        "    return thatObject;" +//
        "}";
    /**
     * @see BeanMapper#doBackward(Object)
     * @see BeanMapper#doBackward(Object, Object)
     */
    final static String unsafeBackward = "" +//
        "@Override public Object unsafeBackward(Object thisObject, Object thatObject) {" +//
        "    {thatType} that=({thatType})thatObject;" +//
        "    {thisType} self=({thisType})thisObject;" +//
        "    {MAPPINGS} " +//
        "    return self; " +//
        "}";

    private MappingScripts() {}

    /** @see BeanMapper#doForward(Object) */
    final static String newThatAsEmpty = "" +//
        "@Override " +//
        "public Object doForward(Object thisObject) {" +//
        "    return thisObject == null ? null : unsafeForward(thisObject, new {thatType}());" +//
        "}";
    final static String newThatAsEmpty4NonFields = "" +//
        "@Override " +//
        "public Object doForward(Object thisObject) {return thisObject == null ? null : new {thatType}();}";

    /** @see BeanMapper#doBackward(Object) */
    final static String newThisAsEmpty = "" +//
        "@Override " +//
        "public Object doBackward(Object thatObject) {" +//
        "    return thatObject == null ? null : unsafeBackward(new {thisType}(), thatObject);" +//
        "}";

    /** @see BeanMapper#doBackward(Object) */
    final static String newThisAsEmpty4NonFields = "" +//
        "@Override " +//
        "public Object doBackward(Object thatObject) {return thatObject == null ? null : new {thisType}();}";

    /** @see com.moon.mapper.MapMapping#fromMap(Object, Map) */
    static final String fromMap = "" +//
        "@Override " +//
        "public Object fromMap(Object thisObject, java.util.Map thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { return thisObject; }" +//
        "    {thisType} self = ({thisType}) thisObject;" +//
        "    {MAPPINGS}" +//
        "    return thisObject;" +//
        "}";

    /** @see com.moon.mapper.MapMapping#toMap(Object, Map) */
    final static String toMap = "" +//
        "@Override " +//
        "public java.util.Map toMap(Object thisObject, java.util.Map thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { return thatObject; }" +//
        "    {thisType} self = ({thisType}) thisObject;" +//
        "    {MAPPINGS}" +//
        "    return thatObject;" +//
        "}";

    /** @see com.moon.mapper.MapMapping#newThis(Map) */
    final static String newThisAsMap = "" +//
        "@Override " +//
        "public Object newThis(java.util.Map thatObject) {" +//
        "    if (thatObject == null) { return null; }" +//
        "    return fromMap(new {thisType}(), thatObject);" +//
        "}";

    /** @see com.moon.mapper.ObjectMapping#toString(Object) */
    final static String toString = "" +//
        "@Override " +//
        "public String toString(Object thisObject) {" +//
        "    if (thisObject == null) { return \"null\"; }" +//
        "    if (!(thisObject instanceof {thisType})) { return thisObject.toString(); }" +//
        "    {thisType} self = ({thisType}) thisObject;" +//
        "    StringBuilder builder = new StringBuilder().append(\"{thisName}{\");" +//
        "    {MAPPINGS}" +//
        "    return builder.append('}').toString();" +//
        "}";

    /** @see com.moon.mapper.ObjectMapping#clone(Object) */
    final static String clone = "" +//
        "@Override " +//
        "public Object clone(Object thisObject) {" +//
        "    if (thisObject == null) { return null; }" +//
        "    {thisType} that = ({thisType}) thisObject;" +//
        "    {implType} self = new {implType}();" +//
        "    {MAPPINGS}" +//
        "    return self;" +//
        "}";
}
