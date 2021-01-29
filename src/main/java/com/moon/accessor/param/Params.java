package com.moon.accessor.param;

import com.moon.accessor.function.ThrowingIntBiConsumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * @author benshaoye
 */
public final class Params extends ArrayList<ParamSetter> {

    public Params() { }

    public static Params of() { return new Params(); }

    public Params with(Integer value) {
        int index = size() + 1;
        if (value == null) {
            new ParamSetter() {
                @Override
                public void setParameter(PreparedStatement stmt) throws SQLException {
                    stmt.setNull(index, Types.INTEGER);
                }
            };
        } else {
            new ParamSetter() {
                @Override
                public void setParameter(PreparedStatement stmt) throws SQLException {
                    stmt.setInt(index, value);
                }
            };
        }
        return this;
    }

    public interface ThrowingIntTableConsumer<T1, T2> {

        void accept(T1 t1, T2 t2, int intValue) throws SQLException;
    }

    // private static class Param<T> implements ParamSetter {
    //
    //     /**
    //      * 从 1 开始
    //      */
    //     private final int parameterIndex;
    //     private final ThrowingIntTableConsumer<PreparedStatement, > consumer;
    //     private final T value;
    //
    //     private Param(
    //         int parameterIndex, T value, ThrowingIntTableConsumer consumer
    //     ) {
    //         this.parameterIndex = parameterIndex;
    //         this.value = value;
    //         this.consumer = consumer;
    //     }
    //
    //     @Override
    //     public void setParameter(PreparedStatement stmt) {
    //         // consumer.accept();
    //     }
    // }
}
