package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.DataRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 仅内部使用
 *
 * @author moonsky
 */
@SuppressWarnings("all")
public interface JpaRecordRepository<T extends JpaRecord<ID>, ID>
    extends DataRepository<T, ID>, JpaRepositoryImplementation<T, ID> {

    @Override
    void disableById(ID id);

    @Override
    void disable(T entity);

    @Override
    void disableAll(Iterable<? extends T> entities);

    @Override
    <S extends T> void disableAll(S first, S second);

    @Override
    <S extends T> S insert(S entity);

    @Override
    List<T> saveAll(T first, T second);

    @Override
    Slice<T> sliceAll(Pageable pageable);

    @Override
    <S extends T> Slice<S> sliceAll(Example<S> example, Pageable pageable);

    @Override
    List<T> findAllById(ID first, ID second);

    @Override
    T getById(ID id);

    @Override
    T getById(ID id, String throwsMessageIfAbsent);

    @Override
    <X extends Throwable> T getById(ID id, Supplier<? extends X> throwIfAbsent) throws X;

    @Override
    T getOrNull(ID id);

    @Override
    void deleteAll(T first, T second);

    @Override
    List<T> findAll();

    @Override
    List<T> findAll(Sort sort);

    @Override
    List<T> findAllById(Iterable<ID> ids);

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
    T getOne(ID id);

    @Override
    <S extends T> List<S> findAll(Example<S> example);

    @Override
    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    @Override
    Page<T> findAll(Pageable pageable);

    @Override
    <S extends T> S save(S entity);

    @Override
    Optional<T> findById(ID id);

    @Override
    boolean existsById(ID id);

    @Override
    long count();

    @Override
    void deleteById(ID id);

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

    @Override
    void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata);

    @Override
    void setEscapeCharacter(EscapeCharacter escapeCharacter);

    @Override
    Optional<T> findOne(Specification<T> spec);

    @Override
    List<T> findAll(Specification<T> spec);

    @Override
    Page<T> findAll(Specification<T> spec, Pageable pageable);

    @Override
    List<T> findAll(Specification<T> spec, Sort sort);

    @Override
    long count(Specification<T> spec);
}
