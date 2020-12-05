package com.moon.data.accessor;

import com.moon.data.Record;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
@Transactional
@SuppressWarnings("all")
public abstract class BaseAccessorImpl<T extends Record<ID>, ID> extends AbstractAccessorImpl<T, ID>
    implements BaseAccessor<T, ID> {

    /**
     * 构造器
     *
     * @param accessServeClass 将要访问的服务具体实现类型，如：UserServiceImpl
     * @param domainClass      具体实体类型
     */
    protected BaseAccessorImpl(Class<? extends BaseAccessor<T, ID>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    /**
     * 是否存在指定{@code id}对应的数据
     *
     * @param id ID
     *
     * @return 如果存在返回 true，否则返回 false
     */
    @Override
    public boolean existsById(ID id) { return getAccessor().existsById(id); }

    /**
     * 返回表数据总数
     *
     * @return 总数
     */
    @Override
    public long count() { return getAccessor().count(); }

    /**
     * 插入
     *
     * @param entity 将要插入的对象
     * @param <S>    数据类型
     *
     * @return 返回插入数据后的对象
     */
    @Override
    @Transactional
    public <S extends T> S insert(S entity) { return getAccessor().insert(entity); }

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
     *
     * @return 保存后的对象
     */
    @Override
    @Transactional
    public List<T> saveAll(T first, T second) { return getAccessor().saveAll(first, second); }

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
     *
     * @return ID 匹配的对象
     */
    @Override
    @Transactional
    public List<T> findAllById(ID first, ID second) {
        return getAccessor().findAllById(first, second);
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
     */
    @Override
    @Transactional
    public void deleteAll(T first, T second) { getAccessor().deleteAll(first, second); }
}
