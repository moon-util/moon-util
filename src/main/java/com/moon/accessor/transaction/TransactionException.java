package com.moon.accessor.transaction;

import com.moon.accessor.exception.SqlException;

import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class TransactionException extends SqlException {

    public TransactionException(SQLException exception) {
    }
}
