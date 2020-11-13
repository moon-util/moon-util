package com.moon.mapping.processing;

import java.util.Map;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
abstract class MapScripts {

    /**
     * @see com.moon.mapping.BeanMapping#doForward(Object)
     * @see com.moon.mapping.BeanMapping#doForward(Object, Object)
     */
    final static String unsafeForward = "" +//
        "@Override public Object unsafeForward(Object thisObject, Object thatObject) {" +//
        "    {thatType} that=({thatType})thatObject;" +//
        "    {thisType} self=({thisType})thisObject;" +//
        "    {MAPPINGS}" +//
        "    return thatObject;" +//
        "}";
    /**
     * @see com.moon.mapping.BeanMapping#doBackward(Object)
     * @see com.moon.mapping.BeanMapping#doBackward(Object, Object)
     */
    final static String unsafeBackward = "" +//
        "@Override public Object unsafeBackward(Object thisObject, Object thatObject) {" +//
        "    {thatType} that=({thatType})thatObject;" +//
        "    {thisType} self=({thisType})thisObject;" +//
        "    {MAPPINGS} " +//
        "    return self; " +//
        "}";

    private MapScripts() {}

    /** @see com.moon.mapping.BeanMapping#doForward(Object) */
    final static String newThatAsEmpty = "" +//
        "@Override " +//
        "public Object doForward(Object thisObject) {" +//
        "    return thisObject == null ? null : unsafeForward(thisObject, new {thatType}());" +//
        "}";
    final static String newThatAsEmpty4NonFields = "" +//
        "@Override " +//
        "public Object doForward(Object thisObject) {return thisObject == null ? null : new {thatType}();}";

    /** @see com.moon.mapping.BeanMapping#doBackward(Object) */
    final static String newThisAsEmpty = "" +//
        "@Override " +//
        "public Object doBackward(Object thatObject) {" +//
        "    return thatObject == null ? null : unsafeBackward(new {thisType}(), thatObject);" +//
        "}";

    /** @see com.moon.mapping.BeanMapping#doBackward(Object) */
    final static String newThisAsEmpty4NonFields = "" +//
        "@Override " +//
        "public Object doBackward(Object thatObject) {return thatObject == null ? null : new {thisType}();}";

    /** @see com.moon.mapping.MapMapping#fromMap(Object, Map) */
    static final String fromMap = "" +//
        "@Override " +//
        "public Object fromMap(Object thisObject, java.util.Map thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { return thisObject; }" +//
        "    {thisType} self = ({thisType}) thisObject;" +//
        "    {MAPPINGS}" +//
        "    return thisObject;" +//
        "}";

    /** @see com.moon.mapping.MapMapping#toMap(Object, Map) */
    final static String toMap = "" +//
        "@Override " +//
        "public java.util.Map toMap(Object thisObject, java.util.Map thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { return thatObject; }" +//
        "    {thisType} self = ({thisType}) thisObject;" +//
        "    {MAPPINGS}" +//
        "    return thatObject;" +//
        "}";

    /** @see com.moon.mapping.MapMapping#newThis(Map) */
    final static String newThisAsMap = "" +//
        "@Override " +//
        "public Object newThis(java.util.Map thatObject) {" +//
        "    if (thatObject == null) { return null; }" +//
        "    return fromMap(new {thisType}(), thatObject);" +//
        "}";

    /** @see com.moon.mapping.ObjectMapping#toString(Object) */
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

    /** @see com.moon.mapping.ObjectMapping#clone(Object) */
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
