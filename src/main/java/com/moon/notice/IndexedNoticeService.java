package com.moon.notice;

import com.moon.core.lang.ArrayUtil;

/**
 * @author benshaoye
 */
public interface IndexedNoticeService extends NoticeService {

    /**
     * 批量发送短信
     *
     * @param targetIdentifiers
     * @param templateCode
     * @param parameters
     */
    void send(String[] targetIdentifiers, String templateCode, String... parameters);

    /**
     * 发送短信
     *
     * @param targetIdentifiers
     * @param templateCode
     * @param parameters
     */
    default void send(String targetIdentifiers, String templateCode, String... parameters) {
        send(ArrayUtil.toArray(targetIdentifiers), templateCode, parameters);
    }

    /**
     * 批量发送短信
     *
     * @param targetIdentifiers
     * @param templateCode
     * @param parameters
     */
    @Override
    default void send(String[] targetIdentifiers, String templateCode, NoticeParameters parameters) {
        send(targetIdentifiers, templateCode, parameters.toArray());
    }
}
