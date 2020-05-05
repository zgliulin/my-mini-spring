package com.liulin.spring.framework.core;

/**
 * Create by DbL on 2020/5/4 0004
 */
public interface LBeanFactory {
    public Object getBean(String beanName);

    public Object getBean(Class beanClass);
}
