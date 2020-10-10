package com.moon.spring.data.jpa.factory;

import com.moon.core.lang.ref.LazyAccessor;
import com.moon.core.util.ListUtil;
import com.moon.data.DataRecord;
import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.DataRepository;
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
import java.io.Serializable;
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
@Transactional(readOnly = true)
public abstract class AbstractRepositoryImpl<T extends JpaRecord<ID>, ID> extends SimpleJpaRepository<T, ID>
    implements DataRepository<T, ID> {

    final static CacheManager NO_OP = new NoOpCacheManager();

    final static Serializable PLACEHOLDER = new byte[0];

    private final LazyAccessor<CacheManager> cacheManagerAccessor;

    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    private final Function<ID, Object> NULL_FN = o -> null;

    private final Class domainClass;
    private final EntityManager em;
    private final String cacheNamespace;

    public AbstractRepositoryImpl(
        JpaEntityInformation<T, ?> ei, EntityManager em
    ) {
        super(ei, em);
        this.em = em;
        this.domainClass = ei.getJavaType();
        LayerRegistry.registerRepository(domainClass, this);
        this.cacheManagerAccessor = LazyAccessor.of(NO_OP);
        this.cacheNamespace = domainClass.getSimpleName();
    }

    public AbstractRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.domainClass = domainClass;
        LayerRegistry.registerRepository(domainClass, this);
        this.cacheManagerAccessor = LazyAccessor.of(NO_OP);
        this.cacheNamespace = domainClass.getSimpleName();
    }

    @Override
    public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    /**
     * 放入缓存后的操作
     *
     * @param cache
     * @param id
     * @param domainClass
     * @param entity
     */
    protected void onCachedEntity(Cache cache, ID id, Class<T> domainClass, Object entity) {}

    /**
     * 删除缓存后的操作
     *
     * @param cache
     * @param id
     * @param domainClass
     */
    protected void onCacheEvicted(Cache cache, ID id, Class<T> domainClass) {}

    @Override
    public T getOrNull(ID id) { return findFromCacheById(NULL_FN, id); }

    @Override
    public T getById(ID id) {
        T value = findFromCacheById(NULL_FN, id);
        if (value == null) {
            throw new NullPointerException("数据不存在: " + id);
        } else {
            return value;
        }
    }

    @Override
    public T getById(ID id, String throwsMessageIfAbsent) {
        T value = findFromCacheById(NULL_FN, id);
        if (value == null) {
            throw new NullPointerException(throwsMessageIfAbsent);
        } else {
            return value;
        }
    }

    @Override
    public <X extends Throwable> T getById(ID id, Supplier<? extends X> throwIfAbsent) throws X {
        T value = findFromCacheById(NULL_FN, id);
        if (value == null) {
            throw throwIfAbsent.get();
        } else {
            return value;
        }
    }

    @Override
    @Transactional
    public List<T> saveAll(T first, T second, T... entities) { return saveAll(asList(first, second, entities)); }

    @Override
    public Slice<T> sliceAll(Pageable pageable) {
        return pageable.isUnpaged() ? new SliceImpl<>(findAll()) : readSlicedEntities(getQuery(null, pageable),
            pageable);
    }

    @Override
    public <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable) {
        ExampleSpecification<S> spec = new ExampleSpecification<>(example, escapeCharacter);
        TypedQuery<S> query = getQuery(spec, example.getProbeType(), pageable);
        return pageable.isUnpaged() ? new SliceImpl<>(query.getResultList()) : readSlicedEntities(query, pageable);
    }

    @Override
    @Transactional
    public <S extends T> S insert(S entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public List<T> findAllById(ID first, ID second, ID... ids) {
        return findAllById(asList(first, second, ids));
    }

    @Override
    @Transactional
    public void deleteAll(T first, T second, T... entities) { deleteAll(asList(first, second, entities)); }

    @Override
    @Transactional
    public void disableById(ID id) { findById(id).ifPresent(this::doDisableEntity); }

    @Override
    @Transactional
    public void disable(T entity) { doDisableEntity(entity); }

    @Override
    @Transactional
    public void disableAll(Iterable<? extends T> entities) {
        if (entities != null) {
            Cache cache = getCache();
            for (T entity : entities) {
                doDisableEntity(cache, entity);
            }
        }
    }

    @Override
    @Transactional
    public <S extends T> void disableAll(S first, S second, S... entities) {
        Cache cache = getCache();
        doDisableEntity(cache, first);
        doDisableEntity(cache, second);
        if (entities != null) {
            for (S entity : entities) {
                doDisableEntity(cache, entity);
            }
        }
    }

    protected <S extends T> void doDisableEntity(S entity) {
        doDisableEntity(getCache(), entity);
    }

    protected <S extends T> void doDisableEntity(Cache cache, S entity) {
        if (entity != null) {
            if (entity instanceof DataRecord) {
                ((DataRecord) entity).withUnavailable();
                ID id = entity.getId();
                cache.evict(id);
                doSaveEntity(entity);
                doEvictCache(cache, id);
            } else {
                delete(entity);
            }
        }
    }

    protected <S extends T> Slice<S> readSlicedEntities(TypedQuery<S> query, Pageable pageable) {
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
    @Transactional
    public <S extends T> S save(S entity) { return doSaveEntity(entity); }

    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList<>();
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
    @Transactional
    public <S extends T> S saveAndFlush(S entity) {
        S saved = doSaveEntity(entity);
        flush();
        return saved;
    }

    @Override
    public Optional<T> findById(ID s) { return ofNullable(findFromCacheById(NULL_FN, s)); }

    @Override
    public List<T> findAllById(Iterable<ID> strings) {
        List<T> results = new ArrayList(guessSize(strings));
        Cache cache = getCache();
        for (ID identity : strings) {
            results.add(findFromCacheById(cache, NULL_FN, identity));
        }
        return results;
    }

    @Override
    @Transactional
    public void delete(T entity) {
        super.delete(entity);
        doEvictCache(entity.getId());
    }

    @Override
    @Transactional
    public void deleteById(ID s) {
        super.deleteById(s);
        doEvictCache(s);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        if (entities != null) {
            Cache cache = getCache();
            for (T entity : entities) {
                super.delete(entity);
                doEvictCache(cache, entity.getId(), domainClass);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAll() {
        super.deleteAll();
        getCache().clear();
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        super.deleteAllInBatch();
        getCache().clear();
    }

    @Override
    @Transactional
    public void deleteInBatch(Iterable<T> entities) {
        super.deleteInBatch(entities);
        Cache cache = getCache();
        Class domainClass = getDomainClass();
        for (T entity : entities) {
            doEvictCache(cache, entity.getId(), domainClass);
        }
    }

    protected Cache getCache() {
        return getCache(getDomainClass().getName());
    }

    protected Cache getCache(String namespace) {
        return cacheManagerAccessor.get().getCache(namespace);
    }

    protected void doEvictCache(ID id) {
        doEvictCache(getCache(), id, getDomainClass());
    }

    protected void doEvictCache(Cache cache, ID id) {
        doEvictCache(cache, id, getDomainClass());
    }

    protected void doEvictCache(Cache cache, ID id, Class domainClass) {
        cache.evict(id);
        onCacheEvicted(cache, id, domainClass);
    }

    protected <S extends T> S doSaveEntity(S s) { return doSaveEntity(getCache(), s); }

    protected <S extends T> S doSaveEntity(Cache cache, S s) {
        ID beforeSaveId = s.getId();
        boolean newer = s.isNew();
        if (!newer) {
            // 在更新前删除一次，防止并发下删除异常，（在某些情况下，可考虑不要这一步）
            cache.evict(beforeSaveId);
        }
        s = super.save(s);
        if (newer) {
            // 新数据直接缓存
            ID savedId = s.getId();
            cache.put(savedId, s);
            onCachedEntity(cache, savedId, getDomainClass(), s);
        } else {
            // 更新后再次删除，防止缓存了历史数据
            doEvictCache(cache, beforeSaveId);
        }
        return s;
    }

    protected T findFromCacheById(Function<ID, Object> convertIdIfAbsent, ID id) {
        return findFromCacheById(getCache(), convertIdIfAbsent, id);
    }

    protected T findFromCacheById(Cache cache, Function<ID, Object> convertIdIfAbsent, ID id) {
        Cache.ValueWrapper wrapper = cache.get(id);
        Object value = wrapper == null ? null : wrapper.get();
        if (value == null) {
            // 不存在缓存
            value = findFromDatabaseById(id);
            if (value == null) {
                // 不存在数据，缓存占位符，并返回 null
                cache.put(id, PLACEHOLDER);
                return null;
            } else {
                // 存在合法数据，缓存后返回
                cache.put(id, value);
                onCachedEntity(cache, id, getDomainClass(), value);
                return (T) value;
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

    final T findFromDatabaseById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("The given id must not be null!");
        }
        return em.find(getDomainClass(), id);
    }

    protected static <T> List<T> asList(T first, T second, T... rest) {
        return ListUtil.addAll(ListUtil.newList(first, second), rest);
    }
}
