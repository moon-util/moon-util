package com.moon.mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author moonsky
 */
public interface BeanMapping<THIS, THAT> extends MapMapping<THIS> {

    /**
     * 将当前对象{@code thisObject}按属性名映射到另一个对象{@code thatObject}
     *
     * @param thisObject 属性数据源对象
     * @param thatObject 属性映射目标对象
     *
     * @return 设置属性后的目标对象 thatObject
     */
    THAT toThat(THIS thisObject, THAT thatObject);

    /**
     * 从目标属性数据源{@code thatObject}按属性名称复制到当前对象{@code thisObject}
     *
     * @param thisObject 当前对象，属性映射目标对象
     * @param thatObject 属性数据源对象
     *
     * @return 当前对象 thisObject
     */
    THIS fromThat(THIS thisObject, THAT thatObject);

    /**
     * 以对象{@code thatObject}为数据源，构造并复制属性到当前类实例对象
     *
     * @param thatObject 属性数据源对象
     *
     * @return 当前类实例
     */
    THIS newThis(THAT thatObject);

    /**
     * 以当前类为数据源，构造并复制属性到目标对象
     *
     * @param thisObject 当前类实例，属性数据源
     *
     * @return 目标对象
     */
    THAT newThat(THIS thisObject);

    default List<THIS> forEachNewThis(Iterable<THAT> thatIterable) {
        List<THIS> thisList = new ArrayList<>(Mappings.detectSize(thatIterable));
        if (thatIterable != null) {
            for (THAT that : thatIterable) {
                thisList.add(newThis(that));
            }
        }
        return thisList;
    }

    default List<THAT> forEachNewThat(Iterable<THIS> thisIterable) {
        List<THAT> thatList = new ArrayList<>(Mappings.detectSize(thisIterable));
        if (thisIterable != null) {
            for (THIS self : thisIterable) {
                thatList.add(newThat(self));
            }
        }
        return thatList;
    }
}
