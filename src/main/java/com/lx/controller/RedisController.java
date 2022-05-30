package com.lx.controller;

import com.alibaba.fastjson.JSONObject;
import com.lx.common.TransactionManagers;
import com.lx.repository.TestEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: jyu
 * @Date: 2022/5/30
 * @Description:
 **/
@Configuration
@AllArgsConstructor
public class RedisController {
    private static final String PATH_PREFIX = "/redis/";

    @Bean("redisRounters")
    public RouterFunction<ServerResponse> redisRounters() {
        return RouterFunctions.route()
                .PUT(PATH_PREFIX + "set", this::set)
                .GET(PATH_PREFIX + "get", this::get)
                .GET(PATH_PREFIX + "testForTransactional", this::testForTransactional)
                .GET(PATH_PREFIX + "testForTransactionalOperator", this::testForTransactionalOperator)
                .build();
    }

    private final TestEntityRepository testEntityRepository;
    /**
     * 默认使用了 Lettuce 连接器
     * {@link org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration} 中实现了自动装配，用法与 RedisTemplate 类似
     *
     */
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final TestTransactionalService testTransactionalService;


    // set key val
    public Mono<ServerResponse> set(ServerRequest serverRequest) {
        // get data from db and set to redis
        return testEntityRepository
                .findById(9)
                .log()
                .flatMap( val ->
                        reactiveStringRedisTemplate
                                .opsForValue()
                                .set("test",JSONObject.toJSONString(val)).log())
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                .switchIfEmpty(ServerResponse.ok().build())
                ;
    }

    // get val by key
    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        return reactiveStringRedisTemplate
                .opsForValue()
                .get("test")
                .log()
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                ;
    }


    // 以下两个方法均会失败，目前 ReactiveRedisTemplate 不支持事务模式
    public Mono<ServerResponse> testForTransactional(ServerRequest serverRequest) {
        return testTransactionalService.testForTransactional(serverRequest);
    }

    public Mono<ServerResponse> testForTransactionalOperator(ServerRequest serverRequest) {
        return testTransactionalService.testForTransactionalOperator(serverRequest);
    }

}
