package com.liulin.demo.service.impl;

import com.liulin.demo.service.IModifyService;
import com.liulin.spring.framework.annotation.LService;

/**
 * Create by DbL on 2020/4/29 0029
 */
@LService
public class ModifyServiceImpl implements IModifyService {

    /**
     * 增加
     */
    public String add(String name,String addr) throws Exception {
        throw new Exception("这是故意抛出来的异常");
    }

    /**
     * 修改
     */
    public String edit(Integer id,String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    /**
     * 删除
     */
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }

}
