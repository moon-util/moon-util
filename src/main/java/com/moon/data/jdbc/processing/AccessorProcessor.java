package com.moon.data.jdbc.processing;

import com.moon.data.jdbc.annotation.Accessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * interface UsernameGetter {
 *     String getUsername();
 *     String getId();
 * }
 *
 * // 返回值可以是 void,boolean,int(等数字类型)
 * updateById(UsernameGetter getter); // update t_user set username = ? where id = ?
 * deleteById(UsernameGetter getter); // delete from t_user where id = ?
 * deleteByIdAndUsername(UsernameGetter getter); // delete from t_user where id = ? and username = ?
 * deleteByIdOrUsername(UsernameGetter getter); // delete from t_user where id = ? or username = ?
 *
 * interface UserInfoGetter extends UsernameGetter {
 *     String getPassword();
 * }
 *
 * // 返回值可以是 void,boolean,int(等数字类型)
 * updateById(UserInfoGetter getter); // update t_user set username = ?, password = ? where id = ?
 * updateUsernameById(UserInfoGetter getter); // update t_user set username = ? where id = ?
 * updateUsernameById(String id, String username); // update t_user set username = ? where id = ?
 * updateUsernameById(String username, String id); // update t_user set username = ? where id = ?
 * updateUsernameById(@P("username") String a, String id); // update t_user set username = ? where id = ?
 *
 * // 与参数顺序无关，但名称要一致: update t_user set username = ?, password = ? where id = ?
 * updateUsernamePasswordById(String username, String password, String id);
 *
 * // 查询
 * UserInfoGetter findById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter fetchById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter selectById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter queryById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter readById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter getById(String id); // select username, password, id from t_user where id = ?
 *
 * List&lt;UserInfoGetter&gt; findByUsernameLike(String username); // select username, password, id where username like '%username%'
 * List&lt;UserInfoGetter&gt; findByUsernameStartsWith(String username); // select username, password, id where username like 'username%'
 * List&lt;UserInfoGetter&gt; findByUsernameEndsWith(String username); // select username, password, id where username like '%username'
 * List&lt;UserInfoGetter&gt; findByAgeGt(int age); // select username, password, id where age > ?
 * List&lt;UserInfoGetter&gt; findByAgeGreatThan(int age); // select username, password, id where age > ?
 * List&lt;UserInfoGetter&gt; findByAgeLt(int age); // select username, password, id where age < ?
 * List&lt;UserInfoGetter&gt; findByAgeLessThan(int age); // select username, password, id where age < ?
 * List&lt;UserInfoGetter&gt; findByAgeLessThanAndUsernameIs(int age, String username); // select username, password, id where age < ? and username = ?
 * // 此情况所有条件都是 and 和 = : select username, password, id where age =? and username = ? and address = ?
 * List&lt;UserInfoGetter&gt; findBy(int age, String username, String address);
 *
 * insert(UserInfoGetter getter); // insert into t_user (id, username, password) values (?, ?, ?)
 * insert(String username); // insert into t_user (id, username) values (?, ?)
 *
 * @author benshaoye
 */
// @AutoService(Processor.class)
public class AccessorProcessor extends AbstractProcessor {

    private final static String ACCESSOR = Accessor.class.getCanonicalName();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supports = new HashSet<>();
        supports.add(ACCESSOR);
        return supports;
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        return false;
    }
}
