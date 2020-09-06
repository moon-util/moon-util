package com.moon.data.service;

import com.moon.data.Record;
import com.moon.data.accessor.DataAccessor;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface DataService<T extends Record<ID>, ID> extends BaseService<T, ID>, DataAccessor<T, ID> {}
