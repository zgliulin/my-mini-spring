package com.liulin.demo.service.impl;

import com.liulin.demo.service.IQueryService;
import com.liulin.spring.framework.annotation.LService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create by DbL on 2020/4/29 0029
 */
@LService
public class QueryServiceImpl implements IQueryService {

    /**
     * 查询
     */
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
        System.out.println("这是在业务方法中打印的：" + json);
        return json;
    }

}

