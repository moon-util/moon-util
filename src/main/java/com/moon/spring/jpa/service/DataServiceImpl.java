package com.moon.spring.jpa.service;

import com.moon.core.util.life.AfterLifeChain;
import com.moon.core.util.life.BeforeLifeChain;
import com.moon.core.util.life.NoneLifeChainEnum;
import com.moon.more.data.registry.LayerEnum;
import com.moon.spring.data.DataAccessorImpl;
import com.moon.spring.jpa.domain.JpaRecord;

import java.util.List;

import static com.moon.core.util.life.LifeUtil.after;
import static com.moon.core.util.life.LifeUtil.before;

/**
 * @author moonsky
 */
public abstract class DataServiceImpl<T extends JpaRecord<String>> extends DataAccessorImpl<String, T>
    implements DataService<T> {

    private final BeforeLifeChain DEFAULT_BEFORE = NoneLifeChainEnum.DEFAULT;
    private final AfterLifeChain DEFAULT_AFTER = NoneLifeChainEnum.DEFAULT;

    private BeforeLifeChain<T> beforeSave;
    private AfterLifeChain<T> afterSave;
    private BeforeLifeChain<T> beforeDelete;

    protected DataServiceImpl() { this(null); }

    protected DataServiceImpl(Class accessType) { super(accessType, null); }

    protected DataServiceImpl(Class accessType, Class domainClass) {
        super(accessType, LayerEnum.REPOSITORY, LayerEnum.SERVICE, domainClass);
    }

    protected DataServiceImpl(LayerEnum accessLay, Class domainClass) {
        this(accessLay, LayerEnum.SERVICE, domainClass);
    }

    protected DataServiceImpl(LayerEnum accessLay, LayerEnum registryLay, Class domainClass) {
        super(null, accessLay, registryLay, domainClass);
    }

    protected DataServiceImpl(
        Class accessServeClass, LayerEnum accessLay, LayerEnum registryMeLay, Class domainClass
    ) { super(accessServeClass, accessLay, registryMeLay, domainClass); }

    /*
     overrides
     */

    /**
     * 保存
     *
     * @param entity
     *
     * @return
     */
    @Override
    public <S extends T> S save(S entity) {
        return after(obtainAfterSave(), super.save(before(obtainBeforeSave(), entity)));
    }

    /**
     * 保存
     *
     * @param entity
     *
     * @return
     */
    @Override
    public <S extends T> S saveAndFlush(S entity) {
        return after(obtainAfterSave(), super.save(before(obtainBeforeSave(), entity)));
    }

    /**
     * 保存
     *
     * @param entities
     *
     * @return
     */
    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return after(obtainAfterSave(), super.saveAll(before(obtainBeforeSave(), entities)));
    }

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
    public List<T> saveAll(T first, T second, T... entities) {
        BeforeLifeChain<T> before = obtainBeforeSave();
        return after(obtainAfterSave(),
            super.saveAll(before(before, first), before(before, second), before(before, entities)));
    }

    /**
     * 删除
     *
     * @param entity
     */
    @Override
    public void delete(T entity) {
        super.delete(before(obtainBeforeDelete(), entity));
    }

    /**
     * 删除
     *
     * @param entities
     */
    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        super.deleteAll(before(obtainBeforeDelete(), entities));
    }

    /**
     * 删除
     *
     * @param first
     * @param second
     * @param entities
     */
    @Override
    public void deleteAll(T first, T second, T... entities) {
        BeforeLifeChain<T> before = obtainBeforeDelete();
        super.deleteAll(before(before, first), before(before, second), before(before, entities));
    }

    /**
     * 逻辑删除
     *
     * @param entity
     */
    @Override
    public void disable(T entity) {
        super.disable(before(obtainBeforeDelete(), entity));
    }

    /**
     * 逻辑删除
     *
     * @param entities
     */
    @Override
    public void disableAll(Iterable<? extends T> entities) {
        super.disableAll(before(obtainBeforeDelete(), entities));
    }

    /**
     * 逻辑删除
     *
     * @param first
     * @param second
     * @param entities
     */
    @Override
    public <S extends T> void disableAll(S first, S second, S... entities) {
        BeforeLifeChain<T> before = obtainBeforeDelete();
        super.disableAll(before(before, first), before(before, second), before(before, entities));
    }

    /*
     getter & setter
     */

    private BeforeLifeChain<T> obtainBeforeSave() {
        return defaultIfNull(getBeforeSave(), DEFAULT_BEFORE);
    }

    private AfterLifeChain<T> obtainAfterSave() {
        return defaultIfNull(getAfterSave(), DEFAULT_AFTER);
    }

    private BeforeLifeChain<T> obtainBeforeDelete() {
        return defaultIfNull(getBeforeDelete(), DEFAULT_BEFORE);
    }

    public BeforeLifeChain<T> getBeforeSave() { return beforeSave; }

    public void setBeforeSave(BeforeLifeChain<T> beforeSave) { this.beforeSave = beforeSave; }

    public AfterLifeChain<T> getAfterSave() { return afterSave; }

    public void setAfterSave(AfterLifeChain<T> afterSave) { this.afterSave = afterSave; }

    public BeforeLifeChain<T> getBeforeDelete() { return beforeDelete; }

    public void setBeforeDelete(BeforeLifeChain<T> beforeDelete) { this.beforeDelete = beforeDelete; }
}
