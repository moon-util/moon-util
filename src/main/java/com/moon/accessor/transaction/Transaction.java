package com.moon.accessor.transaction;

import com.moon.accessor.exception.TransactionException;

/**
 * @author benshaoye
 */
public interface Transaction {

    /**
     * Commit this transaction.
     *
     * @throws TransactionException SQL exception
     */
    void commit() throws TransactionException;

    /**
     * Rollback this transaction.
     *
     * @throws TransactionException the SQL exception
     */
    void rollback() throws TransactionException;
}
