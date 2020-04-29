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
public interface BaseRepository<T extends Recordable<String>>
    extends JpaRepository<T, String>, BaseAccessor<String, T> {

    /**
     * 保存
     *
     * @param first
     * @param second
     * @param entities
     *
     * @return
     */
    @Override
    List<T> saveAll(T first, T second, T... entities);

    /**
     * 下一页切片
     *
     * @param pageable
     *
     * @return
     */
    @Override
    Slice<T> sliceAll(Pageable pageable);

    /**
     * 下一页切片
     *
     * @param example
     * @param pageable
     *
     * @return
     */
    @Override
    <S extends T> Slice<S> sliceAll(
        Example<S> example, Pageable pageable
    );

    /**
     * 查询
     *
     * @param first
     * @param second
     * @param strings
     *
     * @return
     */
    @Override
    List<T> findAllById(String first, String second, String... strings);

    /**
     * 查询
     *
     * @param s
     *
     * @return
     *
     * @throws IllegalArgumentException 数据不存在
     */
    @Override
    T getById(String s);

    /**
     * 查询
     *
     * @param s
     * @param throwsMessageIfAbsent
     *
     * @return
     */
    @Override
    T getById(String s, String throwsMessageIfAbsent);

    /**
     * 查询，抛出指定异常
     *
     * @param s
     * @param throwIfAbsent
     *
     * @return
     *
     * @throws X
     */
    @Override
    <X extends Throwable> T getById(String s, Supplier<? extends X> throwIfAbsent) throws X;

    /**
     * 查询
     *
     * @param s
     *
     * @return
     */
    @Override
    T getOrNull(String s);

    /**
     * 删除
     *
     * @param first
     * @param second
     * @param entities
     */
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

    /**
     * Flushes all pending changes to the database.
     */
    @Override
    void flush();

    /**
     * Saves an entity and flushes changes instantly.
     *
     * @param entity
     *
     * @return the saved entity
     */
    @Override
    <S extends T> S saveAndFlush(S entity);

    /**
     * Deletes the given entities in a batch which means it will create a single {@link Query}. Assume that we will
     * clear
     * the {@link EntityManager} after the call.
     *
     * @param entities
     */
    @Override
    void deleteInBatch(Iterable<T> entities);

    /**
     * Deletes all entities in a batch call.
     */
    @Override
    void deleteAllInBatch();

    /**
     * Returns a reference to the entity with the given identifier. Depending on how the JPA persistence provider is
     * implemented this is very likely to always return an instance and throw an
     * {@link EntityNotFoundException} on first access. Some of them will reject invalid identifiers
     * immediately.
     *
     * @param s must not be {@literal null}.
     *
     * @return a reference to the entity with the given identifier.
     *
     * @see EntityManager#getReference(Class, Object) for details on when an exception is thrown.
     */
    @Override
    T getOne(String s);

    @Override
    <S extends T> List<S> findAll(Example<S> example);

    @Override
    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     *
     * @return a page of entities
     */
    @Override
    Page<T> findAll(Pageable pageable);

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed
     * the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     *
     * @return the saved entity; will never be {@literal null}.
     *
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    @Override
    <S extends T> S save(S entity);

    /**
     * Retrieves an entity by its id.
     *
     * @param s must not be {@literal null}.
     *
     * @return the entity with the given id or {@literal Optional#empty()} if none found.
     *
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Override
    Optional<T> findById(String s);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param s must not be {@literal null}.
     *
     * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
     *
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Override
    boolean existsById(String s);

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    @Override
    long count();

    /**
     * Deletes the entity with the given id.
     *
     * @param s must not be {@literal null}.
     *
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Override
    void deleteById(String s);

    /**
     * Deletes a given entity.
     *
     * @param entity must not be {@literal null}.
     *
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    @Override
    void delete(T entity);

    /**
     * Deletes the given entities.
     *
     * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
     *
     * @throws IllegalArgumentException in case the given {@literal entities} or one of its entities is {@literal null}.
     */
    @Override
    void deleteAll(Iterable<? extends T> entities);

    /**
     * Deletes all entities managed by the repository.
     */
    @Override
    void deleteAll();

    /**
     * Returns a single entity matching the given {@link Example} or {@literal null} if none was found.
     *
     * @param example must not be {@literal null}.
     *
     * @return a single entity matching the given {@link Example} or {@link Optional#empty()} if none was found.
     *
     * @throws IncorrectResultSizeDataAccessException if the Example yields more than one result.
     */
    @Override
    <S extends T> Optional<S> findOne(Example<S> example);

    /**
     * Returns a {@link Page} of entities matching the given {@link Example}. In case no match could be found, an empty
     * {@link Page} is returned.
     *
     * @param example  must not be {@literal null}.
     * @param pageable can be {@literal null}.
     *
     * @return a {@link Page} of entities matching the given {@link Example}.
     */
    @Override
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    /**
     * Returns the number of instances matching the given {@link Example}.
     *
     * @param example the {@link Example} to count instances for. Must not be {@literal null}.
     *
     * @return the number of instances matching the {@link Example}.
     */
    @Override
    <S extends T> long count(Example<S> example);

    /**
     * Checks whether the data store contains elements that match the given {@link Example}.
     *
     * @param example the {@link Example} to use for the existence check. Must not be {@literal null}.
     *
     * @return {@literal true} if the data store contains elements that match the given {@link Example}.
     */
    @Override
    <S extends T> boolean exists(Example<S> example);
}
