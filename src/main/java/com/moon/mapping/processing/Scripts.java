package com.moon.mapping.processing;

import java.util.Map;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
abstract class Scripts {

    /**
     * @see com.moon.mapping.BeanMapping#newThat(Object)
     * @see com.moon.mapping.BeanMapping#copyBackward(Object, Object)
     */
    final static String safeCopyBackward = "" +//
        "private void safeCopyBackward({thisType} self, {thatType} that) { {MAPPINGS} }";
    /**
     * @see com.moon.mapping.BeanMapping#newThat(Object)
     * @see com.moon.mapping.BeanMapping#copyForward(Object, Object)
     */
    final static String safeCopyForward = "" +//
        "private void safeCopyForward({thisType} self, {thatType} that) { {MAPPINGS} }";

    private Scripts() {}

    /** @see com.moon.mapping.BeanMapping#newThat(Object) */
    final static String newThatAsEmpty = "" +//
        "@Override " +//
        "public Object newThat(Object thisObject) {" +//
        "    if (thisObject == null) { return null; }" +//
        "    {thatType} that = new {thatType}();" +//
        "    safeCopyForward(({thisType}) thisObject, that);" +//
        "    return that;" +//
        "}";

    /** @see com.moon.mapping.BeanMapping#newThis(Object) */
    final static String newThisAsEmpty = "" +//
        "@Override " +//
        "public Object newThis(Object thatObject) {" +//
        "    if (thatObject == null) { return null; }" +//
        "    {thisType} self = new {thisType}();" +//
        "    safeCopyBackward(self, ({thatType}) thatObject);" +//
        "    return self;" +//
        "}";

    /** @see com.moon.mapping.BeanMapping#fromThat(Object, Object) */
    final static String copyBackward = "" +//
        "@Override " +//
        "public Object copyBackward(Object thisObject, Object thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { return thisObject; }" +//
        "    safeCopyBackward(({thisType}) thisObject, ({thatType}) thatObject);" +//
        "    return thisObject;" +//
        "}";

    /** @see com.moon.mapping.BeanMapping#toThat(Object, Object) */
    final static String copyForward = "" +//
        "@Override " +//
        "public Object copyForward(Object thisObject, Object thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { return thatObject; }" +//
        "    safeCopyForward(({thisType}) thisObject, ({thatType}) thatObject);" +//
        "    return thatObject;" +//
        "}";

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
        "    {thisType} self = new {thisType}();" +//
        "    {MAPPINGS}" +//
        "    return self;" +//
        "}";
}
