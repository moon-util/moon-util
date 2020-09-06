package com.moon.data.service;

import com.moon.data.Record;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface DataLongService<T extends Record<Long>> extends DataService<T, Long> {

    @Override
    void disableById(Long s);

    @Override
    void disable(T entity);

    @Override
    void disableAll(Iterable<? extends T> entities);

    @Override
    <S extends T> void disableAll(S first, S second, S... entities);

    @Override
    <S extends T> S save(S entity);

    @Override
    <S extends T> S saveAndFlush(S entity);

    @Override
    <S extends T> List<S> saveAll(Iterable<S> entities);

    @Override
    List<T> saveAll(T first, T second, T... entities);

    @Override
    List<T> findAll();

    @Override
    List<T> findAll(Sort sort);

    @Override
    Page<T> findAll(Pageable pageable);

    @Override
    <S extends T> Iterable<S> findAll(Example<S> example);

    @Override
    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    @Override
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    @Override
    Slice<T> sliceAll(Pageable pageable);

    @Override
    <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable);

    @Override
    List<T> findAllById(Iterable<Long> strings);

    @Override
    List<T> findAllById(Long first, Long second, Long... strings);

    @Override
    Optional<T> findById(Long s);

    @Override
    boolean existsById(Long id);

    @Override
    long count();

    @Override
    T getById(Long s);

    @Override
    T getById(Long s, String throwsMessageIfAbsent);

    @Override
    <X extends Throwable> T getById(Long s, Supplier<? extends X> throwIfAbsent) throws X;

    @Override
    T getOne(Long s);

    @Override
    T getOrNull(Long s);

    @Override
    void deleteById(Long s);

    @Override
    void delete(T entity);

    @Override
    void deleteAll(Iterable<? extends T> entities);

    @Override
    void deleteAll(T first, T second, T... entities);
}
