package com.liulin.spring.framework.beans.config;

/**
 * Create by DbL on 2020/5/1 0001
 */
public class LBeanDefinition {
    private String beanClassName;
    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
