package com.moon.data.jpa.repository;

import com.moon.core.enums.Placeholder;
import com.moon.core.util.ListUtil;
import com.moon.data.DataRecord;
import com.moon.data.jpa.DataRepository;
import com.moon.data.jpa.JpaRecord;
import com.moon.data.registry.LayerRegistry;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder.getPredicate;

/**
 * @author moonsky
 */
@NoRepositoryBean
public class DataRepositoryImpl<T extends JpaRecord<String>> extends SimpleJpaRepository<T, String>
    implements DataRepository<T> {

    final static CacheManager NO_OP = new NoOpCacheManager();

    final static Placeholder PLACEHOLDER = Placeholder.DEFAULT;

    private final CacheManager cacheManager = NO_OP;

    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    private final Function<String, Object> NULL_FN = o -> null;

    private final Class domainClass;
    private final EntityManager em;

    public DataRepositoryImpl(JpaEntityInformation<T, ?> ei, EntityManager em) {
        super(ei, em);
        this.em = em;
        domainClass = ei.getJavaType();
        LayerRegistry.registerRepository(domainClass, this);
    }

    public DataRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.domainClass = domainClass;
        LayerRegistry.registerRepository(domainClass, this);
    }

    @Override
    public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
        super.setEscapeCharacter(escapeCharacter);
        this.escapeCharacter = escapeCharacter;
    }

    @Override
    public T getOrNull(String id) { return findFromCacheById(NULL_FN, id); }

    @Override
    public T getById(String id) {
        T value = findFromCacheById(NULL_FN, id);
        if (value == null) {
            throw new IllegalArgumentException("数据不存在: " + id);
        } else {
            return value;
        }
    }

    @Override
    public T getById(String id, String throwsMessageIfAbsent) {
        T value = findFromCacheById(NULL_FN, id);
        if (value == null) {
            throw new IllegalArgumentException(throwsMessageIfAbsent);
        } else {
            return value;
        }
    }

    @Override
    public <X extends Throwable> T getById(String id, Supplier<? extends X> throwIfAbsent) throws X {
        T value = findFromCacheById(NULL_FN, id);
        if (value == null) {
            throw throwIfAbsent.get();
        } else {
            return value;
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public List<T> saveAll(T first, T second, T... entities) { return saveAll(asList(first, second, entities)); }

    @Override
    public Slice<T> sliceAll(Pageable pageable) {
        return pageable.isUnpaged() ? new SliceImpl<>(findAll()) : readSlice(getQuery(null, pageable), pageable);
    }

    @Override
    public <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable) {
        ExampleSpecification<S> spec = new ExampleSpecification<>(example, escapeCharacter);
        TypedQuery<S> query = getQuery(spec, example.getProbeType(), pageable);
        return pageable.isUnpaged() ? new SliceImpl<>(query.getResultList()) : readSlice(query, pageable);
    }

    @Override
    public List<T> findAllById(String first, String second, String... ids) {
        return findAllById(asList(first, second, ids));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteAll(T first, T second, T... entities) { deleteAll(asList(first, second, entities)); }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void disableById(String id) { findById(id).ifPresent(this::disableOne); }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void disable(T entity) { disableOne(entity); }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void disableAll(Iterable<? extends T> entities) {
        if (entities != null) {
            for (T entity : entities) {
                disableOne(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> void disableAll(S first, S second, S... entities) {
        disableOne(first);
        disableOne(second);
        if (entities != null) {
            for (S entity : entities) {
                disableOne(entity);
            }
        }
    }

    protected <S extends T> void disableOne(S entity) {
        if (entity != null) {
            if (entity instanceof DataRecord) {
                ((DataRecord) entity).withUnavailable();
                save(entity);
            } else {
                delete(entity);
            }
        }
    }

    protected <S extends T> Slice<S> readSlice(TypedQuery<S> query, Pageable pageable) {
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<S> content = query.getResultList();
        return new SliceImpl<>(content, pageable, content.size() >= pageable.getPageSize());
    }

    private static class ExampleSpecification<T> implements Specification<T> {

        private static final long serialVersionUID = 1L;

        private final EscapeCharacter escapeCharacter;
        private final Example<T> example;

        ExampleSpecification(Example<T> example, EscapeCharacter escapeCharacter) {
            Assert.notNull(escapeCharacter, "EscapeCharacter must not be null!");
            Assert.notNull(example, "Example must not be null!");
            this.escapeCharacter = escapeCharacter;
            this.example = example;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return getPredicate(root, cb, example, escapeCharacter);
        }
    }

    private static int guessSize(Iterable iter) {
        return iter == null ? 0 : (iter instanceof Collection ? ((Collection) iter).size() : 16);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> S save(S entity) { return doSaveEntity(entity); }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList();
        if (entities == null) {
            return list;
        } else {
            Cache cache = getCache();
            for (S entity : entities) {
                list.add(doSaveEntity(cache, entity));
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> S saveAndFlush(S entity) {
        S saved = doSaveEntity(entity);
        flush();
        return saved;
    }

    @Override
    public Optional<T> findById(String s) { return ofNullable(findFromCacheById(NULL_FN, s)); }

    @Override
    public List<T> findAllById(Iterable<String> strings) {
        List<T> results = new ArrayList(guessSize(strings));
        Cache cache = getCache();
        for (String identity : strings) {
            results.add(findFromCacheById(cache, NULL_FN, identity));
        }
        return results;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(T entity) {
        super.delete(entity);
        getCache().evict(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteById(String s) {
        super.deleteById(s);
        getCache().evict(s);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteAll(Iterable<? extends T> entities) {
        if (entities != null) {
            Cache cache = getCache();
            for (T entity : entities) {
                super.delete(entity);
                cache.evict(entity.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteAll() {
        super.deleteAll();
        getCache().clear();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteAllInBatch() {
        super.deleteAllInBatch();
        getCache().clear();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteInBatch(Iterable<T> entities) {
        super.deleteInBatch(entities);
        Cache cache = getCache();
        for (T entity : entities) {
            cache.evict(entity.getId());
        }
    }

    protected Cache getCache() { return cacheManager.getCache(domainClass.getName()); }

    protected <S extends T> S doSaveEntity(S s) { return doSaveEntity(getCache(), s); }

    protected <S extends T> S doSaveEntity(Cache cache, S s) {
        String beforeSaveId = s.getId();
        boolean newer = s.isNew();
        // 在更新前删除一次，防止并发下删除异常，（在某些情况下，可考虑不要这一步）
        if (!newer) {
            cache.evict(beforeSaveId);
        }
        s = super.save(s);
        if (newer) {
            // 新数据直接缓存
            cache.put(s.getId(), s);
        } else {
            // 更新后再次删除，防止缓存了历史数据
            cache.evict(beforeSaveId);
        }
        return s;
    }

    protected T findFromCacheById(Function<String, Object> convertIdIfAbsent, String id) {
        return findFromCacheById(getCache(), convertIdIfAbsent, id);
    }

    protected T findFromCacheById(Cache cache, Function<String, Object> convertIdIfAbsent, String id) {
        Cache.ValueWrapper wrapper = cache.get(id);
        Object value = wrapper == null ? null : wrapper.get();
        if (value == null) {
            // 不存在缓存
            Optional optional = super.findById(id);
            if (optional.isPresent()) {
                // 存在合法数据，缓存并返回
                value = optional.get();
                cache.put(id, value);
                return (T) value;
            } else {
                // 不存在数据，缓存占位符，并返回 null
                cache.put(id, PLACEHOLDER);
                return null;
            }
        } else if (value == PLACEHOLDER) {
            // 缓存过一个不存在的值
            Object converted = convertIdIfAbsent.apply(id);
            if (converted instanceof RuntimeException) {
                throw (RuntimeException) converted;
            } else if (converted instanceof Error) {
                throw (Error) converted;
            } else if (converted instanceof Throwable) {
                throw new IllegalStateException((String) converted);
            }
            return (T) converted;
        } else {
            // 命中缓存
            return (T) value;
        }
    }

    static <T> List<T> asList(T first, T second, T... rest) {
        return ListUtil.addAll(ListUtil.newList(first, second), rest);
    }
}
