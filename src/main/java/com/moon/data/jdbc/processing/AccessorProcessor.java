package com.moon.data.jdbc.processing;

import com.google.auto.service.AutoService;
import com.moon.data.jdbc.annotation.Accessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * interface UsernameGetter {
 * String getUsername();
 * String getId();
 * }
 * <p>
 * // 返回值可以是 void,boolean,int(等数字类型)
 * updateById(UsernameGetter getter); // update t_user set username = ? where id = ?
 * deleteById(UsernameGetter getter); // delete from t_user where id = ?
 * deleteByIdAndUsername(UsernameGetter getter); // delete from t_user where id = ? and username = ?
 * deleteByIdOrUsername(UsernameGetter getter); // delete from t_user where id = ? or username = ?
 * <p>
 * clearUsername(); // update t_user set username = NULL
 * clearUsername(String id); // update t_user set username = NULL where id = ?
 * clearPassword(String username); // update t_user set password = NULL where username = ?
 * clearPasswordByUsernameLike(String username); // update t_user set password = NULL where username = '%username%'
 * clearPasswordByUsernameStartsWith(String username); // update t_user set password = NULL where username = 'username%'
 * clearUsernameById(String id); // update t_user set username = NULL where id = ?
 * <p>
 * interface UserInfoGetter extends UsernameGetter {
 * String getPassword();
 * }
 * <p>
 * // 返回值可以是 void,boolean,int(等数字类型)
 * updateById(UserInfoGetter getter); // update t_user set username = ?, password = ? where id = ?
 * updateUsernameById(UserInfoGetter getter); // update t_user set username = ? where id = ?
 * updateUsernameById(String id, String username); // update t_user set username = ? where id = ?
 * updateUsernameById(String username, String id); // update t_user set username = ? where id = ?
 * updateUsernameById(@P("username") String a, String id); // update t_user set username = ? where id = ?
 * <p>
 * // 非 ID 字段要保持顺序: update t_user set username = ?, password = ? where id = ?
 * updateUsernamePasswordById(String string1, String string2, String id);
 * // 得让知道哪个字段是 id
 * // 与参数顺序无关，但名称要一致: update t_user set username = ?, password = ? where id = ?
 * updateUsernamePasswordById(String string1, String id, String string2);
 * // update t_user set password = ? where username = ? and create_time > ?
 * updateByUsernameAndCreateTimeAfter(String password, String username, String createTime);
 * // update t_user set password = ? where username like  and create_time > ?
 * updateByUsernameLikeAndCreateTimeAfter(String password, String username, String createTime);
 * updateByUsernameLikeAndCreateTimeAfter(String username, String password, String createTime);
 * updatePasswordByUsernameLikeAndCreateTimeAfter(String username, String password, String createTime);
 * updatePasswordByUsernameLikeAndCreateTimeAfter(String username, String string, String createTime);
 * <p>
 * // 与参数顺序无关，但名称要一致: update t_user set username = ?, password = ? where id = ?
 * updateById(String username, String password, String id);
 * updateUsername(String password); // update t_user set username = ?
 * updateUsername(String string); // update t_user set username = ?
 * updatePassword(String password); // update t_user set password = ?
 * update(String password); // update t_user set password = ?
 * <p>
 * // 查询
 * UserInfoGetter findById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter fetchById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter selectById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter queryById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter readById(String id); // select username, password, id from t_user where id = ?
 * UserInfoGetter getById(String id); // select username, password, id from t_user where id = ?
 * <p>
 * List&lt;UserInfoGetter&gt; findByUsernameLike(String username); // select username, password, id where username like
 * '%username%'
 * List&lt;UserInfoGetter&gt; findByUsernameStartsWith(String username); // select username, password, id where username
 * like 'username%'
 * List&lt;UserInfoGetter&gt; findByUsernameEndsWith(String username); // select username, password, id where username
 * like '%username'
 * List&lt;UserInfoGetter&gt; findByAgeGt(int age); // select username, password, id where age > ?
 * List&lt;UserInfoGetter&gt; findByAgeGreatThan(int age); // select username, password, id where age > ?
 * List&lt;UserInfoGetter&gt; findByAgeLt(int age); // select username, password, id where age < ?
 * List&lt;UserInfoGetter&gt; findByAgeLessThan(int age); // select username, password, id where age < ?
 * List&lt;UserInfoGetter&gt; findByAgeLessThanAndUsernameIs(int age, String username); // select username, password, id
 * where age < ? and username = ?
 * // 此情况所有条件都是 and 和 = : select username, password, id where age =? and username = ? and address = ?
 * List&lt;UserInfoGetter&gt; findAll(int age, String username, String address);
 * <p>
 * insert(UserInfoGetter getter); // insert into t_user (id, username, password) values (?, ?, ?)
 * insert(String username); // insert into t_user (id, username) values (?, ?)
 *
 * @author benshaoye
 */
// @AutoService(Processor.class)
public class AccessorProcessor extends AbstractProcessor {

    private final static String ACCESSOR = Accessor.class.getCanonicalName();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        EnvUtils.initialize(processingEnv);
        Logger.initialize(processingEnv.getMessager());
    }

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
        new AccessorFactory().implementAll(getAccessorClasses(roundEnv));
        return true;
    }

    private static Map<TypeElement, Accessor> getAccessorClasses(RoundEnvironment env) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Accessor.class);
        Map<TypeElement, Accessor> elementAccessorMap = new LinkedHashMap<>();
        for (Element element : elements) {
            TypeElement typed = (TypeElement) element;
            elementAccessorMap.put(typed, element.getAnnotation(Accessor.class));
        }
        return elementAccessorMap;
    }
}
