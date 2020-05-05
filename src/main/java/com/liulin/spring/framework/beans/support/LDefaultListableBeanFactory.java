package com.liulin.spring.framework.beans.support;

import com.liulin.spring.framework.beans.config.LBeanDefinition;
import com.liulin.spring.framework.core.LBeanFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by DbL on 2020/5/4 0004
 */
public class LDefaultListableBeanFactory implements LBeanFactory {

    public Map<String, LBeanDefinition> beanDefinitionMap = new HashMap<String, LBeanDefinition>();

    @Override
    public Object getBean(String beanName) {
        return null;
    }

    @Override
    public Object getBean(Class beanClass) {
        return null;
    }

    public  void doRegistBeanDefinition(List<LBeanDefinition> beanDefinitions) throws Exception {
        for (LBeanDefinition beanDefinition : beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is Exists");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }
}
