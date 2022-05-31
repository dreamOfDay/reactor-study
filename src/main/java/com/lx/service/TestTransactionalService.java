package com.lx.service;

import com.alibaba.fastjson.JSONObject;
import com.lx.common.TransactionManagers;
import com.lx.repository.TestEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    // 非响应式 StringRedisTemplate
    private final StringRedisTemplate stringRedisTemplate;

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


    @Transactional(rollbackFor = Exception.class, transactionManager = TransactionManagers.reactiveTransactionManager)
    public Mono<ServerResponse> testTransactionalByStringRedisTemplate(ServerRequest serverRequest) {
        return testEntityRepository
                // step1 从数据库获取数据
                .findById(9)
                .log()
                .map(e -> {
                    e.setEntityValue(e.getEntityValue() + flag.incrementAndGet());
                    return e;
                })
                // step2 更新数据库
                .flatMap(testEntityRepository::save)
                // step3 更新缓存
                .flatMap( val -> {
                    /**
                     * todo 不知道这里为什么不能交给事务管理器做到自动回滚，按照官方描述应该可以，这里不知道为啥，换了jdbc事务管理器也不行
                     * <html>https://docs.spring.io/spring-data/redis/docs/current/reference/html/#tx.spring</html>
                     * 以上官方写到了如下描述，但是好像未生效
                     * <h2>
                     *     Transaction management requires a PlatformTransactionManager.
                     *     Spring Data Redis does not ship with a PlatformTransactionManager implementation.
                     *     Assuming your application uses JDBC, Spring Data Redis can participate in transactions by using existing transaction managers.
                     * </h2>
                     */
                    // 手动开启 redis 的事务
                    stringRedisTemplate.multi();
                    stringRedisTemplate.opsForValue().set("test", JSONObject.toJSONString(val));
                    if (flag.get()%2 == 1){
                        return Mono.error(new RuntimeException("手动异常!"));
                    }
                    stringRedisTemplate.exec();
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    // 发生异常的时候回滚redis的事务，mysql的事务可以交给 @Transactional 回滚
                    stringRedisTemplate.discard();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ServerResponse.badRequest().build();
                })
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                ;
    }
}
