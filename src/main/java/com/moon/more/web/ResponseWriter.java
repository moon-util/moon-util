package com.moon.more.web;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public final class ResponseWriter {

    private final HttpServletResponse response;

    public ResponseWriter(HttpServletResponse response) { this.response = response; }

    public ResponseWriter contentType(String type) {
        response.setContentType(type);
        return this;
    }

    public ResponseWriter contentAsJson() { return contentType("application/json"); }

    public ResponseWriter contentAsHtml() { return contentType("text/html"); }

    public ResponseWriter contentAsText() { return contentType("text/plain"); }

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

    public ResponseWriter config(Consumer<HttpServletResponse> configurer) {
        configurer.accept(response);
        return this;
    }

    public void write(String data) {
        HttpServletResponse response = this.response;
        try (PrintWriter writer = response.getWriter()) {
            writer.write(data);
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

    public void write(ConsumerStream writer) {
        HttpServletResponse response = this.response;
        try (OutputStream o = response.getOutputStream()) {
            writer.accept(o);
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }
}
