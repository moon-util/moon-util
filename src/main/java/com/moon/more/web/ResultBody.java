package com.moon.more.web;

import java.util.Objects;

/**
 * Web 返回值包装
 * <p>
 * 其实更建议直接使用{@code http}状态码
 *
 * @author moonsky
 */
public class ResultBody<T> {

    private Boolean status;
    private int code;
    private T data;
    private Object extra;
    private String message;

    public static <E> ResultBody<E> ok() { return new ResultBody<>(); }

    public static <E> ResultBody<E> ok(E data) {
        return (ResultBody<E>) ok().data(data);
    }

    public static <E> ResultBody<E> ok(E data, String message) {
        return ok(data).message(message);
    }

    public static <E> ResultBody<E> fail() {
        return (ResultBody<E>) ok().status(false);
    }

    public static <E> ResultBody<E> fail(int code) {
        return (ResultBody<E>) fail().code(code);
    }

    public static <E> ResultBody<E> fail(String message) {
        return (ResultBody<E>) fail().message(message);
    }

    public static <E> ResultBody<E> fail(int code, String message) {
        return (ResultBody<E>) fail(code).message(message);
    }

    public static <E> ResultBody<E> fail(String message, E data) {
        return (ResultBody<E>) fail(message).data(data);
    }

    public static <E> ResultBody<E> fail(int code, String message, E data) {
        return (ResultBody<E>) fail(code, message).data(data);
    }

    public ResultBody<T> status(Boolean status) {
        this.status = status == null ? true : status;
        return this;
    }

    public ResultBody<T> code(int code) {
        setCode(code);
        return this;
    }

    public ResultBody<T> data(T data) {
        setData(data);
        return this;
    }

    public ResultBody<T> extra(Object extra) {
        setExtra(extra);
        return this;
    }

    public ResultBody<T> message(String message) {
        setMessage(message);
        return this;
    }

    public boolean getStatus() { return status == null ? true : status; }

    public void setStatus(Boolean status) { this.status = status; }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public T getData() { return data; }

    public void setData(T data) { this.data = data; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ResultBody<?> body = (ResultBody<?>) o;
        return code == body.code && Objects.equals(status, body.status) && Objects.equals(data,
            body.data) && Objects.equals(extra, body.extra) && Objects.equals(message, body.message);
    }

    @Override
    public int hashCode() { return Objects.hash(status, code, data, extra, message); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResultBody{");
        sb.append("status=").append(status);
        sb.append(", code=").append(code);
        sb.append(", data=").append(data);
        sb.append(", extra=").append(extra);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
