package com.moon.more.web;

import javax.servlet.http.HttpServletRequest;

import static com.moon.core.lang.StringUtil.isEmpty;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.more.web.RequestUtil.header;

/**
 * @author moonsky
 */
public class ContentTypeUtil {

    protected ContentTypeUtil() { noInstanceError(); }

    /**
     * 请求可接受什么形式的数据响应
     *
     * @param request
     *
     * @return
     */
    public final static boolean isAcceptAny(HttpServletRequest request) { return isAcceptOf(request, "*/*"); }

    /**
     * 请求可接受 JSON 形式的数据响应
     *
     * @param request
     *
     * @return
     */
    public final static boolean isAcceptJson(HttpServletRequest request) {
        return isAcceptOf(request, "application/json");
    }

    /**
     * 请求可接受 HTML 形式的数据响应
     *
     * @param request
     *
     * @return
     */
    public final static boolean isAcceptHtml(HttpServletRequest request) { return isAcceptOf(request, "text/html"); }

    /**
     * 请求可接受指定形式的数据响应
     *
     * @param request
     * @param expected 期望的响应形式
     *
     * @return
     */
    @SuppressWarnings("all")
    public final static boolean isAcceptOf(HttpServletRequest request, String expected) {
        if (request == null || expected == null) { return false; }
        String acceptVal = header(request, "accept");
        if (isEmpty(acceptVal)) { acceptVal = header(request, "Accept"); }
        if (isEmpty(acceptVal)) { acceptVal = header(request, "ACCEPT"); }
        return isAcceptOf(acceptVal, expected);
    }

    private static boolean isAcceptOf(String acceptVal, String expected) { return acceptVal.contains(expected); }
}
