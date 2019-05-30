package com.moon.core.net;

import com.moon.core.util.interfaces.EndSupplier;
import com.moon.core.net.enums.RequestMethod;

import java.net.URL;
import java.util.Objects;

/**
 * @author benshaoye
 */
public abstract class HttpConnector {

    private final RequestMethod method;

    private final URL url;

    /*
     * ----------------------------------------------------------------------
     * constructor
     * ----------------------------------------------------------------------
     */

    public HttpConnector(String url) {
        this(url, RequestMethod.GET);
    }

    public HttpConnector(String url, RequestMethod method) {
        this(URLUtil.ofHttps(url), method);
    }

    public HttpConnector(URL url) {
        this(url, RequestMethod.GET);

    }

    public HttpConnector(URL url, RequestMethod method) {
        this.method = method;
        this.url = url;
    }

    /*
     * ----------------------------------------------------------------------
     * setting
     * ----------------------------------------------------------------------
     */

    public HeaderAddr configHeaders() {
        return new HeaderAddr(this);
    }

    public static class HeaderAddr implements EndSupplier<HttpConnector> {

        private final HttpConnector connector;

        private HeaderAddr(HttpConnector connector) {
            this.connector = Objects.requireNonNull(connector);
        }

        @Override
        public HttpConnector end() {
            return connector;
        }
    }
}
