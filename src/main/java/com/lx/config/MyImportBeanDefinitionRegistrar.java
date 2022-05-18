package com.lx.config;

import com.lx.demand.enums.TransEnum;
import com.lx.entity.TestEntity;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.EnumSet;

/**
 * @Author: jyu
 * @Date: 2022/5/16
 * @Description: 扩展 ImportBeanDefinitionRegistrar，手动注册 一个自定义 TestEntity
 **/
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        /**
         * 2022.05.18 已实现，参照 demand包
         *
         * 如果定义了一个接口，将其实现类采用一个默认的实现类全部注册到IOC容器里面，即可随用随取。
         *
         * 比如：
         *      定义了一个输出转换器父接口 Translator
         *          -- DefaultTranslator：转换为 json
         *          -- JacksonTranslator：转换为 jackson
         *
         *      使用起来：
         *          <pre>
         *              @Autowired
         *              private final Translator;
         *          </pre>
         */

        EnumSet.allOf(TransEnum.class)
                .stream()
                .map(TransEnum::getClazz)
                .forEach(e -> {
                    AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(e).getBeanDefinition();
                    registry.registerBeanDefinition(e.getName(),beanDefinition);
                });



    }

}
