package com.moon.core.lang.ref;

import java.util.Map;

/**
 * 二维键值对管理（坐标）
 *
 * @author ZhangDongMin
 * @date 2018/9/11
 */
public final class WeakCoordinate<X, Y, Z> extends FinalCoordinate<X, Y, Z> {

    protected final static int DEFAULT_CAPACITY = 16;
    protected final static Boolean IS_MANAGE = false;
    protected final static Boolean TRUE = true;

    /**
     * 获得一个被管理的缓存坐标对象
     *
     * @param <X>
     * @param <Y>
     * @param <Z>
     * @return
     */
    public static <X, Y, Z> WeakCoordinate<X, Y, Z> manageOne() { return new WeakCoordinate(DEFAULT_CAPACITY, TRUE); }

    /**
     * 获得一个普通缓存坐标对象
     *
     * @param <X>
     * @param <Y>
     * @param <Z>
     * @return
     */
    public static <X, Y, Z> WeakCoordinate<X, Y, Z> one() { return new WeakCoordinate(); }

    protected WeakCoordinate() { this(DEFAULT_CAPACITY, IS_MANAGE); }

    private WeakCoordinate(int capacity, boolean isManage) {
        super(capacity,isManage);
        this.clear();
    }

    @Override
    protected Map<Object, Object> newSub(int capacity) {
        return isManage ? ReferenceUtil.manageMap(capacity) : ReferenceUtil.manageMap();
    }
}
