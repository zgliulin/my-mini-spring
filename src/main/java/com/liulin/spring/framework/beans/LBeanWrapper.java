package com.liulin.spring.framework.beans;

/**
 * Create by DbL on 2020/5/1 0001
 */
public class LBeanWrapper {
    private Object wrapperInstance;
    private Class<?> wrappedClass;

    public LBeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.wrappedClass = instance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedClass;
    }
}
