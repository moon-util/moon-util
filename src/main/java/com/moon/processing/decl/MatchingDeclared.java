package com.moon.processing.decl;

import com.moon.accessor.annotation.condition.IfMatching;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;

/**
 * @author benshaoye
 */
public class MatchingDeclared {

    private final TypeElement matchingAnnotationElem;
    private final TypeElement matcherElem;
    private final String matchingAnnotationName;
    private final String matcherClassname;
    private final IfMatching matching;

    public MatchingDeclared(
        TypeElement matchingAnnotationElem, TypeElement matcherElem, IfMatching matching
    ) {
        this.matchingAnnotationElem = matchingAnnotationElem;
        this.matcherElem = matcherElem;
        this.matching = matching;
        this.matchingAnnotationName = Element2.getQualifiedName(matchingAnnotationElem);
        this.matcherClassname = Element2.getQualifiedName(matcherElem);
    }

    public TypeElement getMatchingAnnotationElem() { return matchingAnnotationElem; }

    public TypeElement getMatcherElem() { return matcherElem; }

    public IfMatching getMatching() { return matching; }

    public String getMatchingAnnotationName() { return matchingAnnotationName; }

    public String getMatcherClassname() { return matcherClassname; }
}
