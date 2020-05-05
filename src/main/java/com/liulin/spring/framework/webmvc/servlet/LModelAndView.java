package com.liulin.spring.framework.webmvc.servlet;

import java.util.Map;

/**
 * Create by DbL on 2020/5/1 0001
 */
public class LModelAndView {
    private String viewName;
    private Map<String,?> model;

    public LModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public LModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
