package com.moon.core.util.validator;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author benshaoye
 */
interface IGroupValidator<M extends Map<K, C>, C extends Collection<E>, K, E, IMPL>
    extends IKeyedValidator<M, K, C, IMPL> {
    /**
     * 匹配验证每一项
     *
     * @param consumer
     * @return
     */
    IMPL requireForEach(BiConsumer<? super K, CollectValidator<C, E>> consumer);
}
