package com.moon.spring.web.advice;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.CollectUtil;
import com.moon.core.util.ListUtil;
import com.moon.more.model.KeyValue;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Collection;
import java.util.List;

/**
 * @author moonsky
 */
public final class ErrorUtil {

    private final static char DOT = '.';

    private static List<KeyValue> sorted(List<KeyValue> list) {
        list.sort((o1, o2) -> {
            String k1 = o1.getKey(), k2 = o2.getKey();
            int v = StringUtil.countOf(k1, DOT) - StringUtil.countOf(k2, DOT);
            return v == 0 ? k1.compareTo(k2) : v;
        });
        return list;
    }

    public static List<KeyValue> forFieldErrors(Collection<FieldError> errors) {
        List<KeyValue> result = ListUtil.newList();
        if (CollectUtil.isNotEmpty(errors)) {
            for (FieldError error : errors) {
                result.add(KeyValue.of(error.getField(), error.getDefaultMessage()));
            }
        }
        return sorted(result);
    }

    public static List<KeyValue> forObjectErrors(Collection<ObjectError> errors) {
        List<KeyValue> result = ListUtil.newList();
        if (CollectUtil.isNotEmpty(errors)) {
            for (ObjectError error : errors) {
                if (error instanceof FieldError) {
                    FieldError err = (FieldError) error;
                    result.add(KeyValue.of(err.getField(), err.getDefaultMessage()));
                } else {
                    result.add(KeyValue.of(error.getObjectName(), error.getDefaultMessage()));
                }
            }
        }
        return sorted(result);
    }

    public static List<KeyValue> forErrors(
        Collection<FieldError> errs, Collection<ObjectError> errors
    ) {
        List<KeyValue> result = forFieldErrors(errs);
        return result.isEmpty() ? forObjectErrors(errors) : result;
    }

    public static List<KeyValue> forErrorsByBindingResult(BindingResult result) {
        return forErrors(result.getFieldErrors(), result.getAllErrors());
    }
}
