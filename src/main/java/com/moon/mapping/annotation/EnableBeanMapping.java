package com.moon.mapping.annotation;

import com.moon.mapping.BeanMapper;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启 Spring 支持后会向容器中注册名称形如{@code mapUserToPersonal}的{@link BeanMapper}, 例：
 *
 * <pre>
 * public class Car {
 *     private String name;
 *     // other fields & setters & getters
 * }
 *
 * &#64;MapperFor({Car.class}) // {@link MapperFor}
 * public class Bus {
 *     private String name;
 *     // other fields & setters & getters
 * }
 *
 * &#64;EnableBeanMapping // 开启 spring 支持
 * &#64;SpringBootApplication
 * public class DemoApplication {
 *
 *     // 用泛型的方式注入
 *     &#64;Autowired
 *     private BeanMapping&lt;Bus, Car&gt; mapping;
 *
 *     // 用命名的方式注入，命名规则形如：mapXxxxToYyyy
 *     // 如果存在多个同名的怎么办呢？会这样 mapXxxxToYyyy1 直接在后面加序号
 *     // 并且这个序号一般没法预测，此时可用泛型的方式注入，Spring 已经处理好这种关系了
 *     // 此时给类改个名加个 DO, BO 之类的后缀不是更好吗，而且一个项目出现同名类到另一个同名类映射的情况极少
 *     &#64;Autowired
 *     private BeanMapping mapBusToCar; // mapping == mapBusToCar
 *
 *     // main
 * }
 * </pre>
 *
 * @author moonsky
 */
@ComponentScan(basePackageClasses = BeanMapper.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableBeanMapping {}
