package com.liulin.spring.framework.webmvc.servlet;

import com.liulin.spring.framework.annotation.*;
import com.liulin.spring.framework.context.LApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by DbL on 2020/4/29 0029
 */
public class LDispatcherServlet extends HttpServlet {
    private LApplicationContext applicationContext;

    // IOC容器，key默认是类名首字母小写，value是对应的实例对象
    private List<LHandlerMapping> handlerMappings = new ArrayList<LHandlerMapping>();

    private Map<LHandlerMapping, LHandlerAdapter> handlerAdapters = new HashMap<LHandlerMapping, LHandlerAdapter>();

    private List<LViewResolver> viewResolvers = new ArrayList<LViewResolver>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        // 6.委派，根据URL去找到一个对应的Method并通过response返回
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            try {
                processDispatchResult(req, resp, new LModelAndView("500"));
            } catch (Exception ex) {
                ex.printStackTrace();
                resp.getWriter().write("500 Exception , Detail : " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 完成了对HandlerMapping的封装
        // 完成了对方法返回值的封装，ModelAndView

        // 1.通过URL获取一个HandlerMapping
        LHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new LModelAndView("404"));
            return;
        }
        // 2.根据一个HandlerMapping获取一个HandlerAdapter
        LHandlerAdapter ha = getHandlerAdapter(handler);

        // 3.解析某一个方法的形参和返回值之后，统一封装为ModelAndView对象
        LModelAndView mv = ha.handler(req, resp, handler);
        // 就把ModelAndView变成一个ViewResolver
        processDispatchResult(req, resp, mv);

    }

    private LHandlerAdapter getHandlerAdapter(LHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()){
            return null;
        }
        return this.handlerAdapters.get(handler);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, LModelAndView lModelAndView) throws Exception {
        if(lModelAndView == null){
            return;
        }
        if(this.viewResolvers.isEmpty()){
            return;
        }
        for(LViewResolver viewResolver : this.viewResolvers){
            LView view = viewResolver.resolverViewName(lModelAndView.getViewName());
            // 直接往浏览器输出
            view.render(lModelAndView.getModel(),req,resp);
            return;
        }
    }

    private LHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        for (LHandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return mapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化Spring的核心IO容器
        applicationContext = new LApplicationContext(config.getInitParameter("contextConfigLocation"));

        // 初始化九大组件
        initStrategies(applicationContext);
        //================MVC部分==============//

        System.out.println("L Spring framework init success ");
    }

    private void initStrategies(LApplicationContext context) {
        // 多文件上传的组件
        //initMultipartResolver(context);
        // 初始化本地语言环境
        // initLocalResolver(context);
        // 初始化模板处理器
        // initThemResolver(context);
        // handlerMapping
        initHandlerMapping(context);
        // 初始化参数适配器
        initHandlerAdapters(context);
        // 初始化异常拦截器
        // initHandlerExceptionResolvers(context);
        // 初始化视图预处理器
        //initRequestToViewNameTranslator(context);
        // 初始化视图转换器
        initViewResolvers(context);
        // Flash管理器
        // initFlashMapManager(context);
    }

    private void initViewResolvers(LApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for(File file : templateRootDir.listFiles()){
            this.viewResolvers.add(new LViewResolver(templateRoot));
        }

    }

    private void initHandlerAdapters(LApplicationContext context) {
        for (LHandlerMapping handlerMapping : handlerMappings){
            this.handlerAdapters.put(handlerMapping,new LHandlerAdapter());
        }
    }

    private void initHandlerMapping(LApplicationContext context) {
        if (context.getBeanDefinitionCount() == 0) {
            return;
        }
        for (String beanName : context.getBeanDefinitionNames()) {
            Object instance = context.getBean(beanName);
            Class<?> clazz = instance.getClass();
            // 类没有加注解的跳过
            if (!clazz.isAnnotationPresent(LController.class)) {
                continue;
            }
            // 如果类上定义了路径，方法上的路径需要拼接上此路径
            String baseUrl = "";
            if (clazz.isAnnotationPresent(LRequestMapping.class)) {
                LRequestMapping Mapping = clazz.getAnnotation(LRequestMapping.class);
                baseUrl = Mapping.value();
            }
            // 只获取public的方法
            for (Method method : clazz.getMethods()) {
                // 方法没有加注解的跳过
                if (!method.isAnnotationPresent(LRequestMapping.class)) {
                    continue;
                }
                LRequestMapping requestMapping = method.getAnnotation(LRequestMapping.class);
                // 对于配置了“/” 和没有配置“/”的通过正则表达式统一处理
                // 将路径中的*改为正则表达式".*"的方式
                String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMappings.add(new LHandlerMapping(pattern, instance, method));
                System.out.println("mapped : " + regex + " , " + method);
            }
        }
    }



}
