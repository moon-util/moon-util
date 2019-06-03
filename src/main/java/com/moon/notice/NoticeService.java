package com.moon.notice;

import com.moon.core.lang.ArrayUtil;

/**
 * @author benshaoye
 */
public interface NoticeService {

    /**
     * 批量发送短信
     *
     * @param targetIdentifiers
     * @param templateCode
     * @param parameters
     */
    void send(String[] targetIdentifiers, String templateCode, NoticeParameters parameters);

    /**
     * 发送短信
     *
     * @param targetIdentifier
     * @param templateCode
     * @param parameters
     */
    default void send(String targetIdentifier, String templateCode, NoticeParameters parameters) {
        send(ArrayUtil.toArray(targetIdentifier), templateCode, parameters);
    }
}
