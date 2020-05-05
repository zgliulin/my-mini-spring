package com.liulin.demo.action;

import com.liulin.demo.service.IQueryService;
import com.liulin.spring.framework.annotation.LAutowired;
import com.liulin.spring.framework.annotation.LController;
import com.liulin.spring.framework.annotation.LRequestMapping;
import com.liulin.spring.framework.annotation.LRequestParam;
import com.liulin.spring.framework.webmvc.servlet.LModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by DbL on 2020/5/2 0001
 */
@LController
@LRequestMapping("/")
public class PageAction {

    @LAutowired
    IQueryService queryService;

    @LRequestMapping("/first.html")
    public LModelAndView query(@LRequestParam("coder") String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("coder", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new LModelAndView("first.html",model);
    }

}
