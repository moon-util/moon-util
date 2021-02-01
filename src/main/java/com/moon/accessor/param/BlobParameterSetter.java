package com.moon.accessor.param;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class BlobParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Blob value;

    public BlobParameterSetter(int parameterIndex, Blob value) {
        this(parameterIndex, Types.BLOB, value);
    }

    public BlobParameterSetter(int parameterIndex, int sqlType, Blob value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setBlob(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
