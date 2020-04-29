package com.moon.spring.data;

import com.moon.more.model.BaseSupporter;
import com.moon.more.model.id.IdSupplier;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public interface BaseAccessor<ID,T extends IdSupplier<ID>> extends BaseSupporter {

    /**
     * 保存
     *
     * @param entity
     *
     * @return
     */
    <S extends T> S save(S entity);

    /**
     * 保存
     *
     * @param entity
     *
     * @return
     */
    <S extends T> S saveAndFlush(S entity);

    /**
     * 保存
     *
     * @param entities
     *
     * @return
     */
    <S extends T> List<S> saveAll(Iterable<S> entities);

    /**
     * 保存
     *
     * @param first
     * @param second
     * @param entities
     *
     * @return
     */
    List<T> saveAll(T first, T second, T... entities);

    /**
     * 查询
     *
     * @return
     */
    List<T> findAll();

    /**
     * 查询
     *
     * @param sort
     *
     * @return
     */
    List<T> findAll(Sort sort);

    /**
     * 查询
     *
     * @param pageable
     *
     * @return
     */
    Page<T> findAll(Pageable pageable);

    /**
     * 查询
     *
     * @param example
     * @param <S>
     *
     * @return
     */
    <S extends T> Iterable<S> findAll(Example<S> example);

    /**
     * 查询
     *
     * @param example
     * @param sort
     * @param <S>
     *
     * @return
     */
    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    /**
     * 查询
     *
     * @param example
     * @param pageable
     * @param <S>
     *
     * @return
     */
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    /**
     * 下一页切片
     *
     * @param pageable
     *
     * @return
     */
    Slice<T> sliceAll(Pageable pageable);

    /**
     * 下一页切片
     *
     * @param example
     * @param pageable
     * @param <S>
     *
     * @return
     */
    <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable);

    /**
     * 查询
     *
     * @param ids
     *
     * @return
     */
    List<T> findAllById(Iterable<ID> ids);

    /**
     * 查询
     *
     * @param first
     * @param second
     * @param ids
     *
     * @return
     */
    List<T> findAllById(ID first, ID second, ID... ids);

    /**
     * 查询
     *
     * @param id
     *
     * @return
     */
    Optional<T> findById(ID id);

    /**
     * 查询
     *
     * @param id
     *
     * @return
     *
     * @throws IllegalArgumentException 数据不存在
     */
    T getById(ID id);

    /**
     * 查询
     *
     * @param id
     * @param throwsMessageIfAbsent
     *
     * @return
     */
    T getById(ID id, String throwsMessageIfAbsent);

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
    <X extends Throwable> T getById(ID id, Supplier<? extends X> throwIfAbsent) throws X;

    /**
     * 查询
     *
     * @param id
     *
     * @return
     */
    T getOne(ID id);

    /**
     * 查询
     *
     * @param id
     *
     * @return
     */
    T getOrNull(ID id);

    /**
     * 删除
     *
     * @param id
     */
    void deleteById(ID id);

    /**
     * 删除
     *
     * @param entity
     */
    void delete(T entity);

    /**
     * 删除
     *
     * @param entities
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * 删除
     *
     * @param first
     * @param second
     * @param entities
     */
    void deleteAll(T first, T second, T... entities);
}
