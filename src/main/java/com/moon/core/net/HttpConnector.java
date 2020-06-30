package com.moon.core.net;

import com.moon.core.net.enums.RequestMethod;
import com.moon.core.util.MapUtil;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @author moonsky
 */
public class HttpConnector {

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> params = new HashMap<>();

    private RequestMethod method;

    private URL url;

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
        setMethod(method);
        this.url = url;
    }

    public void setMethod(RequestMethod method) {
        this.method = requireNonNull(method);
    }

    public RequestMethod getMethod() { return method; }

    public Map<String, String> getHeaders() {
        return headers == null ? MapUtil.empty() : headers;
    }

    public Map<String, String> getParams() {
        return params == null ? MapUtil.empty() : params;
    }

    /*
     * ----------------------------------------------------------------------
     * setting
     * ----------------------------------------------------------------------
     */

    public HttpConnector header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpConnector param(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public void doGet() {
        doSend(RequestMethod.GET);
    }

    public void doPost() {
        doSend(RequestMethod.POST);
    }

    public void doPut() {
        doSend(RequestMethod.PUT);
    }

    public void doPatch() {
        doSend(RequestMethod.PATCH);
    }

    public void doDelete() {
        doSend(RequestMethod.DELETE);
    }

    public void doSend(HttpMethod method) {
        doSend(RequestMethod.valueOf(method.name()));
    }

    public void doSend(RequestMethod method) {
        try {
            URLConnection opened = url.openConnection();

            HttpURLConnection connect = (HttpURLConnection) opened;
            connect.setRequestMethod(method.getName());
            for (Map.Entry<String, String> en : headers.entrySet()) {
                connect.setRequestProperty(en.getKey(), en.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
