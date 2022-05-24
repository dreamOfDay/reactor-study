package com.lx.common;

/**
 * @Author: jyu
 * @Date: 2022/5/24
 * @Description:
 **/
public interface TransactionManagers {

    /**
     * {@link org.springframework.boot.autoconfigure.data.r2dbc.R2dbcTransactionManagerAutoConfiguration}
     * 响应式的事务管理器
     */
    String reactiveTransactionManager = "connectionFactoryTransactionManager";

    /**
     * {@link org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration}
     * JDBC事务管理器
     */
    String jdbcTransactionManager = "transactionManager";

}
