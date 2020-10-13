package com.moon.data.accessor;

import com.moon.core.model.BaseSupporter;
import com.moon.data.Record;
import org.springframework.data.domain.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public interface BaseAccessor<T extends Record<ID>, ID> extends BaseSupporter {

    /**
     * 总是用 insert 语句插入一条数据
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
     * @param entities 将要保存的对象
     *
     * @return 保存后的对象
     */
    <S extends T> List<S> saveAll(Iterable<S> entities);

    /**
     * 保存
     *
     * @param first    将要保存的对象
     * @param second   将要保存的对象
     * @param entities 将要保存的对象
     *
     * @return 保存后的对象
     */
    List<T> saveAll(T first, T second, T... entities);

    /**
     * 查询
     *
     * @return 返回所有记录结果
     */
    List<T> findAll();

    /**
     * 查询
     *
     * @param sort 排序方式
     *
     * @return 返回排序后的所有对象
     */
    List<T> findAll(Sort sort);

    /**
     * 查询
     *
     * @param pageable 分页
     *
     * @return 返回分页结果
     */
    Page<T> findAll(Pageable pageable);

    /**
     * 查询
     *
     * @param example
     * @param <S>
     *
     * @return 符合条件的所有对象
     */
    <S extends T> Iterable<S> findAll(Example<S> example);

    /**
     * 查询
     *
     * @param example
     * @param sort    排序方式
     * @param <S>
     *
     * @return 符合条件的所有对象
     */
    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    /**
     * 查询
     *
     * @param example
     * @param pageable 分页
     * @param <S>
     *
     * @return 符合条件的所有分页对象
     */
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    /**
     * 下一页切片
     *
     * @param pageable 分页
     *
     * @return 所有符合条件的片段，与 page 的区别是，page 会返回总数，这个不需要返回数据总数
     */
    Slice<T> sliceAll(Pageable pageable);

    /**
     * 下一页切片
     *
     * @param example
     * @param pageable 分页
     * @param <S>
     *
     * @return 所有符合条件的片段，与 page 的区别是，page 会返回总数，这个不需要返回数据总数
     */
    <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable);

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
     * @param ids    ID 列表
     *
     * @return 符合 ID 的所有对象
     */
    List<T> findAllById(ID first, ID second, ID... ids);

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
     * @param first    将要删除的对象
     * @param second   将要删除的对象
     * @param entities 将要删除的对象
     */
    void deleteAll(T first, T second, T... entities);
}
