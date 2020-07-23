package com.moon.spring.web.error;

import java.util.Map;

/**
 * @author moonsky
 */
class ExceptionCache {

    private final static ExceptionService SERVICE = newExceptionService();

    /**
     * 返回一个异常处理服务
     *
     * @return 异常处理服务
     *
     * @see ExceptionService#ExceptionService(Map) 注意区别，这个方法并没有默认处理器
     */
    public final static ExceptionService newExceptionService() { return new ExceptionService(); }

    /**
     * 返回默认全局异常处理服务
     * <p>
     * 由于这个异常处理服务设计场景是用于处理全局异常，所以假设了用户并不会随意到处进行增删相关操作，
     * 这里获取到的始终是单例，增删的时候请谨慎操作，如果需要自定义请使用{@link #newExceptionService()}
     *
     * @return 单例
     *
     * @see ExceptionService#ExceptionService(Map) 注意区别，这个方法并没有默认处理器
     */
    public final static ExceptionService getDefaultInstance() { return SERVICE; }
}
