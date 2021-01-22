package com.moon.accessor.transaction;

import java.sql.Connection;

/**
 * @author benshaoye
 */
public enum TransactionIsolationLevel {
    NONE(Connection.TRANSACTION_NONE),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    public final int level;

    TransactionIsolationLevel(int level) { this.level = level; }

    public int getLevel() { return level; }
}
