package com.moon.mapping.processing;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.lang.model.element.TypeElement;

/**
 * @author benshaoye
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
final class BasicDefinition extends BaseDefinition<BasicMethod, BasicProperty> {

    public BasicDefinition(TypeElement enclosingElement) { super(enclosingElement); }
}
