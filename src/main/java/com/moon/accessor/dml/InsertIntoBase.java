package com.moon.accessor.dml;

import com.moon.accessor.config.Configuration;
import com.moon.accessor.config.ConnectionFactory;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.accessor.util.Math2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author benshaoye
 */
abstract class InsertIntoBase<R, TB extends Table<R, TB>> extends TableFieldsHolder<R, TB>
    implements InsertInto<R, TB> {

    private final Configuration config;
    private final TB table;

    @SafeVarargs
    InsertIntoBase(Configuration config, TB table, TableField<?, R, TB>... fields) {
        super(fields);
        this.config = config;
        this.table = table;
    }

    protected final Configuration getConfig() { return config; }

    public TB getTable() { return table; }

    protected final int doInsert(List<Object[]> values) {
        ConnectionFactory factory = getConfig().getConnectionFactory();
        return doInsert(factory, getTable(), getFields(), values);
    }

    private static <R, TB extends Table<R, TB>> int doInsert(
        ConnectionFactory factory, TB table, TableField<?, R, TB>[] fields, List<Object[]> valuesAll
    ) {
        String placeholders = Placeholders.find(fields.length);
        String fieldsSerial = joinMapped(fields, TableField::getColumnName);
        String insert = toInsertSQL(table.getTableName(), fieldsSerial, placeholders);
        return factory.use(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                if (valuesAll.size() == 1) {
                    return executeInsert(statement, valuesAll.get(0));
                }
                return executeInsert(statement, valuesAll);
            }
        });
    }

    private static int executeInsert(PreparedStatement statement, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
        return statement.executeUpdate();
    }

    private static int executeInsert(PreparedStatement statement, List<Object[]> valuesAll) throws SQLException {
        for (Object[] values : valuesAll) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            statement.addBatch();
        }
        return Math2.sum(statement.executeBatch());
    }

    private static String toInsertSQL(String tableName, String fieldsSerial, String placeholders) {
        StringBuilder insertSQL = new StringBuilder("INSERT INTO ").append(tableName);
        insertSQL.append('(').append(fieldsSerial).append(')');
        insertSQL.append(" VALUES (").append(placeholders).append(')');
        return insertSQL.toString();
    }

    private static <T> String joinMapped(T[] ts, Function<T, String> mapper) {
        int length = ts.length;
        String[] values = new String[length];
        for (int i = 0; i < length; i++) {
            values[i] = mapper.apply(ts[i]);
        }
        return String.join(",", values);
    }

    private static class Placeholders {

        private final static Map<Integer, String> CACHED = new HashMap<>();

        public static String find(Integer length) {
            String placeholder = CACHED.get(length);
            if (placeholder == null) {
                String[] chars = new String[length];
                Arrays.fill(chars, "?");
                placeholder = String.join(",", chars);
                // 没必要用 ConcurrentHashMap，它每次读都比 HashMap 慢
                // 但是加锁只有第一次放入时慢点
                synchronized (CACHED) {
                    CACHED.put(length, placeholder);
                }
            }
            return placeholder;
        }
    }
}
