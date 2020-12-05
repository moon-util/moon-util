package com.moon.data.accessor;

import com.moon.data.Record;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public interface BaseAccessor<T extends Record<ID>, ID> {

    /**
     * 总是用 insert 语句插入一条数据，这是为了支持手动指定 ID 而设计的
     * <p>
     * 有些少数场景可能希望不使用全局 ID 策略，而自己指定一个特殊的主键值，此时可以通过
     * <p>
     * 调用此方法达到目的，同时由于{@code insert}方法的开放性，也有些需要注意的：
     *
     * <pre>
     * 1. 全局主键策略一般会自带唯一性，手动指定也需要手动控制主键唯一，否则会主键冲突；
     * 2. 此方法也可以不用指定主键，此时应等价于{@link #save(Record)}（实际上也是 insert）；
     * </pre>
     *
     * @param entity 将要插入的对象
     * @param <S>    数据类型
     *
     * @return 返回插入数据后的对象
     */
    <S extends T> S insert(S entity);

    /**
     * 保存
     *
     * @param entity 将要保存的对象
     *
     * @return 返回保存后的对象；对于新对象，返回的对象 == 保存前的对象，且返回对象有 ID 值；
     * 对于旧对象，返回值就是保存前的对象
     */
    <S extends T> S save(S entity);

    /**
     * 保存
     *
     * @param entity 将要保存的对象
     *
     * @return
     */
    <S extends T> S saveAndFlush(S entity);

    /**
     * 保存
     *
     * @param first  将要保存的对象
     * @param second 将要保存的对象
     *
     * @return 保存后的对象
     */
    List<T> saveAll(T first, T second);

    /**
     * 保存
     *
     * @param entities 将要保存的对象
     *
     * @return 保存后的对象
     */
    <S extends T> List<S> saveAll(Iterable<S> entities);

    /**
     * 查询
     *
     * @return 返回所有记录结果
     */
    List<T> findAll();

    /**
     * 查询
     *
     * @param ids ID 列表
     *
     * @return 符合 ID 的所有对象
     */
    List<T> findAllById(Iterable<ID> ids);

    /**
     * 查询
     *
     * @param first  ID
     * @param second ID
     *
     * @return 符合 ID 的所有对象
     */
    List<T> findAllById(ID first, ID second);

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象
     */
    Optional<T> findById(ID id);

    /**
     * 表中是否存在对应 ID 存在的值
     *
     * @param id ID
     *
     * @return 如果存在 ID 匹配的对象返回 true；否则返回 false
     */
    boolean existsById(ID id);

    /**
     * 返回表总数
     *
     * @return 表中数据总数
     */
    long count();

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象，不存在是抛出异常
     *
     * @throws IllegalArgumentException 数据不存在
     */
    T getById(ID id);

    /**
     * 查询
     *
     * @param id                    ID
     * @param throwsMessageIfAbsent 异常消息
     *
     * @return ID 匹配的对象，不存在是抛出异常
     */
    T getById(ID id, String throwsMessageIfAbsent);

    /**
     * 查询，抛出指定异常
     *
     * @param id            ID
     * @param throwIfAbsent 异常
     * @param <X>           异常类型
     *
     * @return ID 匹配的对象，不存在是抛出异常
     *
     * @throws X 异常
     */
    <X extends Throwable> T getById(ID id, Supplier<? extends X> throwIfAbsent) throws X;

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象，不存在是抛出异常
     *
     * @throws EntityNotFoundException 异常
     */
    T getOne(ID id);

    /**
     * 查询
     *
     * @param id ID
     *
     * @return ID 匹配的对象，不存在时返回 null
     */
    T getOrNull(ID id);

    /**
     * 删除
     *
     * @param id ID
     */
    void deleteById(ID id);

    /**
     * 删除
     *
     * @param entity 将要删除的对象
     */
    void delete(T entity);

    /**
     * 删除
     *
     * @param entities 将要删除的对象
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * 删除
     *
     * @param first  将要删除的对象
     * @param second 将要删除的对象
     */
    void deleteAll(T first, T second);
}
