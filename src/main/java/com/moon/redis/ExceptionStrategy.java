package com.moon.redis;

/**
 * @author benshaoye
 */
public enum ExceptionStrategy implements ExceptionHandler {
    IGNORE,
    MESSAGE {
        @Override
        public void onException(Exception ex) { System.err.println(ex.getMessage()); }
    },
    PRINT {
        @Override
        public void onException(Exception ex) { ex.printStackTrace(); }
    },
    THROW {
        @Override
        public void onException(Exception ex) { throw new RuntimeException(ex); }
    };

    @Override
    public void onException(Exception ex) {}
}
