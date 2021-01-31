package com.moon.accessor.param;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class BlobParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Blob value;

    public BlobParameterSetter(int parameterIndex, Blob value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setBlob(getParameterIndex(), value);
    }
}
