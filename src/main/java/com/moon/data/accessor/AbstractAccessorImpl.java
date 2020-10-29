package com.moon.data.accessor;

import com.moon.data.Record;
import com.moon.data.registry.LayerEnum;
import com.moon.data.registry.LayerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author moonsky
 */
public abstract class AbstractAccessorImpl<T extends Record<ID>, ID> implements BaseAccessor<T, ID> {

    private static boolean isExtendOf(Class thisClass, Class superClass) {
        return thisClass != null && superClass != null && superClass.isAssignableFrom(thisClass);
    }

    protected static boolean isAccessorType(Class cls) { return isExtendOf(cls, BaseAccessor.class); }

    protected static boolean isRecordableType(Class cls) { return isExtendOf(cls, Record.class); }

    protected final Class deduceDomainClass() {
        Class domainClass = null;
        final Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] types = paramType.getActualTypeArguments();
            if (types.length == 1) {
                domainClass = (Class) types[0];
            } else {
                Class recordable = null;
                for (Type entityType : types) {
                    if (entityType instanceof Class) {
                        Class cls = (Class) entityType;
                        if (isRecordableType(cls)) {
                            recordable = cls;
                        }
                    }
                }
                domainClass = recordable;
            }
        }
        return domainClass;
    }

    @Autowired
    private ApplicationContext context;
    @Autowired(required = false)
    private WebApplicationContext webContext;

    protected final Class domainClass;
    private BaseAccessor<T, ID> accessor;

    /**
     * 构造器
     *
     * @param accessServeClass 将要访问的服务具体实现类型，如：UserServiceImpl
     * @param domainClass      具体实体类型
     */
    protected AbstractAccessorImpl(
        Class<? extends BaseAccessor<T, ID>> accessServeClass, Class<T> domainClass
    ) {
        Class access = accessServeClass, domain = domainClass;

        // 自动推测和交换领域实例类和数据访问类
        if (domain == null) {
            // 根据泛型推断领域实体类型
            domain = deduceDomainClass();
        } else if (!isRecordableType(domain)) {
            if (isAccessorType(domain)) {
                Class tempClass = null;
                if (!isAccessorType(access)) {
                    tempClass = access;
                    access = domain;
                }
                if (tempClass != null && isRecordableType(tempClass)) {
                    domain = tempClass;
                }
            } else {
                // 根据泛型推断领域实体类型
                domain = deduceDomainClass();
            }
        }

        if (access == null || !isAccessorType(access)) {
            if (domain == null && isRecordableType(access)) {
                domain = access;
            }
            access = null;
        }

        this.domainClass = domain == null ? deduceDomainClass() : domain;
        LayerRegistry.registry(provideThisLayer(), getDomainClass(), this);
        AccessorRegistration.registry(getInjectRunner(access));
    }

    /**
     * 自动注入任务
     *
     * @param accessServeClass
     *
     * @return
     */
    private Runnable getInjectRunner(Class<? extends BaseAccessor> accessServeClass) {
        return () -> {
            BaseAccessor accessor = provideDefaultAccessor();
            if (accessor == null && accessServeClass != null) {
                accessor = getContext().getBean(accessServeClass);
            }
            if (accessor == null) {
                Class domainClass = getDomainClass();
                LayerEnum layer = injectAccessLayer();
                if (layer != null) {
                    accessor = LayerRegistry.get(layer, domainClass);
                } else {
                    accessor = LayerRegistry.pullTopLevelBy(provideThisLayer(), domainClass);
                }
            }
            if (this.accessor == null) {
                this.accessor = accessor;
            }
        };
    }

    public Class getDomainClass() { return domainClass; }

    protected ApplicationContext getContext() { return context; }

    protected WebApplicationContext getWebContext() {
        WebApplicationContext webContext = this.webContext;
        if (webContext != null) {
            return webContext;
        }
        ApplicationContext context = this.getContext();
        if (context instanceof WebApplicationContext) {
            return (WebApplicationContext) context;
        }
        return null;
    }

    protected final BaseAccessor<T, ID> obtainOriginAccessor() { return accessor; }

    protected BaseAccessor<T, ID> getAccessor() { return obtainOriginAccessor(); }

    /**
     * like getRepository
     * like getService
     * like getMapper
     * <p>
     * and so on...
     *
     * @return
     */
    protected BaseAccessor<T, ID> provideDefaultAccessor() { return null; }

    /**
     * 我要访问的是哪一层
     * <p>
     * 通常：controller 访问 service，service 访问 repository/mapper层
     *
     * @return
     */
    protected LayerEnum injectAccessLayer() { return null; }

    /**
     * 告诉注册器，“我”是哪一层
     *
     * @return
     */
    protected abstract LayerEnum provideThisLayer();
}
