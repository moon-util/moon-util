package com.moon.processing.file;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public abstract class BaseScoped<M extends Appender> extends BaseImportable implements Appender {

    private final boolean inInterface;
    private final Map<String, M> memberMap = new LinkedHashMap<>();

    protected BaseScoped(Importer importer, boolean inInterface) {
        super(importer);
        this.inInterface = inInterface;
    }

    protected final Map<String, M> getMemberMap() { return memberMap; }

    protected final boolean inInterface() { return inInterface; }

    public final void remove(String signature) {
        getMemberMap().remove(signature);
    }

    @Override
    public void appendTo(JavaAddr addr) {
        for (M member : getMemberMap().values()) {
            member.appendTo(addr);
        }
    }
}
