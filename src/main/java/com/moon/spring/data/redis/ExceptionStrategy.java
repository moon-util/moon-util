package com.moon.spring.data.redis;

import com.moon.core.util.logger.Logger;
import com.moon.core.util.logger.LoggerUtil;

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
     * 打印堆栈信息到日志
     */
    LOGGER_DEBUG {
        @Override
        public void onException(Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage(), ex);
            }
        }
    },
    /**
     * 打印堆栈信息到日志
     */
    LOGGER_INFO {
        @Override
        public void onException(Exception ex) {
            if (logger.isInfoEnabled()) {
                logger.info(ex.getMessage(), ex);
            }
        }
    },
    /**
     * 打印堆栈信息到日志
     */
    LOGGER_WARN {
        @Override
        public void onException(Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
        }
    },
    /**
     * 打印堆栈信息到日志
     */
    LOGGER_ERROR {
        @Override
        public void onException(Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex.getMessage(), ex);
            }
        }
    },
    /**
     * 抛出异常
     */
    THROW {
        @Override
        public void onException(Exception ex) { throw new MoonRedisAccessException(ex); }
    };

    private final static Logger logger = LoggerUtil.getLogger(RedisAccessor.class);

    @Override
    public void onException(Exception ex) {}
}
