package com.moon.accessor;

import com.moon.data.IdentifierGenerator;

import java.io.Serializable;

/**
 * ID 注入器
 * <p>
 * 如果实现类是{@link IdentifierInjector}将不会主动调用{@link IdentifierInjector#generateId(Object, Object)}方法
 * 如果实现类是{@link IdentifierGenerator}就调用{@link IdentifierGenerator#generateId(Object, Object)}方法，
 * 并把返回值{@code id}设置到实体{@code object}
 *
 * @author benshaoye
 */
public interface IdentifierInjector<T, ID extends Serializable, METADATA> extends IdentifierGenerator<ID, METADATA> {

    /**
     * 给 object 设置主键
     * <p>
     * [IdentifierInjector 主键策略不会主动调用 generateId]
     * <p>
     * {@link IdentifierGenerator}通常可用于给单主键设置，执行 insert 语句时，自动将 ID 设置到实体上
     * {@link IdentifierInjector}可用于给任意主键策略设置，因为这里需要主动将 id 设置到 object 里，如
     *
     * <pre>
     * public class IdInjector implements IdentifierInjector {
     *
     *     public void injectId(T object, METADATA metadata) {
     *         // 可以这样: object.setId(generateId(object, metadata));
     *
     *         // 或者: object.setId( <自定义主键值> );
     *
     *         // 或者联合主键策略可以这样用:
     *         // object.setId1("id1");
     *         // object.setId2("id2");
     *         // 总之是需要你主动将主键设置到 object
     *     }
     *
     *     public ID generateId(Object entity, METADATA metadata) {
     *         return "id";
     *     }
     * }
     * </pre>
     *
     * @param object   实体类
     * @param metadata 元信息
     *
     * @return
     */
    void injectId(T object, METADATA metadata);

    /**
     * 生成 ID
     *
     * @param entity   数据实体
     * @param metadata 元信息
     *
     * @return
     */
    @Override
    ID generateId(Object entity, METADATA metadata);
}
