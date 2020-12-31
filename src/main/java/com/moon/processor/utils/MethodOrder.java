package com.moon.processor.utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author benshaoye
 */
public class MethodOrder implements Comparator<ExecutableElement> {

    public static final MethodOrder instance = new MethodOrder();

    private MethodOrder() {
    }

    private static String getName(Element elem) {
        return elem.getSimpleName().toString();
    }


    @SuppressWarnings("all")
    @Override
    public int compare(ExecutableElement a, ExecutableElement b) {
        int cmp = getName(a).compareTo(getName(b));
        if (cmp != 0) {
            return cmp;
        }
        List<? extends Element> aParams = a.getParameters();
        List<? extends Element> bParams = b.getParameters();
        if (Collect2.size(aParams) != Collect2.size(bParams)) {
            return Collect2.size(aParams) - Collect2.size(bParams);
        }
        int size = Collect2.size(aParams);
        Types types = Environment2.getTypes();
        for (int i = 0; i < size; i++) {
            final Element aparam = aParams.get(i);
            final Element bparam = bParams.get(i);
            if (Objects.equals(aparam, bparam)) {
                continue;
            }
            cmp = getName(aparam).compareTo(getName(bparam));
            if (cmp != 0) {
                return cmp;
            }

            TypeMirror aret = a.getReturnType();
            TypeMirror bret = b.getReturnType();
            if (Objects.equals(aret, bret)) {
                return 0;
            }

            // Super type comes last: Integer, Number, Object
            if (types.isSubtype(bret, aret)) {
                return 1;
            }
            if (types.isSubtype(aret, bret)) {
                return -1;
            }
            return aret.toString().compareTo(bret.toString());
        }
        return 0;
    }
}
