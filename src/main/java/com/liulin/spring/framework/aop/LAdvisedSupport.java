package com.liulin.spring.framework.aop;

import com.liulin.spring.framework.aop.aspect.LAdvice;
import com.liulin.spring.framework.aop.config.LAopConfig;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by DbL on 2020/5/3 0003
 */
public class LAdvisedSupport {
    private LAopConfig config;
    private Class<?> targetClass;
    private Object target;
    private Map<Method, Map<String, LAdvice>> methodCache;
    private Pattern pointCutClassPattern;

    public LAdvisedSupport(LAopConfig config) {
        this.config = config;
    }

    // 解析配置文件的方法
    private void parse() {
        //pointCut=public .* com.liulin.demo.service..*Service..*(.*)  改为正则表达式
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        // 1.方法的修饰符和返回值
        // 2.类名
        // 3.方法的名称和形参列表

        // 生成匹配class的正则
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));

        methodCache = new HashMap<Method, Map<String, LAdvice>>();
        // 专门匹配方法的正则
        Pattern pointCutPattern = Pattern.compile(pointCut);
        try {
            Class<?> aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }
            // 以上都是初始化工作，准备阶段

            //  从这里开始封装LAopAdvice
            for (Method method : this.targetClass.getMethods()) {
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pointCutPattern.matcher(methodString);
                if (matcher.matches()) {
                    Map<String, LAdvice> advices = new HashMap<String, LAdvice>();
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        advices.put("before", new LAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectBefore())));
                    }
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        advices.put("after", new LAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfter())));
                    }
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        LAdvice advice = new LAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfterThrow()));
                        advice.setThrowName(config.getAspectAfterThrowingName());
                        advices.put("afterThrow", advice);
                    }
                    // 跟目标代理类的业务方法和Advices建立一对多的关联关系，以便在Proxy中获得
                    methodCache.put(method,advices);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // 根据一个目标代理类方法获得其对应的通知
    public Map<String, LAdvice> getAdvices(Method method, Object o) throws Exception {
        Map<String, LAdvice> cache = methodCache.get(method);
        if (null == cache) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cache = methodCache.get(m);
            this.methodCache.put(m, cache);
        }
        return cache;
    }

    // 给ApplicationContext首先IOC中的对象初始化时调用，决定要不要生成代理类的逻辑
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public Class getTargetClass() {
        return this.targetClass ;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return this.target;
    }
}
