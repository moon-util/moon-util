package com.moon.spring.data;

import com.moon.more.RunnerRegistration;
import com.moon.more.data.registry.LayerEnum;
import com.moon.more.data.registry.LayerRegistry;
import com.moon.more.model.id.IdSupplier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.moon.core.lang.ClassUtil.isExtendOf;

/**
 * @author benshaoye
 */
public abstract class BaseAccessorImpl<ID, T extends IdSupplier<ID>> implements BaseAccessor<ID, T>, InitializingBean {

    private final static Class NULL = null;

    protected static boolean isAccessorType(Class cls) {
        return isExtendOf(cls, BaseAccessor.class);
    }

    protected static boolean isRecordableType(Class cls) {
        return isExtendOf(cls, IdSupplier.class);
    }

    protected final Class deduceDomainClass() {
        Class domainClass = null;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] types = paramType.getActualTypeArguments();
            if (types.length == 1) {
                domainClass = (Class) types[0];
            } else {
                Class recordable = null;
                for (Type entityType : types) {
                    if (entityType instanceof Class) {
                        Class cls = (Class) entityType;
                        if (isRecordableType(cls)) {
                            recordable = cls;
                        }
                    }
                }
                domainClass = recordable;
            }
        }
        return domainClass;
    }

    @Autowired
    private WebApplicationContext context;

    protected final Class domainClass;
    private BaseAccessor<ID, T> accessor;

    protected BaseAccessorImpl(LayerEnum accessLay) {
        this(accessLay, NULL);
    }

    protected BaseAccessorImpl(LayerEnum accessLay, Class domainClass) {
        this(NULL, accessLay, domainClass);
    }

    protected BaseAccessorImpl(LayerEnum accessLay, LayerEnum registryMeLay) {
        this(NULL, accessLay, registryMeLay, NULL);
    }

    protected BaseAccessorImpl(Class<? extends BaseAccessor> accessServeClass, LayerEnum registryMeLay) {
        this(accessServeClass, registryMeLay, NULL);
    }

    protected BaseAccessorImpl(
        Class<? extends BaseAccessor> accessServeClass, LayerEnum registryMeLay, Class domainClass
    ) { this(accessServeClass, null, registryMeLay, domainClass); }

    protected BaseAccessorImpl(
        Class<? extends BaseAccessor> accessServeClass, LayerEnum accessLay, LayerEnum registryMeLay
    ) { this(accessServeClass, accessLay, registryMeLay, NULL); }

    /**
     * 构造器
     *
     * @param accessServeClass 将要访问的服务具体实现类型，如：UserServiceImpl
     * @param accessLay        内部管理访问的层
     * @param registryMeLay    内部管理注册的层，注册后供其他层访问
     * @param domainClass      具体实体类型
     */
    protected BaseAccessorImpl(
        Class<? extends BaseAccessor> accessServeClass, LayerEnum accessLay, LayerEnum registryMeLay, Class domainClass
    ) {
        if (domainClass == null || !(domainClass != null && isRecordableType(domainClass))) {
            if (isRecordableType(accessServeClass)) {
                domainClass = accessServeClass;
                accessServeClass = null;
            } else {
                domainClass = deduceDomainClass();
            }
        }

        if (!(accessServeClass != null && isAccessorType(accessServeClass))) {
            if (domainClass == null && isRecordableType(accessServeClass)) {
                domainClass = accessServeClass;
            }
            accessServeClass = null;
        }

        domainClass = domainClass == null ? deduceDomainClass() : domainClass;
        Runnable runner = getRunner(accessServeClass, accessLay, domainClass);
        LayerRegistry.registry(registryMeLay, domainClass, this);
        RunnerRegistration.getInstance().registry(runner);
        this.domainClass = domainClass;
    }

    protected Runnable getRunner(
        Class<? extends BaseAccessor> accessServeClass, LayerEnum accessLay, Class domainClass
    ) {
        return () -> {
            BaseAccessor accessor = getDefaultAccessor();
            if (accessor == null && accessServeClass != null) {
                accessor = getContext().getBean(accessServeClass);
            }
            if (accessor == null) {
                accessor = LayerRegistry.get(accessLay, domainClass);
            }
            this.accessor = accessor;
        };
    }

    public Class getDomainClass() { return domainClass; }

    protected WebApplicationContext getContext() { return context; }

    protected BaseAccessor<ID, T> getAccessor() { return accessor; }

    @Override
    public void afterPropertiesSet() throws Exception {}

    /**
     * like getRepository
     * like getService
     * like getMapper
     * <p>
     * and so on...
     *
     * @return
     */
    protected BaseAccessor<ID, T> getDefaultAccessor() { return null; }

    @Override
    public boolean existsById(String id) { return getAccessor().existsById(id); }

    @Override
    public long count() { return getAccessor().count(); }

    /**
     * 保存
     *
     * @param entity
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> S save(S entity) { return getAccessor().save(entity); }

    /**
     * 保存
     *
     * @param entity
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> S saveAndFlush(S entity) { return getAccessor().saveAndFlush(entity); }

    /**
     * 保存
     *
     * @param entities
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> List<S> saveAll(Iterable<S> entities) { return getAccessor().saveAll(entities); }

    /**
     * 保存
     *
     * @param first
     * @param second
     * @param entities
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public List<T> saveAll(T first, T second, T... entities) { return getAccessor().saveAll(first, second, entities); }

    /**
     * 查询
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() { return getAccessor().findAll(); }

    /**
     * 查询
     *
     * @param sort
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> findAll(Sort sort) { return getAccessor().findAll(sort); }

    /**
     * 查询
     *
     * @param pageable
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) { return getAccessor().findAll(pageable); }

    /**
     * 查询
     *
     * @param example
     * @param <S>
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public <S extends T> Iterable<S> findAll(Example<S> example) { return getAccessor().findAll(example); }

    /**
     * 查询
     *
     * @param example
     * @param sort
     * @param <S>
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) { return getAccessor().findAll(example, sort); }

    /**
     * 查询
     *
     * @param example
     * @param pageable
     * @param <S>
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return getAccessor().findAll(example, pageable);
    }

    /**
     * 下一页切片
     *
     * @param pageable
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<T> sliceAll(Pageable pageable) { return getAccessor().sliceAll(pageable); }

    /**
     * 下一页切片
     *
     * @param example
     * @param pageable
     * @param <S>
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable) {
        return getAccessor().sliceAll(example, pageable);
    }

    /**
     * 查询
     *
     * @param ids
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> findAllById(Iterable<ID> ids) { return getAccessor().findAllById(ids); }

    /**
     * 查询
     *
     * @param first
     * @param second
     * @param ids
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> findAllById(ID first, ID second, ID... ids) {
        return getAccessor().findAllById(first, second, ids);
    }

    /**
     * 查询
     *
     * @param id
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) { return getAccessor().findById(id); }

    /**
     * 查询
     *
     * @param id
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public T getById(ID id) { return getAccessor().getById(id); }

    /**
     * 查询
     *
     * @param id
     * @param throwsMessageIfAbsent
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public T getById(ID id, String throwsMessageIfAbsent) {
        return getAccessor().getById(id, throwsMessageIfAbsent);
    }

    /**
     * 查询，抛出指定异常
     *
     * @param id
     * @param throwIfAbsent
     * @param <X>
     *
     * @return
     *
     * @throws X
     */
    @Override
    @Transactional(readOnly = true)
    public <X extends Throwable> T getById(ID id, Supplier<? extends X> throwIfAbsent) throws X {
        return getAccessor().getById(id, throwIfAbsent);
    }

    /**
     * 查询
     *
     * @param id
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public T getOne(ID id) { return getAccessor().getOne(id); }

    /**
     * 查询
     *
     * @param id
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public T getOrNull(ID id) { return getAccessor().getOrNull(id); }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteById(ID id) { getAccessor().deleteById(id); }

    /**
     * 删除
     *
     * @param entity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(T entity) { getAccessor().delete(entity); }

    /**
     * 删除
     *
     * @param entities
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteAll(Iterable<? extends T> entities) { getAccessor().deleteAll(entities); }

    /**
     * 删除
     *
     * @param first
     * @param second
     * @param entities
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteAll(T first, T second, T... entities) { getAccessor().deleteAll(first, second, entities); }
}
