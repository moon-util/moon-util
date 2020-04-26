package com.moon.spring.jpa.service;

import com.moon.more.data.registry.LayerEnum;
import com.moon.spring.data.BaseRecordAccessor;
import com.moon.spring.jpa.JpaRecordable;

/**
 * @author benshaoye
 */
public abstract class RecordServiceImpl<T extends JpaRecordable<String>> extends BaseRecordAccessor<String, T>
    implements RecordService<T> {

    public RecordServiceImpl() { this(null); }

    public RecordServiceImpl(Class classname) { super(classname, LayerEnum.REPOSITORY); }
}
