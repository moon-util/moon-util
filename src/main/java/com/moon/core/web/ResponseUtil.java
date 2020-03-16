package com.moon.core.web;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author benshaoye
 */
public final class ResponseUtil {

    public static void write200(HttpServletResponse response, String content) throws IOException {
        write(response, content, 200);
    }

    public static void write400(HttpServletResponse response, String content) throws IOException {
        write(response, content, 400);
    }

    public static void write401(HttpServletResponse response, String content) throws IOException {
        write(response, content, 401);
    }

    public static void write500(HttpServletResponse response, String content) throws IOException {
        write(response, content, 500);
    }

    public static void write(HttpServletResponse response, String content, int status) throws IOException {
        write(response, content, status, UTF_8);
    }

    public static void write(HttpServletResponse response, String content, int status, Charset charset)
        throws IOException { write(response, content, status, charset.name()); }

    public static void write(HttpServletResponse response, String content, int status, String charset)
        throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(charset);
        try (Writer writer = response.getWriter()) {
            writer.write(content);
        }
    }
}
