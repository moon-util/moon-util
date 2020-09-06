package com.moon.data.accessor;

import com.moon.data.Record;
import com.moon.data.registry.LayerEnum;
import com.moon.data.registry.LayerRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
@Transactional
@SuppressWarnings("all")
public abstract class BaseAccessorImpl<T extends Record<ID>, ID> implements BaseAccessor<T, ID>, InitializingBean {

    private static boolean isExtendOf(Class thisClass, Class superClass) {
        return thisClass != null && superClass != null && superClass.isAssignableFrom(thisClass);
    }

    protected static boolean isAccessorType(Class cls) { return isExtendOf(cls, BaseAccessor.class); }

    protected static boolean isRecordableType(Class cls) { return isExtendOf(cls, Record.class); }

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
    private ApplicationContext context;
    @Autowired(required = false)
    private WebApplicationContext webContext;

    protected final Class domainClass;
    private BaseAccessor<T, ID> accessor;

    /**
     * 构造器
     *
     * @param accessServeClass 将要访问的服务具体实现类型，如：UserServiceImpl
     * @param domainClass      具体实体类型
     */
    protected BaseAccessorImpl(
        Class<? extends BaseAccessor<T, ID>> accessServeClass, Class<T> domainClass
    ) {
        Class access = accessServeClass, domain = domainClass;

        if (domain == null) {
            domain = deduceDomainClass();
        } else if (!isRecordableType(domain)) {
            if (isAccessorType(domain)) {
                Class tempClass = null;
                if (!isAccessorType(access)) {
                    tempClass = access;
                    access = domain;
                }
                if (tempClass != null && isRecordableType(tempClass)) {
                    domain = tempClass;
                }
            } else {
                domain = deduceDomainClass();
            }
        }

        if (access == null || !isAccessorType(access)) {
            if (domain == null && isRecordableType(access)) {
                domain = access;
            }
            access = null;
        }

        this.domainClass = domain == null ? deduceDomainClass() : domain;
        LayerRegistry.registry(provideThisLayer(), getDomainClass(), this);
        AccessorRegistration.registry(getInjectRunner(access));
    }

    protected Runnable getInjectRunner(Class<? extends BaseAccessor> accessServeClass) {
        return () -> {
            BaseAccessor accessor = provideDefaultAccessor();
            if (accessor == null && accessServeClass != null) {
                accessor = getContext().getBean(accessServeClass);
            }
            if (accessor == null) {
                accessor = LayerRegistry.get(pullingAccessLayer(), getDomainClass());
            }
            if (this.accessor == null) {
                this.accessor = accessor;
            }
        };
    }

    public Class getDomainClass() { return domainClass; }

    protected ApplicationContext getContext() { return context; }

    protected WebApplicationContext getWebContext() {
        WebApplicationContext webContext = this.webContext;
        if (webContext != null) {
            return webContext;
        }
        ApplicationContext context = this.getContext();
        if (context instanceof WebApplicationContext) {
            return (WebApplicationContext) context;
        }
        return null;
    }

    protected BaseAccessor<T, ID> getAccessor() { return accessor; }

    /**
     * like getRepository
     * like getService
     * like getMapper
     * <p>
     * and so on...
     *
     * @return
     */
    protected BaseAccessor<T, ID> provideDefaultAccessor() { return null; }

    /**
     * 告诉注册器，“我”是哪一层
     *
     * @return
     */
    protected abstract LayerEnum provideThisLayer();

    /**
     * 我要访问的是哪一层
     * <p>
     * 通常：controller 访问 service，service 访问 repository/mapper层
     *
     * @return
     */
    protected abstract LayerEnum pullingAccessLayer();

    @Override
    public void afterPropertiesSet() throws Exception { }

    @Override
    public boolean existsById(ID id) { return getAccessor().existsById(id); }

    @Override
    public long count() { return getAccessor().count(); }

    /**
     * 保存
     *
     * @param entity 将要保存的对象
     *
     * @return 保存后的对象
     */
    @Override
    @Transactional
    public <S extends T> S save(S entity) { return getAccessor().save(entity); }

    /**
     * 保存
     *
     * @param entity 将要保存的对象
     *
     * @return 保存后的对象
     */
    @Override
    @Transactional
    public <S extends T> S saveAndFlush(S entity) { return getAccessor().saveAndFlush(entity); }

