package com.moon.accessor;

import com.moon.accessor.util.String2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.moon.accessor.util.String2.firstWord;

/**
 * @author benshaoye
 */
public class SQL {

    private final static String INSERT = "INSERT";
    private final static String UPDATE = "UPDATE";
    private final static String SELECT = "SELECT";
    private final static String DELETE = "DELETE";

    private final StringBuilder builder = new StringBuilder();
    private List<Object> parameters;

    public SQL() { }

    private List<Object> ensureParameters() { return parameters == null ? (parameters = new ArrayList<>()) : parameters; }

    public static SQL of() { return new SQL(); }

    public static SQL insert(CharSequence script) { return ofStartsWith(script, INSERT); }

    public static SQL update(CharSequence script) { return ofStartsWith(script, UPDATE); }

    public static SQL select(CharSequence script) { return ofStartsWith(script, SELECT); }

    public static SQL delete(CharSequence script) { return ofStartsWith(script, DELETE); }

    public static SQL ofStartsWith(CharSequence script, String leading) {
        if (leading.equalsIgnoreCase(firstWord(script))) {
            return of().add(script);
        }
        return of().add(leading).add(" ").add(script);
    }

    public SQL add(CharSequence script) {
        builder.append(script);
        return this;
    }

    public SQL add(CharSequence script, Object value) {
        builder.append(script);
        ensureParameters().add(value);
        return this;
    }

    public SQL add(CharSequence script, Supplier<?> supplier) {
        return add(script, supplier.get());
    }

    public SQL addIf(CharSequence script, Object value, boolean doJoined) {
        return doJoined ? add(script, value) : this;
    }

    public SQL addIf(CharSequence script, Supplier<?> supplier, boolean doJoined) {
        return doJoined ? add(script, supplier.get()) : this;
    }

    public SQL addInCollect(CharSequence columnLike, Collection<?> values) {
        String questionMark = String2.joinQuestionMark(values.size(), "(", ")");
        add(columnLike).add(questionMark);
        ensureParameters().addAll(parameters);
        return this;
    }

    public SQL addInCollectIf(CharSequence columnLike, Collection<?> values, boolean doJoined) {
        return doJoined ? addInCollect(columnLike, values) : this;
    }
}
