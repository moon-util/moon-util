package com.moon.redis;

/**
 * @author moonsky
 */
public enum ExceptionStrategy implements ExceptionHandler {
    /**
     * 忽略
     */
    IGNORE,
    /**
     * 打印消息到控制台
     */
    MESSAGE {
        @Override
        public void onException(Exception ex) { System.err.println(ex.getMessage()); }
    },
    /**
     * 打印堆栈信息到控制台
     */
    PRINT {
        @Override
        public void onException(Exception ex) { ex.printStackTrace(); }
    },
    /**
     * 抛出异常
     */
    THROW {
        @Override
        public void onException(Exception ex) { throw new RuntimeException(ex); }
    };

    @Override
    public void onException(Exception ex) {}
}
