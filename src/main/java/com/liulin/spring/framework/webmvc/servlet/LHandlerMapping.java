package com.liulin.spring.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Create by DbL on 2020/5/1 0001
 */
public class LHandlerMapping {
    private Pattern pattern; // URL
    private Method method; // 对应的Method
    private Object controller; // Method对应的实例对象

    public LHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }
}
