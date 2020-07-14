package com.moon.spring.web.advice;

import com.moon.core.json.JSON;
import com.moon.core.util.MapUtil;
import com.moon.more.web.ContentTypeUtil;
import com.moon.spring.jpa.identity.SnowflakeLongIdentifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class WebError {

    final static transient SnowflakeLongIdentifier worker = SnowflakeLongIdentifier.of(29, 29);

    private final long serial;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final WebError prev;

    private final Throwable ex;

    public WebError(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
        this.serial = nextSerial();
        this.response = response;
        this.request = request;
        this.prev = null;
        this.ex = ex;
    }

    private WebError(WebError prev, Throwable cause) {
        this.serial = prev.serial;
        this.response = prev.response;
        this.request = prev.request;
        this.prev = prev;
        this.ex = cause;
    }

    public boolean isAcceptHtml() { return ContentTypeUtil.isAcceptHtml(request); }

    public Class<? extends Throwable> getThrowableType() { return ex.getClass(); }

    public Throwable getCause() { return ex.getCause(); }

    public WebError getCauseError() {
        return ex.getCause() == null ? null : new WebError(this, ex.getCause());
    }

    public long getSerial() { return serial; }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Throwable getEx() {
        return ex;
    }

    public static long nextSerial() {
        return worker.nextId();
    }

    public static String nextSerialAsString() {
        return String.valueOf(worker.nextId());
    }

    @Override
    public String toString() {
        HttpServletRequest request = this.request;
        String url = request.getServletPath();
        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return new ToStrBuilder(serial, url, headers, parameters).toString();
    }

    private static class ToStrBuilder {

        private final long serial;
        private final String url;
        private final Map<String, String> headers;
        private final Map<String, String[]> parameters;

        private ToStrBuilder(
            long serial, String url, Map<String, String> headers, Map<String, String[]> parameters
        ) {
            this.parameters = parameters;
            this.headers = headers;
            this.serial = serial;
            this.url = url;
        }

        public long getSerial() { return serial; }

        public String getUrl() { return url; }

        public Map<String, String> getHeaders() { return MapUtil.emptyIfNull(headers); }

        public Map<String, String[]> getParameters() { return MapUtil.emptyIfNull(parameters); }

        @Override
        public String toString() {
            return JSON.stringify(this);
        }
    }
}
