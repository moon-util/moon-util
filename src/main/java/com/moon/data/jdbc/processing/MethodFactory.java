package com.moon.data.jdbc.processing;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.sql.DataSource;

/**
 * 普通 POJO 接口、抽象类实现
 * @author benshaoye
 */
final class MethodFactory {

    public Object parse(ExecutableElement elem, EntityModel model) {

        return parseForCreation(elem, model);
    }

    private Object parseForCreation(ExecutableElement elem,  EntityModel model) {
        // TODO for custom implementation

        DataSource dataSource = null;
        String name = StringUtils.getSimpleName(elem);
        if (Keywords.isInsert(name)) {
            // insertXxx
        } else if (Keywords.isSave(name)) {
            // saveXxx | if (有 id 字段) { 根据 id 值判断是 insert/update } else (无 id 字段) { 只 insert }
        } else if (Keywords.isUpdate(name)) {
            // update(Xxx)?(By)?(Xxx)?
            // update(username,id): update t_user set username = ?, id = ?
            // updateById(username,id): update t_user set username = ? where id = ?
            // updateUsernameById(username,password,id): update t_user set username = ? where id = ?
        } else if (Keywords.isUpdateNull(name)) {
            // clearXxxByXxx
        } else if (Keywords.isDelete(name)) {
            // deleteXxx
        } else if (Keywords.isSelect(name)) {
            // (select|fetch|query|find|read|search)Xxx
        } else if (Keywords.isSelectCount(name)) {
            // countXxx
        } else if (Keywords.isSelectExists(name)) {
            // existsXxx
        } else if (Keywords.isSelectGet(name)) {
            // getXxx
        } else if (Keywords.isSelectList(name)) {
            // listXxx
        }
        throw new IllegalStateException("Invalid method definition of: " + name);
    }
}
