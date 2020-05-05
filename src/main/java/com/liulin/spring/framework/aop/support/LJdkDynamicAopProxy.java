package com.liulin.spring.framework.aop.support;

import com.liulin.spring.framework.aop.LAdvisedSupport;
import com.liulin.spring.framework.aop.aspect.LAdvice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Create by DbL on 2020/5/3 0003
 */
public class LJdkDynamicAopProxy implements InvocationHandler {
    private LAdvisedSupport config;

    public LJdkDynamicAopProxy(LAdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // key存储 befor、after、throw ...
        Map<String, LAdvice> advices = config.getAdvices(method, null);
        Object returnValue;
        try {
            invokeAdvice(advices.get("before"));

            returnValue = method.invoke(this.config.getTarget(), args);

            invokeAdvice(advices.get("after"));
        } catch (Exception e) {
            invokeAdvice(advices.get("afterThrow"));
            throw e;
        }
        return returnValue;
    }

    private void invokeAdvice(LAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), this.config.getTargetClass().getInterfaces(), this);
    }
}
