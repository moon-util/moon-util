package com.moon.processing;

import com.google.auto.service.AutoService;
import com.moon.accessor.annotation.Accessor;
import com.moon.accessor.annotation.domain.TableModel;
import com.moon.accessor.annotation.condition.IfMatching;
import com.moon.mapper.annotation.MapperFor;
import com.moon.processing.holder.Holders;
import com.moon.processing.holder.MatchingHolder;
import com.moon.processing.util.Log2;
import com.moon.processing.util.Element2;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.moon.processing.util.Extract2.getMapperForValues;

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
@AutoService(Processor.class)
public class ProcessingProcessor extends AbstractProcessor {

    private final Holders holders;

    public ProcessingProcessor() {
        this.holders = Holders.INSTANCE;
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        // if (roundEnv.processingOver()) {
        //     doWriteJavaFiles();
        // } else {
        //     doProcessing(roundEnv);
        // }
        Log2.warn("================================>>>>>>>>");
        doProcessing(roundEnv);
        doWriteJavaFiles();
        return true;
    }

    private void doProcessing(RoundEnvironment roundEnv) {
        doProcessingMapper(roundEnv);
        doProcessingMatching(roundEnv);
        doProcessingTableModel(roundEnv);
        doProcessingAccessor(roundEnv);
    }

    private void doProcessingMatching(RoundEnvironment roundEnv) {
        Elements elements = processingEnv.getElementUtils();
        Set<? extends Element> matchingSet = roundEnv.getElementsAnnotatedWith(IfMatching.class);
        MatchingHolder matchingHolder = holders.getMatchingHolder();
        for (Element annotated : matchingSet) {
            TypeElement matchingElem = (TypeElement) annotated;
            Log2.warn(matchingElem);
            IfMatching matching = annotated.getAnnotation(IfMatching.class);
            String matcherClass = Element2.getClassname(matching, IfMatching::value);
            TypeElement matcherElem = elements.getTypeElement(matcherClass);
            matchingHolder.with(matchingElem, matcherElem, matching);
        }
    }

    private void doProcessingMapper(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(MapperFor.class).forEach(annotated -> {
            TypeElement mapperForAnnotated = (TypeElement) annotated;
            MapperFor mapperFor = annotated.getAnnotation(MapperFor.class);
            Collection<TypeElement> forValues = getMapperForValues(mapperForAnnotated);
            for (TypeElement forElement : forValues) {
                holders.getMapperHolder().with(mapperFor, mapperForAnnotated, forElement);
            }
        });
    }

    private void doProcessingTableModel(RoundEnvironment roundEnv) {
        Set<? extends Element> models = roundEnv.getElementsAnnotatedWith(TableModel.class);
        models.forEach(element -> holders.getTypeHolder().with((TypeElement) element));
    }

    private void doProcessingAccessor(RoundEnvironment roundEnv) {
        Set<? extends Element> accessors = roundEnv.getElementsAnnotatedWith(Accessor.class);
        accessors.forEach(element -> holders.getAccessorHolder().with((TypeElement) element));
    }

    private void doWriteJavaFiles() {
        holders.writeJavaFile(new JavaFiler());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        holders.init(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() { return SourceVersion.latestSupported(); }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(Accessor.class.getCanonicalName());
        supportedTypes.add(MapperFor.class.getCanonicalName());
        supportedTypes.add(TableModel.class.getCanonicalName());
        return supportedTypes;
    }
}
