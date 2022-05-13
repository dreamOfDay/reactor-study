package com.lx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

/**
 * @Author: jyu
 * @Date: 2022/5/13
 * @Description: 基础配置类
 **/
@Configuration
public class IConfiguration {

    /**
     * 之前可以通过 {@see Connet}
     * @param databaseClient
     * @return
     */
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(DatabaseClient databaseClient){
        return new R2dbcEntityTemplate(databaseClient);
    }
}
