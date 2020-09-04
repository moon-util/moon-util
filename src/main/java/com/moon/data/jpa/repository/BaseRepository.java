package com.moon.data.jpa.repository;

import com.moon.data.accessor.BaseAccessor;
import com.moon.data.jpa.JpaRecord;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
@NoRepositoryBean
public interface BaseRepository<T extends JpaRecord<ID>, ID> extends JpaRepository<T, ID>, BaseAccessor<T, ID> {

    @Override
    List<T> saveAll(T first, T second, T... entities);

    @Override
    Slice<T> sliceAll(Pageable pageable);

    @Override
    <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable);

    @Override
    List<T> findAllById(ID first, ID second, ID... strings);

    @Override
    T getById(ID s);

    @Override
    T getById(ID s, String throwsMessageIfAbsent);

    @Override
    <X extends Throwable> T getById(ID s, Supplier<? extends X> throwIfAbsent) throws X;

    @Override
    T getOrNull(ID s);

    @Override
    void deleteAll(T first, T second, T... entities);

    @Override
    List<T> findAll();

    @Override
    List<T> findAll(Sort sort);

    @Override
    List<T> findAllById(Iterable<ID> strings);

    @Override
    <S extends T> List<S> saveAll(Iterable<S> entities);

    @Override
    void flush();

    @Override
    <S extends T> S saveAndFlush(S entity);

    @Override
    void deleteInBatch(Iterable<T> entities);

    @Override
    void deleteAllInBatch();

    @Override
    T getOne(ID s);

    @Override
    <S extends T> List<S> findAll(Example<S> example);

    @Override
    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    @Override
    Page<T> findAll(Pageable pageable);

    @Override
    <S extends T> S save(S entity);

    @Override
    Optional<T> findById(ID s);

    @Override
    boolean existsById(ID s);

    @Override
    long count();

    @Override
    void deleteById(ID s);

    @Override
    void delete(T entity);

    @Override
    void deleteAll(Iterable<? extends T> entities);

    @Override
    void deleteAll();

    @Override
    <S extends T> Optional<S> findOne(Example<S> example);

    @Override
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    @Override
    <S extends T> long count(Example<S> example);

    @Override
    <S extends T> boolean exists(Example<S> example);
}
