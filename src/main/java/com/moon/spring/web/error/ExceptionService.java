package com.moon.spring.web.error;

import com.moon.core.enums.Maps;
import com.moon.core.lang.ClassUtil;
import com.moon.spring.web.SpringWebUtil;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public class ExceptionService {

    private final static Map<String, RestExceptionHandler> CACHE = new HashMap<>();

    final static void doRegistry(String classname, RestExceptionHandler handler) {
        CACHE.put(classname, handler);
    }

    private final Map<String, RestExceptionHandler> handlerMap;

    public ExceptionService() { this((Supplier) Maps.HashMaps); }

    public ExceptionService(Supplier<Map<String, RestExceptionHandler>> supplier) {
        this(supplier.get());
        this.handlerMap.putAll(CACHE);
    }

    public ExceptionService(Map<String, RestExceptionHandler> handlers) {
        this.handlerMap = Objects.requireNonNull(handlers);
    }

    /**
     * 注册异常处理器
     *
     * @param exceptionType 处理的异常类型
     * @param handler       异常处理器
     *
     * @see RestExceptionEnum
     */
    public void registry(Class exceptionType, RestExceptionHandler handler) {
        registry(exceptionType.getName(), handler);
    }

    /**
     * 注册异常处理器
     *
     * @param exceptionFullName 处理的异常名称
     * @param handler           异常处理器
     *
     * @see RestExceptionEnum
     */
    public void registry(String exceptionFullName, RestExceptionHandler handler) {
        handlerMap.put(exceptionFullName, handler);
    }

    /**
     * 如果不存在指定类型异常处理器，就注册异常处理器
     *
     * @param exceptionFullName 处理的异常名称
     * @param handler           异常处理器
     *
     * @see RestExceptionEnum
     */
    public void registryIfAbsent(String exceptionFullName, RestExceptionHandler handler) {
        handlerMap.putIfAbsent(exceptionFullName, handler);
    }

    /**
     * 删除一个处理器
     *
     * @param exceptionType
     */
    public void remove(Object exceptionType) {
        if (exceptionType instanceof CharSequence) {
            String type = exceptionType.toString();
            handlerMap.remove(type);
            handlerMap.remove(ClassUtil.forNameOrNull(type));
        } else if (exceptionType instanceof Class) {
            Class type = (Class) exceptionType;
            handlerMap.remove(type.getName());
            handlerMap.remove(type);
        } else {
            handlerMap.remove(exceptionType);
        }
    }

    /**
     * 查找异常处理器
     *
     * @param throwable 异常
     *
     * @return 异常处理器，不存在对应异常处理器时返回 null
     */
    public RestExceptionHandler findRestExceptionHandler(Throwable throwable) {
        return findRestExceptionHandler(throwable.getClass());
    }

    /**
     * 查找异常处理器
     *
     * @param exceptionType 异常类型
     *
     * @return 异常处理器，不存在对应异常处理器时返回 null
     */
    public RestExceptionHandler findRestExceptionHandler(Class exceptionType) {
        return findRestExceptionHandler(exceptionType.getName());
    }

    /**
     * 查找异常处理器
     *
     * @param exceptionName 异常名称
     *
     * @return 异常处理器，不存在对应异常处理器时返回 null
     */
    public RestExceptionHandler findRestExceptionHandler(String exceptionName) {
        return handlerMap.get(exceptionName);
    }


    /**
     * 处理全局异常
     *
     * @param root 异常对象
     *
     * @return ResponseEntity
     *
     * @throws Throwable 当不存在对应异常处理器或不能正确处理异常时，抛出原异常
     * @see SpringWebUtil#onThrowable(Throwable)
     */
    public ResponseEntity onThrowable(Throwable root) throws Throwable {
        ResponseEntity result = doThrowable(root);
        if (result == null) {
            throw root;
        }
        return result;
    }

    /**
     * 处理全局异常
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param root     异常对象
     *
     * @return ResponseEntity
     *
     * @throws Throwable 当不存在对应异常处理器或不能正确处理异常时，抛出原异常
     * @see MvcExceptionUtil
     * @see RestExceptionEnum
     * @see SpringWebUtil#onThrowable(HttpServletRequest, HttpServletResponse, Throwable)
     */
    public ResponseEntity onThrowable(HttpServletRequest request, HttpServletResponse response, Throwable root)
        throws Throwable {
        ResponseEntity result = doThrowable(request, response, root);
        if (result == null) {
            throw root;
        }
        return result;
    }

    private ResponseEntity doThrowable(Throwable ex) throws Throwable {
        Throwable cause;
        ResponseEntity result = null;
        if ((cause = ex.getCause()) != null) {
            result = doThrowable(cause);
        }
        if (result == null) {
            RestExceptionHandler handler = findRestExceptionHandler(ex);
            if (handler != null) {
                result = handler.onThrowable(ex);
            }
        }
        return result;
    }

    private ResponseEntity doThrowable(HttpServletRequest request, HttpServletResponse response, Throwable ex)
        throws Throwable {
        Throwable cause;
        ResponseEntity result = null;
        if ((cause = ex.getCause()) != null) {
            result = doThrowable(request, response, cause);
        }
        if (result == null) {
            RestExceptionHandler handler = findRestExceptionHandler(ex);
            if (handler != null) {
                result = handler.onThrowable(request, response, ex);
            }
        }
        return result;
    }
}
