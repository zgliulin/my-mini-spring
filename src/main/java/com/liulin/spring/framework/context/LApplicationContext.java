package com.liulin.spring.framework.context;

import com.liulin.spring.framework.annotation.LAutowired;
import com.liulin.spring.framework.annotation.LController;
import com.liulin.spring.framework.annotation.LService;
import com.liulin.spring.framework.aop.LAdvisedSupport;
import com.liulin.spring.framework.aop.config.LAopConfig;
import com.liulin.spring.framework.aop.support.LJdkDynamicAopProxy;
import com.liulin.spring.framework.beans.LBeanWrapper;
import com.liulin.spring.framework.beans.config.LBeanDefinition;
import com.liulin.spring.framework.beans.support.LDefaultListableBeanFactory;
import com.liulin.spring.framework.core.LBeanFactory;
import com.liulin.spring.framework.beans.support.LBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 职责:完成Bean的创建和DI
 * Create by DbL on 2020/5/1 0001
 */
public class LApplicationContext implements LBeanFactory {

    private LBeanDefinitionReader reader;

    private LDefaultListableBeanFactory registry = new LDefaultListableBeanFactory();

    private Map<String, LBeanWrapper> factoryBeanInstanceCache = new HashMap<String, LBeanWrapper>();

    private Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();

    public LApplicationContext(String... configLocations) {
        // 1.加载配置文件
        reader = new LBeanDefinitionReader(configLocations);
        try {
            // 2.解析配置文件，封装成BeanDefinition
            List<LBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
            // 3.把Beandefinition缓存起来
            // beanDefinitionMap
            registry.doRegistBeanDefinition(beanDefinitions);
            doAutoWrited();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutoWrited() {
        // 调用getBean() 仅非延时加载的类
        for (Map.Entry<String, LBeanDefinition> beanDefinitionEntry : registry.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }



    // Bean的实例化,DI是从这个方法开始的
    public Object getBean(String beanName) {
        // 1. 先拿到BeanDefinition配置信息
        LBeanDefinition beanDefinition = registry.beanDefinitionMap.get(beanName);
        // 2.反射实例化 newInstance()
        Object instance = instantiateBean(beanName, beanDefinition);
        // 3.封装成beanWrapper
        LBeanWrapper beanWrapper = new LBeanWrapper(instance);
        // 4.保存到IOC容器
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        // 5.执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);
        return beanWrapper.getWrapperInstance();
    }

    private void populateBean(String beanName, LBeanDefinition beanDefinition, LBeanWrapper beanWrapper) {
        // 可能会涉及到循环依赖？  这里不做处理
        // 用两个缓存
        // 1.把第一次读取结果为空的BeanDefinition存到第一个缓存
        // 2.等第一次循环之后，第二次循环再检查第一次的缓存。再进行赋值
        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        // Spring中为component ，为其他注解的父类，这里只针对自定义的LController与LService两个注解
        if(!(clazz.isAnnotationPresent(LController.class) || clazz.isAnnotationPresent(LService.class)) ){
            return;
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(LAutowired.class)) {
                return;
            }
            LAutowired autowired = field.getAnnotation(LAutowired.class);
            // 如果用户没有自定义的beanName，就默认根据类型注入
            String autoWiredName = autowired.value().trim();
            if ("".equals(autoWiredName)) {
                // 接口全名
                autoWiredName = field.getType().getName();
            }
            // 对于类中的private属性的成员进行暴力访问
            field.setAccessible(true);
            try {
                if(this.factoryBeanInstanceCache.get(autoWiredName) == null){
                    continue;
                }
                // ioc.get(beanName) 相当于通过接口的全名从IOC中拿到接口的实现的实例
                field.set(instance, this.factoryBeanInstanceCache.get(autoWiredName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    // 创建真正的实例对象
    private Object instantiateBean(String beanName, LBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            if(this.factoryBeanObjectCache.containsKey(beanName)){
                instance =  factoryBeanObjectCache.get(beanName);
            }else {
                Class<?> aClass = Class.forName(className);
                instance = aClass.newInstance();
                //====================AOP START==================//
                // 如果满足条件，就指直接返回Proxy对象
                // 1.加载AOP配置文件
                LAdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(aClass);
                config.setTarget(instance);
                // 判断规则，要不要生成代理类，如果要就覆盖原生对象，如果不要就不做任何处理，返回原生对象
                if(config.pointCutMatch()){
                    instance = new LJdkDynamicAopProxy(config).getProxy();
                }
                //====================AOP END==================//
                this.factoryBeanObjectCache.put(beanName, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private LAdvisedSupport instantionAopConfig(LBeanDefinition beanDefinition) {
        LAopConfig config = new LAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new LAdvisedSupport(config);
    }

    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    public int getBeanDefinitionCount() {
        return registry.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return registry.beanDefinitionMap.keySet().toArray(new String[getBeanDefinitionCount()]);
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
