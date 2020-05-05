package com.liulin.spring.framework.webmvc.servlet;

import com.liulin.spring.framework.annotation.LRequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by DbL on 2020/5/1 0001
 */
public class LHandlerAdapter {

    public LModelAndView handler(HttpServletRequest req, HttpServletResponse resp, LHandlerMapping handler) throws Exception {
        // 保存形参列表 将参数名称和参数的位置保存起来
        Map<String,Integer> paramIndexMapping = new HashMap<String,Integer>();
        // 通过运行时的状态去拿到注解的值
        Annotation[][] pa = handler.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof LRequestParam) {
                    String paramName = ((LRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName,i);
                    }
                }
            }
        }
        // 初始化
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramterType = paramTypes[i];
            if (paramterType == HttpServletRequest.class || paramterType == HttpServletResponse.class) {
                paramIndexMapping.put(paramterType.getName(),i);
            }else if (paramterType == String.class) {

            }
        }
        // 拼接实参列表
        // http://localhost:8080/web/add.json?name=DBL&addr=chongqing
        Map<String, String[]> params = req.getParameterMap();

        Object[] paramValues = new Object[paramTypes.length];

        for(Map.Entry<String,String[]> param : params.entrySet()){
            String value = Arrays.toString(params.get(param.getKey()))
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s+","");
            if(!paramIndexMapping.containsKey(param.getKey())){
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());

            // 允许自定义的类型转换器Converter
            paramValues[index] = castStringValue(value,paramTypes[index]);
        }

       // String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
       // method.invoke(applicationContext.getBean(beanName), paramValues);
        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }
        Object result = handler.getMethod().invoke(handler.getController(),paramValues);
        if (result == null || result instanceof  Void){
            return null;
        }
        boolean isModelAndView = handler.getMethod().getReturnType() == LModelAndView.class;
        if(isModelAndView){
            return (LModelAndView) result;
        }
        return null;
    }

    private Object castStringValue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }else if(Integer.class == paramType){
            return Integer.valueOf(value);
        }else if(Double.class == paramType){
            Double.valueOf(value);
        }else{
            if(value != null ){
                return value;
            }
        }
        return null;
    }
}
