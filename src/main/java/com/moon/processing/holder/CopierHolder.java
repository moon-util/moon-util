package com.moon.processing.holder;

import com.moon.processing.decl.CopierDeclared;
import com.moon.processing.decl.RecordDeclared;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class CopierHolder {

    private final RecordHolder recordHolder;

    private final Map<String, CopierDeclared> copierDeclaredMap = new HashMap<>();

    public CopierHolder(RecordHolder recordHolder) { this.recordHolder = recordHolder; }

    public CopierDeclared with(TypeElement thisElement, TypeElement thatElement) {
        String thisClass = Element2.getQualifiedName(thisElement);
        String thatClass = Element2.getQualifiedName(thatElement);
        String copierKey = toCopierKey(thisClass, thatClass);
        CopierDeclared copierDeclared = copierDeclaredMap.get(copierKey);
        if (copierDeclared != null) {
            return copierDeclared;
        }
        RecordDeclared thisDeclared = recordHolder.with(thisElement);
        RecordDeclared thatDeclared = recordHolder.with(thatElement);
        copierDeclared = new CopierDeclared(thisDeclared, thatDeclared);
        copierDeclaredMap.put(copierKey, copierDeclared);
        return copierDeclared;
    }

    private String toCopierKey(String thisClass, String thatClass) {
        return String.format("%s>%s", thisClass, thatClass);
    }
}
