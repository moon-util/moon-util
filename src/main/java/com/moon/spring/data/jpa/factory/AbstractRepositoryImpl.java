package com.moon.spring.data.jpa.factory;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.logger.Logger;
import com.moon.core.util.logger.LoggerUtil;
import com.moon.data.DataRecord;
import com.moon.data.RecordConst;
import com.moon.data.annotation.RecordCacheNamespace;
import com.moon.data.registry.LayerRegistry;
import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.DataRepository;
import com.moon.spring.data.jpa.start.EnableJpaRecordCaching;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * @author moonsky
 */
@NoRepositoryBean
@SuppressWarnings("all")
@Transactional(readOnly = true)
public abstract class AbstractRepositoryImpl<T extends JpaRecord<ID>, ID> extends SimpleJpaRepository<T, ID>
    implements DataRepository<T, ID> {

    protected final static Logger logger = LoggerUtil.getLogger();
    /**
     * @see com.moon.spring.data.jpa.start.JpaRecordCacheRegistrar#CACHE_PROPERTIES_NAME
     */
    private final static String CACHE_PROPERTIES_NAME = "iMoonUtilJpaRecordRepositoryCacheProperties";

    private final static String[] CACHE_DELIMITERS = {"", ".", "-", ">", ":", "_"};

    private final static Map<String, Object> CACHED_NAMESPACES = new ConcurrentHashMap<>();
    /**
     * 空字符串
     * <p>
     * 枚举序列化后可能导致多系统不能互相引用
     *
     * @see #isPlaceholder(Object)
     */
    protected final static Serializable PLACEHOLDER = new byte[0];

    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    private final Function<ID, Object> NULL_FN = o -> null;
    private final RepositoryContextMetadata metadata;
    private final CacheManager cacheManager;
    private final String cacheNamespace;
    private final Class domainClass;
    private final EntityManager em;

    public AbstractRepositoryImpl(
        RepositoryInformation repositoryInformation,
        JpaEntityInformation<T, ?> ei,
        EntityManager em,
        RepositoryContextMetadata metadata
    ) {
        super(ei, em);
        this.em = em;
        this.metadata = metadata;
        this.domainClass = ei.getJavaType();
        LayerRegistry.registerRepository(domainClass, this);
        this.cacheManager = deduceCacheManager(metadata);
        this.cacheNamespace = deduceCacheNamespace(metadata, domainClass, PLACEHOLDER);
    }

    public AbstractRepositoryImpl(Class<T> domainClass, EntityManager em, RepositoryContextMetadata metadata) {
        super(domainClass, em);
        this.em = em;
        this.metadata = metadata;
        this.domainClass = domainClass;
        LayerRegistry.registerRepository(domainClass, this);
        this.cacheManager = deduceCacheManager(metadata);
        this.cacheNamespace = deduceCacheNamespace(metadata, domainClass, PLACEHOLDER);
    }

    private static <T> T onEnabledRecordCacheOrDefault(
        RepositoryContextMetadata metadata,
        BiFunction<ApplicationContext, EnableJpaRecordCaching, T> converter,
        T defaultValue
    ) {
        T resultValue = defaultValue;
        try {
            ApplicationContext context = metadata.getApplicationContext();
            if (context.containsBean(CACHE_PROPERTIES_NAME)) {
                EnableJpaRecordCaching cache = context.getBean(CACHE_PROPERTIES_NAME, EnableJpaRecordCaching.class);
                if (context.containsBeanDefinition(cache.cacheManagerRef())) {
                    resultValue = converter.apply(context, cache);
                }
            }
        } catch (Throwable ignored) {
            resultValue = defaultValue;
        }
        return resultValue == null ? defaultValue : resultValue;
    }

    protected static CacheManager deduceCacheManager(RepositoryContextMetadata metadata) {
        return onEnabledRecordCacheOrDefault(metadata,
            (ctx, cache) -> ctx.getBean(cache.cacheManagerRef(), CacheManager.class),
            NoOpCacheManager.MANAGER);
    }

    protected static String deduceCacheNamespace(
        RepositoryContextMetadata metadata, Class<?> domainClass, Object placeholder
    ) {
        return onEnabledRecordCacheOrDefault(metadata, (ctx, c) -> {
            return toCacheNamespace(c.group(), domainClass, placeholder);
        }, null);
    }

    /**
     * 这种实现方式当项目存在同名不同包实体类时，可能引起分布式数据不一致，这个问题可通过如下方式解决
     * <pre>
     * 在实体类上注解{@link RecordCacheNamespace}
     * </pre>
     *
     * @param domainClass
     * @param placeholder
     *
     * @return
     */
    protected static String toCacheNamespace(String globalGroup, Class<?> domainClass, Object placeholder) {
        String group = StringUtil.defaultIfBlank(globalGroup, RecordConst.CACHE_GROUP), value;
        RecordCacheNamespace namespaceCfg = domainClass.getDeclaredAnnotation(RecordCacheNamespace.class);
        if (namespaceCfg != null) {
            group = namespaceCfg.group();
            value = namespaceCfg.value();
            // 如果自定义了 group 和 namespace 直接返回
            if (StringUtil.isNotBlank(value)) {
                String namespace = mergeNamespace(group, value);
                CacheNamespace.putNamespace(domainClass, namespace);
                return namespace;
            }
        }
        // 否则自动推断：实体名、缩写实体名、完全限定名
        String namespace = detectNamespace(group, domainClass.getSimpleName(), domainClass, placeholder);
        if (namespace != null) {
            return namespace;
        }
        for (String delimiter : CACHE_DELIMITERS) {
            String tempName = ClassUtil.getShortName(domainClass, delimiter);
            namespace = detectNamespace(group, tempName, domainClass, placeholder);
            if (namespace != null) {
                return namespace;
            }
        }
        // 完全限定名
        return detectNamespace(group, domainClass.getName(), domainClass, placeholder);
    }

    private static String mergeNamespace(String group, String namespace) {
        return new StringBuilder(group).append(' ').append(namespace).toString().trim().replace(' ', ':');
    }

    /**
     * 检测当前缓存命名空间是否唯一，如果是就缓存并返回，否则直接返回 null
     *
     * @param namespace   命名空间
     * @param placeholder 占位符
     *
     * @return
     */
    private static String detectNamespace(String group, String namespace, Class domainClass, Object placeholder) {
        String groupedNamespace = mergeNamespace(group, namespace);
        if (!CACHED_NAMESPACES.containsKey(groupedNamespace)) {
            CACHED_NAMESPACES.put(groupedNamespace, placeholder);
            CacheNamespace.putNamespace(domainClass, namespace);
            return groupedNamespace;
        }
        return null;
    }

    @Override
    public void setEscapeCharacter(EscapeCharacter escapeCharacter) { this.escapeCharacter = escapeCharacter; }

    protected RepositoryContextMetadata getMetadata() { return metadata; }

    protected ApplicationContext getApplicationContext() {
        RepositoryContextMetadata metadata = getMetadata();
        return metadata == null ? null : metadata.getApplicationContext();
    }

    /*
     * *****************************************************************************************************************
     * ***** save **************************************************************************************************
     * *****************************************************************************************************************
     */

    @Override
    @Transactional
    public <S extends T> S insert(S entity) {
        if (entity.isNew()) {
            return this.save(entity);
        } else {
            em.persist(JpaIdentifierUtil.extractPresetPrimaryKey(entity));
            ID id = entity.getId();
            Cache cache = getCache();
            cache.put(id, entity);
            onCachedEntity(cache, id, getDomainClass(), entity);
            return entity;
        }
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) { return doSaveEntity(entity); }

    @Override
    @Transactional
    public List<T> saveAll(T first, T second, T... entities) { return saveAll(asList(first, second, entities)); }

    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList<>();
        if (entities == null) {
            return list;
        } else {
            Cache cache = getCache();
            for (S entity : entities) {
                list.add(doSaveEntity(entity, cache));
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

    /*
     * *****************************************************************************************************************
     * ***** find one **************************************************************************************************
     * *****************************************************************************************************************
     */

    @Override
    public Optional<T> findById(ID s) { return ofNullable(findFromCacheById(s, NULL_FN)); }

    @Override
    public T getOrNull(ID id) { return findFromCacheById(id, NULL_FN); }

    @Override
    public T getById(ID id) {
        T value = findFromCacheById(id, NULL_FN);
        if (value == null) {
            throw new NullPointerException("数据不存在: " + id);
        } else {
            return value;
        }
    }

    @Override
    public T getById(ID id, String throwsMessageIfAbsent) {
        T value = findFromCacheById(id, NULL_FN);
        if (value == null) {
            throw new NullPointerException(throwsMessageIfAbsent);
        } else {
            return value;
        }
    }

    @Override
    public <X extends Throwable> T getById(ID id, Supplier<? extends X> throwIfAbsent) throws X {
        T value = findFromCacheById(id, NULL_FN);
        if (value == null) {
            throw throwIfAbsent.get();
        } else {
            return value;
        }
    }

    /*
     * *****************************************************************************************************************
     * ***** slice *****************************************************************************************************
     * *****************************************************************************************************************
     */

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

    protected <S extends T> Slice<S> readSlicedEntities(TypedQuery<S> query, Pageable pageable) {
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<S> content = query.getResultList();
        return new SliceImpl<>(content, pageable, content.size() >= pageable.getPageSize());
    }

    private static int guessSize(Iterable iter) {
        return iter == null ? 0 : (iter instanceof Collection ? ((Collection) iter).size() : 16);
    }

    /*
     * *****************************************************************************************************************
     * ***** find all **************************************************************************************************
     * *****************************************************************************************************************
     */

    @Override
    public List<T> findAllById(ID first, ID second, ID... ids) { return findAllById(asList(first, second, ids)); }

    @Override
    public List<T> findAllById(Iterable<ID> strings) {
        List<T> results = new ArrayList(guessSize(strings));
        Cache cache = getCache();
        for (ID identity : strings) {
            results.add(findFromCacheById(identity, NULL_FN, cache));
        }
        return results;
    }

    /*
     * *****************************************************************************************************************
     * ***** disable ***************************************************************************************************
     * *****************************************************************************************************************
     */

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
                doDisableEntity(entity, cache);
            }
        }
    }

    @Override
    @Transactional
    public <S extends T> void disableAll(S first, S second, S... entities) {
        Cache cache = getCache();
        doDisableEntity(first, cache);
        doDisableEntity(second, cache);
        if (entities != null) {
            for (S entity : entities) {
                doDisableEntity(entity, cache);
            }
        }
    }

    /*
     * *****************************************************************************************************************
     * ***** delete ****************************************************************************************************
     * *****************************************************************************************************************
     */

    @Override
    @Transactional
    public void delete(T entity) {
        super.delete(entity);
        doEvictCached(entity.getId());
    }

    @Override
    @Transactional
    public void deleteById(ID s) {
        super.deleteById(s);
        doEvictCached(s);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        if (entities != null) {
            Cache cache = getCache();
            for (T entity : entities) {
                super.delete(entity);
                doEvictCached(entity.getId(), domainClass, cache);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAll(T first, T second, T... entities) { deleteAll(asList(first, second, entities)); }

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
            doEvictCached(entity.getId(), domainClass, cache);
        }
    }

    /*
     * *****************************************************************************************************************
     * ***** getCache **************************************************************************************************
     * *****************************************************************************************************************
     */

    protected Cache getCache() { return getCache(cacheNamespace); }

    protected CacheManager getCacheManager() { return cacheManager; }

    protected Cache getCache(String namespace) { return getCacheManager().getCache(namespace); }

    /*
     * *****************************************************************************************************************
     * ***** doEvictCached *********************************************************************************************
     * *****************************************************************************************************************
     */

    protected void doEvictCached(ID id) { doEvictCached(id, getDomainClass(), true, getCache()); }

    protected void doEvictCached(ID id, boolean triggerEvictEvent) {
        doEvictCached(id, getDomainClass(), triggerEvictEvent, getCache());
    }

    protected void doEvictCached(ID id, Cache cache) { doEvictCached(id, true, cache); }

    protected void doEvictCached(ID id, boolean triggerEvictEvent, Cache cache) {
        doEvictCached(id, getDomainClass(), triggerEvictEvent, cache);
    }

    protected void doEvictCached(ID id, Class domainClass, Cache cache) {
        doEvictCached(id, domainClass, true, cache);
    }

    protected void doEvictCached(ID id, Class domainClass, boolean triggerEvictEventEvent, Cache cache) {
        cache.evict(id);
        if (triggerEvictEventEvent) {
            onCacheEvicted(cache, id, domainClass);
        }
    }

    /*
     * *****************************************************************************************************************
     * ***** doSaveEntity **********************************************************************************************
     * *****************************************************************************************************************
     */

    protected <S extends T> S doSaveEntity(S s) { return doSaveEntity(s, true); }

    protected <S extends T> S doSaveEntity(S s, boolean triggerEvictEvent) {
        return doSaveEntity(s, triggerEvictEvent, getCache());
    }

    protected <S extends T> S doSaveEntity(S s, Cache cache) { return doSaveEntity(s, true, cache); }

    protected <S extends T> S doSaveEntity(S s, boolean triggerEvictEvent, Cache cache) {
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
            doEvictCached(beforeSaveId, triggerEvictEvent, cache);
        }
        return s;
    }

    /*
     * *****************************************************************************************************************
     * ***** doDisableEntity *******************************************************************************************
     * *****************************************************************************************************************
     */

    protected <S extends T> void doDisableEntity(S entity) { doDisableEntity(entity, getCache()); }

    protected <S extends T> void doDisableEntity(S entity, Cache cache) {
        if (entity instanceof DataRecord) {
            ((DataRecord) entity).withUnavailable();
            ID id = entity.getId();
            cache.evict(id);
            doSaveEntity(entity, false);
            onDisableEntity(cache, id, getDomainClass(), entity);
        } else {
            delete(entity);
        }
    }

    /*
     * *****************************************************************************************************************
     * ***** findFromCacheById *****************************************************************************************
     * *****************************************************************************************************************
     */

    protected T findFromCacheById(ID id, Function<ID, Object> convertIdIfAbsent) {
        return findFromCacheById(id, convertIdIfAbsent, getCache());
    }

    protected T findFromCacheById(ID id, Function<ID, Object> convertIdIfAbsent, Cache cache) {
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
        } else if (isPlaceholder(value)) {
            // 缓存过一个不存在的值
            Object converted = convertIdIfAbsent.apply(id);
            if (converted == null) {
                return null;
            } else if (converted instanceof RuntimeException) {
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

    /**
     * 是否是占位符
     *
     * @param value
     *
     * @return
     *
     * @see #PLACEHOLDER
     */
    protected boolean isPlaceholder(Object value) {
        return value instanceof byte[] && ((byte[]) value).length == 0;
    }

    /*
     * *****************************************************************************************************************
     * ***** findFromDatabaseById **************************************************************************************
     * *****************************************************************************************************************
     */

    final T findFromDatabaseById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("The given id must not be null!");
        }
        return em.find(getDomainClass(), id);
    }

    protected static <T> List<T> asList(T first, T second, T... rest) {
        return ListUtil.addAll(ListUtil.newList(first, second), rest);
    }

    /*
     * *****************************************************************************************************************
     * ***** trigger events ********************************************************************************************
     * *****************************************************************************************************************
     */

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

    /**
     * 禁用后的操作
     * <p>
     * 禁用不会触发删除缓存事件
     *
     * @param cache
     * @param id
     * @param domainClass
     */
    protected void onDisableEntity(Cache cache, ID id, Class<T> domainClass, Object entity) {}

    /*
     * *****************************************************************************************************************
     * ***** inner classes *********************************************************************************************
     * *****************************************************************************************************************
     */

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
            return QueryByExamplePredicateBuilder.getPredicate(root, cb, example, escapeCharacter);
        }
    }

    private static class NoOpCacheManager implements CacheManager {

        private final static NoOpCacheManager MANAGER = new NoOpCacheManager();

        @Override
        public Cache getCache(String name) { return NoOpCache.CACHE; }

        @Override
        public Collection<String> getCacheNames() { return NoOpSet.SET; }
    }

    private static class NoOpCache implements Cache {

        private final static NoOpCache CACHE = new NoOpCache();

        @Override
        public String getName() { return null; }

        @Override
        public Object getNativeCache() { return null; }

        @Override
        public ValueWrapper get(Object key) { return null; }

        @Override
        public <T> T get(Object key, Class<T> type) { return null; }

        @Override
        public <T> T get(Object key, Callable<T> valueLoader) { return null; }

        @Override
        public void put(Object key, Object value) { }

        @Override
        public void evict(Object key) { }

        @Override
        public void clear() { }
    }

    private static class NoOpSet<T> implements Set<T> {

        private final static NoOpSet SET = new NoOpSet();
        private final static Object[] OBJECTS = new Object[0];

        @Override
        public int size() { return 0; }

        @Override
        public boolean isEmpty() { return true; }

        @Override
        public boolean contains(Object o) { return false; }

        @Override
        public Iterator<T> iterator() { return NoOpItr.ITR; }

        @Override
        public Object[] toArray() { return OBJECTS; }

        @Override
        public <T1> T1[] toArray(T1[] a) { return a; }

        @Override
        public boolean add(T t) { return false; }

        @Override
        public boolean remove(Object o) { return false; }

        @Override
        public boolean containsAll(Collection<?> c) { return false; }

        @Override
        public boolean addAll(Collection<? extends T> c) { return false; }

        @Override
        public boolean retainAll(Collection<?> c) { return false; }

        @Override
        public boolean removeAll(Collection<?> c) { return false; }

        @Override
        public void clear() { }

        @Override
        public boolean removeIf(java.util.function.Predicate<? super T> filter) { return false; }

        @Override
        public void forEach(Consumer<? super T> action) { }
    }

    private static class NoOpItr implements Iterator {

        private final static NoOpItr ITR = new NoOpItr();

        @Override
        public boolean hasNext() { return false; }

        @Override
        public Object next() { return null; }

        @Override
        public void remove() { }

        @Override
        public void forEachRemaining(Consumer action) { }
    }
}
