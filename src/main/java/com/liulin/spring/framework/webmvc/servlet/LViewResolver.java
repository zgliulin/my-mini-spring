package com.liulin.spring.framework.webmvc.servlet;

import java.io.File;

/**
 * Create by DbL on 2020/5/2 0002
 */
public class LViewResolver {
    private final String DEFAULT_TEMMPLATE_SUFFX = ".html";
    private File templateRootDir;

    public LViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);

    }

    public LView resolverViewName(String viewName) {
        if (null == viewName || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath()+"/"+viewName).replaceAll("/+","/"));
        return new LView(templateFile);
    }
}
