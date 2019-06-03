package com.moon.notice;

import com.moon.core.util.ListUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public class NoticeParametersImpl implements NoticeParameters {

    private final static NoticeParameter[] EMPTY = new NoticeParameter[0];

    private List<NoticeParameter> parameters;

    private List<NoticeParameter> create() { return create(16); }

    private List<NoticeParameter> create(int capacity) { return new ArrayList<>(capacity); }

    private List<NoticeParameter> ensureParameters() {
        return parameters == null ? (parameters = create()) : parameters;
    }

    /**
     * 返回所有参数
     *
     * @return
     */
    @Override
    public NoticeParameter[] getParameters() {
        return ensureParameters().toArray(EMPTY);
    }

    /**
     * 添加
     *
     * @param parameter
     *
     * @return
     */
    @Override
    public NoticeParametersImpl add(NoticeParameter parameter) {
        ListUtil.add(ensureParameters(), parameter);
        return this;
    }

    /**
     * 添加所有
     *
     * @param parameters
     *
     * @return
     */
    @Override
    public NoticeParametersImpl addAll(NoticeParameter... parameters) {
        ListUtil.addAll(ensureParameters(), parameters);
        return this;
    }

    /**
     * 删除第一个
     *
     * @param parameter
     *
     * @return
     */
    @Override
    public NoticeParametersImpl remove(NoticeParameter parameter) {
        ensureParameters().remove(parameter);
        return this;
    }

    /**
     * 删除所有匹配的
     *
     * @param tester
     *
     * @return
     */
    @Override
    public NoticeParametersImpl removeAll(Predicate<? super NoticeParameter> tester) {
        List<NoticeParameter> parameters = ensureParameters();
        List<NoticeParameter> now = create(parameters.size());
        for (NoticeParameter param : parameters) {
            if (!tester.test(param)) {
                now.add(param);
            }
        }
        this.parameters = now;
        return this;
    }

    @Override
    public Map toMap() {
        List<NoticeParameter> parameters = ensureParameters();
        Map ret = new HashMap(parameters.size());
        for (NoticeParameter param : parameters) {
            ret.put(param.name(), param.value());
        }
        return ret;
    }

    @Override
    public List toList() {
        return ensureParameters().stream().map(param -> param.value()).collect(Collectors.toList());
    }

    @Override
    public String[] toArray() {
        List<NoticeParameter> parameters = ensureParameters();
        String[] ret = new String[parameters.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = parameters.get(i).value();
        }
        return ret;
    }
}
