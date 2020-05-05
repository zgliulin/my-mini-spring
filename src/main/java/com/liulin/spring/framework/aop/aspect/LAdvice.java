package com.liulin.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * Create by DbL on 2020/5/3 0003
 */
public class LAdvice {
    private Object aspect;
    private Method adviceMethod;
    private String throwName;

    public LAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    public Object getAspect() {
        return aspect;
    }

    public void setAspect(Object aspect) {
        this.aspect = aspect;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getThrowName() {
        return throwName;
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
