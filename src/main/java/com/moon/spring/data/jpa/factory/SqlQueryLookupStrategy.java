package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.annotation.JdbcDelete;
import com.moon.spring.data.jpa.annotation.JdbcInsert;
import com.moon.spring.data.jpa.annotation.JdbcSelect;
import com.moon.spring.data.jpa.annotation.JdbcUpdate;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

import javax.persistence.EntityManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author moonsky
 */
public class SqlQueryLookupStrategy implements QueryLookupStrategy {

    private final QueryLookupStrategy lookupStrategy;
    private final QueryExtractor extractor;
    private final EntityManager em;
    private final JpaRecordRepositoryMetadata metadata;

    public SqlQueryLookupStrategy(
        QueryLookupStrategy lookupStrategy,
        EntityManager em,
        QueryExtractor extractor,
        JpaRecordRepositoryMetadata metadata
    ) {
        this.metadata = metadata;
        this.lookupStrategy = lookupStrategy;
        this.extractor = extractor;
        this.em = em;
    }

    public JpaRecordRepositoryMetadata getRepositoryContextMetadata() { return metadata; }

    @Override
    public RepositoryQuery resolveQuery(
        Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries
    ) {
        try {
            return lookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
        } catch (RuntimeException e) {
            return resolveJdbcQuery(method, metadata, factory, namedQueries, e);
        }
    }

    private RepositoryQuery resolveJdbcQuery(
        Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries queries, Exception e
    ) {
        JdbcAttributes attrs = resolveJdbcAttributes(method, e);
        JpaRecordRepositoryMetadata ctxMetadata = getRepositoryContextMetadata();
        if (RepositoryUtil.isPresentJdbcTemplateBean(ctxMetadata)) {
            return new RecordJdbcRepositoryQuery(attrs, method, metadata, factory, extractor, em, ctxMetadata);
        }
        return new RecordJpaRepositoryQuery(attrs, method, metadata, factory, extractor, em, ctxMetadata);
    }

    private JdbcAttributes resolveJdbcAttributes(Method method, Exception e) {
        IntAddr addr = new IntAddr();
        JdbcSelect jdbcSelect = addr.obtainAnnotation(method, JdbcSelect.class);
        JdbcDelete jdbcDelete = addr.obtainAnnotation(method, JdbcDelete.class);
        JdbcInsert jdbcInsert = addr.obtainAnnotation(method, JdbcInsert.class);
        JdbcUpdate jdbcUpdate = addr.obtainAnnotation(method, JdbcUpdate.class);
        if (addr.not(1)) {
            String names = Stream.of(JdbcSelect.class, JdbcDelete.class, JdbcInsert.class, JdbcUpdate.class)
                .map(it -> '@' + it.getSimpleName() + "(..)")
                .collect(Collectors.joining("\n\t\t\t"));
            throw new IllegalStateException("方法" + method.getDeclaringClass()
                .getSimpleName() + '.' + method.getName() + "(..) 只能注解其中一个：" + names + "\n");
        }
        if (jdbcSelect != null) {
            return JdbcAttributes.of(jdbcSelect);
        }
        if (jdbcDelete != null) {
            return JdbcAttributes.of(jdbcDelete);
        }
        if (jdbcInsert != null) {
            return JdbcAttributes.of(jdbcInsert);
        }
        if (jdbcUpdate != null) {
            return JdbcAttributes.of(jdbcUpdate);
        }
        return requireAnnotated(true, method, e);
    }

    private static class IntAddr {

        private int value = 0;

        public <E extends Annotation> E obtainAnnotation(AnnotatedElement elem, Class<E> annotationType) {
            E data = getAnnotation(elem, annotationType);
            if (data != null) {
                value++;
            }
            return data;
        }

        public boolean not(int expected) {
            return value != expected;
        }
    }

    private static <A extends Annotation> A getAnnotation(AnnotatedElement elem, Class<A> annotationType) {
        A annotation = elem.getAnnotation(annotationType);
        return annotation == null ? elem.getDeclaredAnnotation(annotationType) : annotation;
    }

    final static String MSG_TEMPLATE = "解析错误：{}, 请检查语法或使用注解 @" + JdbcSelect.class.getSimpleName() + "(..).";

    private JdbcAttributes requireAnnotated(boolean doThrow, Method method, Exception e) {
        if (doThrow) {
            String message = method.getDeclaringClass().getSimpleName() + "#" + method.getName();
            throw new IllegalArgumentException(MSG_TEMPLATE.replace("{}", message), e);
        }
        return null;
    }
}
