package com.moon.accessor.dialect;

/**
 * 数据库关键字、保留字等
 *
 * @author benshaoye
 */
public interface Keyword {

    /**
     * 序号
     *
     * @return ordinal
     *
     * @see Enum#ordinal()
     */
    int ordinal();

    /**
     * 关键字名
     *
     * @return name
     *
     * @see Enum#name()
     */
    String name();


    enum MySQL implements Keyword {
        TRUE,
        FALSE,
        LIMIT,
        KILL,
        NAME,
        IDENTIFIED,
        PASSWORD,
        DUAL,
        BINARY,
        SHOW,
        REPLACE,
        MEMBER,
    }

    enum Default implements Keyword {
        SELECT,
        INSERT,
        UPDATE,
        DELETE,

        DISTINCT,
        FROM,
        WHERE,
        GROUP,
        HAVING,
        ORDER,
        BY,
        INTO,
        AS,
        SET,

        UNION,
        ALL,
        EXCEPT,
        INTERSECT,
        MINUS,
        INNER,
        LEFT,
        RIGHT,
        CROSS,
        FULL,
        OUTER,
        JOIN,
        ON,
        SCHEMA,
        CAST,
        COLUMN,
        USE,
        DATABASE,
        TO,

        AND,
        OR,
        XOR,
        CASE,
        WHEN,
        THEN,
        ELSE,
        END,
        EXISTS,
        IN,

        IS,
        NOT,
        NULL,

        NEW,
        ASC,
        DESC,
        LIKE,
        ESCAPE,
        BETWEEN,
        VALUES,
        INTERVAL,

        LOCK,
        SOME,
        ANY,
        TRUNCATE,

        CREATE,
        ALTER,
        DROP,

        PRIMARY,
        KEY,
        UNIQUE,
        FOREIGN,
        DEFAULT,
        REFERENCES,
        CHECK,
        CONSTRAINT,

        TABLE,
        TABLESPACE,
        VIEW,
        SEQUENCE,
        TRIGGER,
        USER,
        INDEX,
        SESSION,
        PROCEDURE,
        FUNCTION,

        EXPLAIN,
        FOR,
        IF,
        ;
    }
}
