package com.moon.spring.jpa.repository;

import com.moon.more.data.Recordable;
import com.moon.spring.data.BaseAccessor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
@NoRepositoryBean
@SuppressWarnings("all")
public interface BaseRepository<T extends Recordable<String>>
    extends JpaRepository<T, String>, BaseAccessor<String, T> {

    @Override
    List<T> saveAll(T first, T second, T... entities);

    @Override
    Slice<T> sliceAll(Pageable pageable);

    @Override
    <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable);

    @Override
    List<T> findAllById(String first, String second, String... strings);

    @Override
    T getById(String s);

    @Override
    T getById(String s, String throwsMessageIfAbsent);

    @Override
    <X extends Throwable> T getById(String s, Supplier<? extends X> throwIfAbsent) throws X;

    @Override
    T getOrNull(String s);

    @Override
    void deleteAll(T first, T second, T... entities);

    @Override
    List<T> findAll();

    @Override
    List<T> findAll(Sort sort);

    @Override
    List<T> findAllById(Iterable<String> strings);

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
    T getOne(String s);

    @Override
    <S extends T> List<S> findAll(Example<S> example);

    @Override
    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    @Override
    Page<T> findAll(Pageable pageable);

    @Override
    <S extends T> S save(S entity);

    @Override
    Optional<T> findById(String s);

    @Override
    boolean existsById(String s);

    @Override
    long count();

    @Override
    void deleteById(String s);

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