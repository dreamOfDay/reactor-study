package com.lx.controller;

import com.alibaba.fastjson.JSONObject;
import com.lx.common.TransactionManagers;
import com.lx.repository.TestEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: jyu
 * @Date: 2022/5/30
 * @Description: 尝试 Reactive方式操作redis方案，以下例子均为失败示例。 暂时未发现ReactiveRedisTemplate实现pipeline，事务的方案，目前应该未支持
 *               但是 RedisTemplate 支持：
 *                  1. @EnableTransactionManagement//配置声明式事务管理
 *                  2. redisTemplate.setEnableTransactionSupport(true);//让你的RedisTemplate开启事务支持
 **/
@Service
@AllArgsConstructor
public class TestTransactionalService {


    private final TestEntityRepository testEntityRepository;
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final TransactionalOperator transactionalOperator;

    private static final AtomicInteger flag = new AtomicInteger(0);

    @Transactional(rollbackFor = Exception.class, transactionManager = TransactionManagers.reactiveTransactionManager)
    public Mono<ServerResponse> testForTransactional(ServerRequest serverRequest) {
        Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        return testEntityRepository
                .findById(9)
                .log()
                .flatMap( val ->
                        reactiveStringRedisTemplate
                                .opsForValue()
                                .set("test",JSONObject.toJSONString(val))
                                .log())
                .map(e -> {
                    System.out.println(flag.incrementAndGet());
                    if (flag.get()%2 == 1){
                        throw new RuntimeException("手动异常!");
                    }
                    return e;
                })
                .onErrorResume(e -> {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
                    return null;
                })
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                .switchIfEmpty(ServerResponse.ok().build())
                ;
    }

    public Mono<ServerResponse> testForTransactionalOperator(ServerRequest serverRequest) {
        return testEntityRepository
                .findById(9)
                .log()
                .flatMap( val ->
                        reactiveStringRedisTemplate
                                .opsForValue()
                                .set("test", JSONObject.toJSONString(val))
                                .log())
                .map(e -> {
                    System.out.println(flag.incrementAndGet());
                    if (flag.get()%2 == 1){
                        throw new RuntimeException("手动异常!");
                    }
                    return e;
                })
                .onErrorResume(e -> {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return null;
                })
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                .switchIfEmpty(ServerResponse.ok().build())
                .as(transactionalOperator::transactional)
                ;
    }
}
