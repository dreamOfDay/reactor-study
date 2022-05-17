package com.lx.config;

import com.lx.entity.TestEntity;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author: jyu
 * @Date: 2022/5/16
 * @Description: 扩展 ImportBeanDefinitionRegistrar，手动注册 一个自定义 TestEntity
 **/
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(TestEntity.class);
        MutablePropertyValues propertyValues = genericBeanDefinition.getPropertyValues();
        propertyValues.addPropertyValue("entityId",-1);
        propertyValues.addPropertyValue("entityName","testName");
        propertyValues.addPropertyValue("entityValue","testVal");
        registry.registerBeanDefinition("myTestEntity", genericBeanDefinition);

    }

}
