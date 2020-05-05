package com.liulin.spring.framework.beans.support;

import com.liulin.spring.framework.beans.config.LBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Create by DbL on 2020/5/1 0001
 */
public class LBeanDefinitionReader {
    // 保存扫描的结果
    private List<String> registryBeanClasses = new ArrayList<String>();
    private Properties contextConfig = new Properties();

    public LBeanDefinitionReader(String... configLocations) {
        // 现在只有一个 写死取第一个
        doLoadConfig(configLocations[0]);
        // 扫描配置文件中配置的相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    public Properties getConfig(){
        return this.contextConfig;
    }

    public List<LBeanDefinition> loadBeanDefinitions() {
        List<LBeanDefinition> result = new ArrayList<LBeanDefinition>();
        for (String className : registryBeanClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){
                    continue;
                }
                // 保存类对应的ClassName(全类名) 还有beanName
                // 1. 默认类名首字母小写
                result.add(doCreateBeandefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                // 2. 自定义
                // 3. 接口注入
                for(Class<?> i : beanClass.getInterfaces()){
                    result.add(doCreateBeandefinition(i.getName(),beanClass.getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private LBeanDefinition doCreateBeandefinition(String beanName, String beanClassName) {
        LBeanDefinition beanDefinition = new LBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(beanName);
        return beanDefinition;
    }

    private void doLoadConfig(String contextConfigLocation) {
        // Spring中使用策略模式读取，这里直接将classpath: 替换成空
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:", ""));
        try {
            contextConfig.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != resourceAsStream) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String scanPackage) {
        // com.liulin.demo 需要转换成文件夹的路径形式便于扫描路径下的文件
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        // 当成是一个classPath文件夹
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                // 递归遍历子文件夹
                doScanner(scanPackage + "." + file.getName());
            } else {
                // 非class文件跳过
                if (!file.getName().endsWith(".class")) continue;
                String className = file.getName().replace(".class", "");
                // Class.forName(className)
                // 防止名字重复，这里使用包名加上类名
                registryBeanClasses.add(scanPackage + "." + className);
            }
        }
    }

    /**
     * 首字母小写
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
