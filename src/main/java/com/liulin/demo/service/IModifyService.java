package com.liulin.demo.service;

/**
 * Create by DbL on 2020/4/29 0029
 */
public interface IModifyService {
    /**
     * 增加
	 */
    public String add(String name, String addr) throws Exception;

    /**
     * 修改
     */
    public String edit(Integer id, String name);

    /**
     * 删除
     */
    public String remove(Integer id);
}