    /**
     * 保存
     *
     * @param entities 将要保存的对象
     *
     * @return 保存后的对象
     */
    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) { return getAccessor().saveAll(entities); }

    /**
     * 保存
     *
     * @param first    将要保存的对象
     * @param second   将要保存的对象
     * @param entities 将要保存的对象
     *
     * @return 保存后的对象
     */
    @Override
    @Transactional
    public List<T> saveAll(T first, T second, T... entities) { return getAccessor().saveAll(first, second, entities); }

    /**
     * 查询
     *
     * @return 所有对象
     */
    @Override
    @Transactional
    public List<T> findAll() { return getAccessor().findAll(); }

    /**
     * 查询
     *
     * @param sort 排序
     *
     * @return 排序后的所有对象
     */
    @Override
    @Transactional
    public List<T> findAll(Sort sort) { return getAccessor().findAll(sort); }

    /**
     * 查询
     *
     * @param pageable 分页
     *
     * @return 所有分页对象
     */
    @Override
    @Transactional
    public Page<T> findAll(Pageable pageable) { return getAccessor().findAll(pageable); }

    /**
     * 查询
     *
     * @param example
     * @param <S>
     *
     * @return 所有符合条件的对象
     */
    @Override
    @Transactional
    public <S extends T> Iterable<S> findAll(Example<S> example) { return getAccessor().findAll(example); }

    /**
     * 查询
     *
     * @param example
     * @param sort    排序
     * @param <S>
     *
     * @return 排序后的所有对象
     */
    @Override
    @Transactional
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) { return getAccessor().findAll(example, sort); }

    /**
     * 查询
     *
     * @param example
     * @param pageable 分页
     * @param <S>
     *
     * @return 分页对象
     */
    @Override
    @Transactional
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return getAccessor().findAll(example, pageable);
    }

    /**
     * 下一页切片
     *
     * @param pageable 分页
     *
     * @return 分页对象
     */
    @Override
    @Transactional
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
    @Transactional
    public <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable) {
        return getAccessor().sliceAll(example, pageable);
    }

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象
     */
    @Override
    @Transactional
    public List<T> findAllById(Iterable<ID> ids) { return getAccessor().findAllById(ids); }

    /**
     * 查询
     *
     * @param first  ID
     * @param second ID
     * @param id     ID
     *
     * @return ID 匹配的对象
     */
    @Override
    @Transactional
    public List<T> findAllById(ID first, ID second, ID... ids) {
        return getAccessor().findAllById(first, second, ids);
    }

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象
     */
    @Override
    @Transactional
    public Optional<T> findById(ID id) { return getAccessor().findById(id); }

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象
     */
    @Override
    @Transactional
    public T getById(ID id) { return getAccessor().getById(id); }

    /**
     * 查询
     *
     * @param id                    ID
     * @param throwsMessageIfAbsent 异常消息
     *
     * @return ID 匹配的对象
     */
    @Override
    @Transactional
    public T getById(ID id, String throwsMessageIfAbsent) {
        return getAccessor().getById(id, throwsMessageIfAbsent);
    }

    /**
     * 查询，抛出指定异常
     *
     * @param id            ID
     * @param throwIfAbsent 异常
     * @param <X>           异常类型
     *
     * @return ID 匹配的对象
     *
     * @throws X 异常
     */
    @Override
    @Transactional
    public <X extends Throwable> T getById(ID id, Supplier<? extends X> throwIfAbsent) throws X {
        return getAccessor().getById(id, throwIfAbsent);
    }

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象
     *
     * @throws EntityNotFoundException 当 id 无法匹配到对象是抛出异常
     */
    @Override
    @Transactional
    public T getOne(ID id) { return getAccessor().getOne(id); }

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象或 null
     */
    @Override
    @Transactional
    public T getOrNull(ID id) { return getAccessor().getOrNull(id); }

    /**
     * 删除
     *
     * @param id ID
     */
    @Override
    @Transactional
    public void deleteById(ID id) { getAccessor().deleteById(id); }

    /**
     * 删除
     *
     * @param entity 将要删除的对象
     */
    @Override
    @Transactional
    public void delete(T entity) { getAccessor().delete(entity); }

    /**
     * 删除
     *
     * @param entities 将要删除的对象
     */
    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) { getAccessor().deleteAll(entities); }

    /**
     * 删除
     *
     * @param first    将要删除的对象
     * @param second   将要删除的对象
     * @param entities 将要删除的对象
     */
    @Override
    @Transactional
    public void deleteAll(T first, T second, T... entities) { getAccessor().deleteAll(first, second, entities); }
}
