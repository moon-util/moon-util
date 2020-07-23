package com.moon.spring.jpa.service;

import com.moon.spring.data.BaseAccessor;
import com.moon.spring.jpa.domain.JpaRecord;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface BaseService<T extends JpaRecord<String>> extends BaseAccessor<String, T> {

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
    List<T> findAllById(Iterable<String> strings);

    @Override
    List<T> findAllById(String first, String second, String... strings);

    @Override
    Optional<T> findById(String s);

    @Override
    boolean existsById(String id);

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
    void deleteAll(T first, T second, T... entities);
}
