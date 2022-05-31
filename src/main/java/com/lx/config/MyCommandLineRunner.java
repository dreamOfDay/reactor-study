package com.lx.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: jyu
 * @Date: 2022/5/31
 * @Description:
 **/
@Component
@AllArgsConstructor
public class MyCommandLineRunner implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        /**
         * 开启 StringRedisTemplate 的事务模式，这里采用 CommandLineRunner 的方式实现，官方推荐实现如下
         * <html>https://docs.spring.io/spring-data/redis/docs/current/reference/html/#tx.spring</html>
         */
        StringRedisTemplate template = applicationContext.getBean(StringRedisTemplate.class);
        template.setEnableTransactionSupport(true);
    }
}
