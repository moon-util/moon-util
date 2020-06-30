package com.moon.core.lang.ref;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class BooleanAccessorTestTest {

    @Test
    void testOfFalse() {
        FinalAccessor<String> accessor = FinalAccessor.of();
        BooleanAccessor.ofFalse()
            .flip().ifTrue(() ->
            accessor.set("今天天气很棒"))
            .setTrue().ifTrue(() ->
            accessor.set("明天天气很棒"));

        System.out.println(accessor);
    }
}