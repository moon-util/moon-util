package com.moon.processing.holder;

import com.moon.accessor.annotation.condition.IfMatching;
import com.moon.processing.decl.MatchingDeclared;

import javax.lang.model.element.TypeElement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class MatchingHolder {

    private final Map<String, MatchingDeclared> matchingDeclaredMap = new LinkedHashMap<>();

    public MatchingHolder() { }

    public MatchingDeclared with(TypeElement matchingAnnotationElem, TypeElement matcherElem, IfMatching matching) {
        MatchingDeclared declared = new MatchingDeclared(matchingAnnotationElem, matcherElem, matching);
        matchingDeclaredMap.put(declared.getMatchingAnnotationName(), declared);
        return declared;
    }
}
