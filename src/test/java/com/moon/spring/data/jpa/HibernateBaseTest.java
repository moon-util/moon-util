package com.moon.spring.data.jpa;

import com.moon.core.io.IOUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.annotation.AnnotationUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.function.ThrowingConsumer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import javax.persistence.Entity;
import java.util.*;
import java.util.function.Supplier;

import static com.moon.core.util.IteratorUtil.forEach;

/**
 * @author moonsky
 */
public abstract class HibernateBaseTest extends MySQLDatabaseBaseTest implements ISystemOut {

    private final static Class[] classes = new Class[0];

    private Configuration config;
    private SessionFactory factory;

    protected Class[] getClasses() {
        return classes;
    }

    protected void initializeFactory() {
        initializeFactory(new Class[0]);
    }

    protected void initializeFactory(Class... classes) {
        initializeFactory(null, classes);
    }

    @Override
    protected String getSchema() {
        Map<String, Object> props = factory.getProperties();
        String url = String.valueOf(props.get(Environment.URL));
        int last = url.indexOf('?');
        last = last < 0 ? url.length() : last;
        int start = url.lastIndexOf('/', last);
        return url.substring(start + 1, last);
    }

    protected String getTableName(Class targetEntity) {
        Entity entity = AnnotationUtil.get(targetEntity, Entity.class);
        String name = entity.name();
        if (StringUtil.isEmpty(name)) {
            return targetEntity.getSimpleName();
        }
        return name;
    }

    @Override
    protected SessionFactory getSessionFactory() { return factory; }

    protected void initializeFactory(String configLocation, Class... classes) {
        config = new Configuration();
        if (StringUtil.isBlank(configLocation)) {
            config.configure();
        } else {
            config.configure(configLocation);
        }
        addTarget(config, classes);
        addTarget(config, getClasses());
        factory = config.buildSessionFactory();
    }

    protected void destroy() {
        IOUtil.close(factory);
    }

    protected SessionFactory getFactory() {
        return factory;
    }

    protected void autoSession(ThrowingConsumer<? super Session>... consumers) {
        for (ThrowingConsumer<? super Session> consumer : consumers) {
            try (Session session = getFactory().openSession()) {
                consumer.accept(session);
            } catch (Throwable t) {
                throw new IllegalStateException(t.getMessage(), t);
            }
        }
    }

    protected void autoTransaction(Session session, ThrowingRunner runner) {
        Transaction tx = session.beginTransaction();
        try {
            runner.run();
            tx.commit();
        } catch (Throwable t) {
            tx.rollback();
            throw new IllegalStateException(t.getMessage(), t);
        }
    }

    protected <T> void autoInsert(T... ts) {
        autoSession(session -> autoTransaction(session, () -> forEach(ts, (t, idx) -> {
            System.out.println(">>>> begin");
            session.save(t);
            System.out.println(String.format(">>>> end --------<<%d>>", idx));
        })));
    }

    protected <T> void autoInsert(Collection<T> ts) {
        autoSession(session -> autoTransaction(session, () -> forEach(ts, (t, idx) -> {
            System.out.println(">>>> begin");
            session.save(t);
            System.out.println(String.format(">>>> end --------<<%d>>", idx));
        })));
    }

    protected <T> List<T> createList(int count, Supplier<T> supplier) {
        return createList(count, ArrayList::new, supplier);
    }

    protected <T> List<T> createList(int count, Supplier<List<T>> listCreator, Supplier<T> supplier) {
        List<T> list = listCreator.get();
        IteratorUtil.forEach(count, idx -> list.add(supplier.get()));
        return list;
    }

    protected <T> List<T> createList(T... ts) {
        return ListUtil.newList(ts);
    }

    protected <T> Set<T> createSet(int count, Supplier<T> supplier) {
        return createSet(count, HashSet::new, supplier);
    }

    protected <T> Set<T> createSet(int count, Supplier<Set<T>> setCreator, Supplier<T> supplier) {
        Set<T> list = setCreator.get();
        IteratorUtil.forEach(count, idx -> list.add(supplier.get()));
        return list;
    }

    protected <T> Set<T> createSet(T... ts) {
        return SetUtil.newLinkedHashSet(ts);
    }

    private static void addTarget(Configuration config, Class... classes) {
        IteratorUtil.forEach(classes, cls -> {
            if (cls.getAnnotation(Entity.class) == null) {
                config.addClass(cls);
            } else {
                config.addAnnotatedClass(cls);
            }
        });
    }
}