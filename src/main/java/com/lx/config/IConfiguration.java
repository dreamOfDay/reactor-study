package com.lx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

/**
 * @Author: jyu
 * @Date: 2022/5/13
 * @Description: 基础配置类
 *
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

    /**
     * 手动注册 TransactionalOperator
     * 为什么能够注入 ReactiveTransactionManager?
     * 因为 {@link org.springframework.boot.autoconfigure.data.r2dbc.R2dbcTransactionManagerAutoConfiguration} 中自动装配了 ReactiveTransactionManager
     * @param transactionManager
     * @return
     */
    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }

    // 如果没有上述 R2dbcTransactionManagerAutoConfiguration 中自动装配 ReactiveTransactionManager，则需要打开此注释手动装配
    /*@Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return (new R2dbcTransactionManager(connectionFactory));
    }*/


}
