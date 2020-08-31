package com.moon.data.jpa;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.function.ThrowingConsumer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public abstract class MySQLDatabaseBaseTest {

    protected abstract String getSchema();

    protected abstract SessionFactory getSessionFactory();

    final static String TABLE = "SELECT `TABLE_NAME` `tableName` FROM `information_schema`.`tables` WHERE `table_schema`=?";

    final static String COLUMN = "SELECT DISTINCT `TABLE_NAME` `tableName`, `COLUMN_NAME` `columnName`, `COLUMN_TYPE` `columnType` FROM `information_schema`.`columns` WHERE `table_name`=?";

    protected void autoSession(ThrowingConsumer<? super Session>... consumers) {
        SessionFactory factory = getSessionFactory();
        IteratorUtil.forEach(consumers, consumer -> {
            try (Session session = factory.openSession()) {
                consumer.accept(session);
            } catch (Throwable t) {
                throw new IllegalStateException(t);
            }
        });
    }

    protected void assertExistsTable(String tableName) {
        autoSession(session -> {
            NativeQuery query = session.createSQLQuery(TABLE);
            query.setParameter(1, getSchema());
            Stream<String> stream = query.list().stream().map(String::valueOf);
            Set<String> results = stream.collect(Collectors.toSet());
            assertTrue(results.contains(tableName));
        });
    }

    protected void assertTableColumnsCount(String tableName, int count) {
        consume(tableName,
            settings -> assertEquals(count,
                settings.size(),
                () -> format("数据表列数不匹配：\n\tExcepted : %d\n\tActual   : %d (%s)\n",
                    count,
                    settings.size(),
                    JoinerUtil.join(settings.keySet(), ", "))));
    }

    protected void assertTableColumn(String tableName, String columnName) {
        consume(tableName,
            settings -> assertTrue(settings.containsKey(columnName),
                () -> format("不存在指定列: %s.%s", tableName, columnName)));
    }

    protected void assertTableColumnTypeIs(String tableName, String columnName, String columnType) {
        consume(tableName, settings -> {
            String type = settings.get(columnName);
            assertNotNull(type, () -> format("不存在指定列: %s.%s\n", tableName, columnName));
            assertEquals(columnType, type, () -> format("类型不匹配：\n\tExcepted : %s\n\tActual   : %s\n", columnType, type));
        });
    }

    protected void assertTableColumnTypeLike(String tableName, String columnName, String columnType) {
        consume(tableName, settings -> {
            String type = settings.get(columnName);
            assertNotNull(type, () -> format("不存在指定列: %s.%s\n", tableName, columnName));
            assertTrue(type.contains(columnType),
                () -> format("类型不匹配：\n\tExcepted : %s\n\tActual   : %s\n", columnType, type));
        });
    }

    protected void autoQuery(CharSequence sql, Object... params) {
        autoSession(session -> {
            NativeQuery query = session.createSQLQuery(sql.toString());
            IteratorUtil.forEach(params, (param,idx) -> {
                query.setParameter(idx + 1, param);
            });

        });
    }

    private void consume(String tableName, ThrowingConsumer<Map<String, String>> consumer) {
        autoSession(session -> {
            NativeQuery query = session.createSQLQuery(COLUMN);
            query.setParameter(1, tableName);
            List list = query.list();
            Map<String, String> columnsSettings = new LinkedHashMap<>();
            for (Object current : list) {
                if (current instanceof Object[]) {
                    int index = 0;
                    Object[] array = (Object[]) current;
                    Object tbName = array[index++];
                    Object colName = array[index++];
                    Object colType = array[index++];
                    columnsSettings.put(String.valueOf(colName), String.valueOf(colType));
                }
            }
            consumer.accept(columnsSettings);
        });
    }
}
