package com.moon.data.service;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;
import com.moon.data.accessor.DataAccessor;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 文档参考{@link com.moon.data.accessor.BaseAccessor}和{@link DataAccessor}
 * 这里是为了一屏能展示更多方法
 *
 * @author moonsky
 * @see com.moon.data.accessor.BaseAccessor
 * @see DataAccessor
 */
@SuppressWarnings("all")
public interface BaseStringService<T extends Record<String>> extends BaseAccessor<T, String> {

    @Override
    <S extends T> S insert(S entity);

    @Override
    <S extends T> S save(S entity);

    @Override
    <S extends T> S saveAndFlush(S entity);

    @Override
    List<T> saveAll(T first, T second);

    @Override
    <S extends T> List<S> saveAll(Iterable<S> entities);

    @Override
    List<T> findAll();

    @Override
    List<T> findAllById(Iterable<String> strings);

    @Override
    List<T> findAllById(String first, String second);

    @Override
    Optional<T> findById(String s);

    @Override
    boolean existsById(String s);

    @Override
    long count();

    @Override
    T getById(String s);

    @Override
    T getById(String s, String throwsMessageIfAbsent);

    @Override
    <X extends Throwable> T getById(String s, Supplier<? extends X> throwIfAbsent) throws X;

    @Override
    T getOne(String s);

    @Override
    T getOrNull(String s);

    @Override
    void deleteById(String s);

    @Override
    void delete(T entity);

    @Override
    void deleteAll(Iterable<? extends T> entities);

    @Override
    void deleteAll(T first, T second);
}
