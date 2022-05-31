package com.lx;

import com.lx.config.MyImportBeanDefinitionRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @Author: jyu
 * @Date: 2022/5/13
 * @Description:
 **/
@Import({MyImportBeanDefinitionRegistrar.class})
@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "com.lx.repository")
public class ReactorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactorApplication.class, args);
    }

}
