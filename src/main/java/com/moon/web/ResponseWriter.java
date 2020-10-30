package com.moon.web;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * @author moonsky
 */
public final class ResponseWriter {

    private final HttpServletResponse response;

    public ResponseWriter(HttpServletResponse response) { this.response = response; }

    /**
     * 设置响应头内容格式
     *
     * @param type 响应内容格式
     *
     * @return 当前对象
     */
    public ResponseWriter contentTypeOf(String type) {
        response.setContentType(type);
        return this;
    }

    public ResponseWriter contentAsJson() { return contentTypeOf("application/json"); }

    public ResponseWriter contentAsHtml() { return contentTypeOf("text/html"); }

    public ResponseWriter contentAsText() { return contentTypeOf("text/plain"); }

    public ResponseWriter setHeader(String name, String value) {
        response.setHeader(name, value);
        return this;
    }

    public ResponseWriter addCookie(String name, String value) {
        CookieUtil.set(response, name, value);
        return this;
    }

    public ResponseWriter removeCookie(String name) {
        CookieUtil.remove(response, name);
        return this;
    }

    public ResponseWriter charset(String charset) {
        response.setCharacterEncoding(charset);
        return this;
    }

    public ResponseWriter charset(Charset charset) { return charset(charset.name()); }

    public ResponseWriter charsetUtf8() { return charset(StandardCharsets.UTF_8); }

    public ResponseWriter status(int status) {
        response.setStatus(status);
        return this;
    }

    public ResponseWriter status200() { return status(200); }

    public ResponseWriter status400() { return status(400); }

    public ResponseWriter status401() { return status(401); }

    public ResponseWriter status500() { return status(500); }

    public ResponseWriter ok() { return status200(); }

    public ResponseWriter config(Consumer<HttpServletResponse> configurer) {
        configurer.accept(response);
        return this;
    }

    public void write(CharSequence data) {
        HttpServletResponse response = this.response;
        try (PrintWriter writer = response.getWriter()) {
            writer.write(data == null ? null : data.toString());
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    public void write(ConsumerWriter writer) {
        HttpServletResponse response = this.response;
        try (PrintWriter w = response.getWriter()) {
            writer.accept(w);
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    public void writeStream(ConsumerStream writer) {
        HttpServletResponse response = this.response;
        try (OutputStream o = response.getOutputStream()) {
            writer.accept(o);
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }
}
