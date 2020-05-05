package com.liulin.demo.action;

import com.liulin.demo.service.IModifyService;
import com.liulin.demo.service.IQueryService;
import com.liulin.spring.framework.annotation.LAutowired;
import com.liulin.spring.framework.annotation.LController;
import com.liulin.spring.framework.annotation.LRequestMapping;
import com.liulin.spring.framework.annotation.LRequestParam;
import com.liulin.spring.framework.webmvc.servlet.LModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by DbL on 2020/4/29 0029
 */
@LController
@LRequestMapping("/web")
public class MyAction {

    @LAutowired
    IQueryService queryService;
    @LAutowired
    IModifyService modifyService;

    @LRequestMapping("/query.json")
    public LModelAndView query(HttpServletRequest request, HttpServletResponse response,
                               @LRequestParam("name") String name) {
        String result = queryService.query(name);
        return out(response, result);
    }

    @LRequestMapping("/add*.json")
    public LModelAndView add(HttpServletRequest request, HttpServletResponse response,
                    @LRequestParam("name") String name, @LRequestParam("addr") String addr)  {
        try {
            String result = modifyService.add(name, addr);
            return out(response,result);
        }catch (Throwable e){
            Map<String,String> model = new HashMap<String,String>();
            model.put("detail",e.getCause().getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return new LModelAndView("500",model);
        }
    }

    @LRequestMapping("/remove.json")
    public LModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                       @LRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        return out(response, result);
    }

    @LRequestMapping("/edit.json")
    public LModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                     @LRequestParam("id") Integer id,
                     @LRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        return out(response, result);
    }


    private LModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

