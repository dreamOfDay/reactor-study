package com.lx.controller;

import com.lx.common.TransactionManagers;
import com.lx.entity.TestEntity;
import com.lx.repository.TestEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: jyu
 * @Date: 2022/5/13
 * @Description: 尝试了几种数据库的操作方式，包含事务模式
 *
 * @see org.springframework.data.r2dbc.repository.R2dbcRepository
 * @see org.springframework.data.r2dbc.core.DatabaseClient;
 * @see org.springframework.data.r2dbc.core.R2dbcEntityTemplate
 *
 **/
@Configuration
@RequiredArgsConstructor
public class DBController {
    private static final String PATH_PREFIX = "/db/";

    @Bean("dbRounters")
    public RouterFunction<ServerResponse> dbRouters() {
        return RouterFunctions.route()
                .GET(PATH_PREFIX + "repository", this::repository)
                .GET(PATH_PREFIX + "databaseClient", this::databaseClient)
                .GET(PATH_PREFIX + "template", this::template)
                .GET(PATH_PREFIX + "testForTransactionalOperator", this::testForTransactionalOperator)
                .GET(PATH_PREFIX + "testForTransactionManager", this::testForTransactionManager)
                .build();
    }

    private final TestEntityRepository testEntityRepository;
//    private final R2dbcRepository<TestEntity,Integer> testEntityRepository;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> template(ServerRequest serverRequest) {
        // select * from test_entity where entity_name='myName2' sort by entity_id desc
        return r2dbcEntityTemplate
                .select(TestEntity.class)
                .from("test_entity")
                .matching(
                        Query.query(Criteria.where("entity_name").is("myName2"))
                                .sort(Sort.by("entity_id").descending()))
                .all()
                .log()
                .collectList()
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                ;
    }

    public Mono<ServerResponse> databaseClient(ServerRequest serverRequest) {
        PageRequest pageRequest = PageRequest.of(
                Integer.parseInt(serverRequest.queryParam("startPage").orElse("0")),
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
        );
        // select * limit #{startPage}.#{size}
        return databaseClient
                .select()
                .from("test_entity")
                .as(TestEntity.class)
                .page(pageRequest)
                .fetch()
                .all()
                .log()
                .collectList()
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                ;
    }

    public Mono<ServerResponse> repository(ServerRequest serverRequest) {
        TestEntity testEntity1 = TestEntity.builder().entityName("myName").entityValue("myValue").build();
        TestEntity testEntity2 = TestEntity.builder().entityName("myName2").entityValue("myValue2").build();
        // select * from test_entity where entity_name = 'myName2'
        // findByEntityName 为jpa的扩展方式
        return testEntityRepository
//                .findAll()
                .findByEntityName("myName2")
                .log()
                .collectList()
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                ;
        // insert list
/*        testEntityRepository
                .saveAll(Flux.just(testEntity1,testEntity2))
                .collectList()
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                ;*/
    }


    // 事务模式

    private static final AtomicInteger flag = new AtomicInteger(0);

    public Mono<ServerResponse> testForTransactionalOperator(ServerRequest serverRequest){
        // 使用 transactionalOperator 示例
        /**
         * 注意这里只有第一次会成功，
         * 因为 flag 第一次成功以后会变成1，第二次 flag 变成2的时候会抛出 “手动异常”，此时不仅数据库回滚，flag 的值也发生了回滚
         */
        return testEntityRepository
                .findById(9)
                .log()
                .map(e -> {
                    System.out.println(flag.get());
                    if (flag.get()%2 == 1){
                        throw new RuntimeException("手动异常!");
                    }
                    e.setEntityValue(e.getEntityValue() + flag.incrementAndGet());
                    return e;
                })
                .flatMap(e -> testEntityRepository.save(e))
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                .as(transactionalOperator::transactional)
        ;
    }

    /**
     * 为什么这里的 transactionManager 能够使用 connectionFactoryTransactionManager？
     * 因为 {@link org.springframework.boot.autoconfigure.data.r2dbc.R2dbcTransactionManagerAutoConfiguration} 中自动装配了 ReactiveTransactionManager
     * @param serverRequest
     * @return
     */
    // 如果是读操作，可以将以下 readOnly 注释解开
    @Transactional(/*readOnly = true, */transactionManager = TransactionManagers.reactiveTransactionManager)
    public Mono<ServerResponse> testForTransactionManager(ServerRequest serverRequest){
        // 效果与 testForTransactionalOperator 方法所展示一致
        return testEntityRepository
                .findById(9)
                .log()
                .map(e -> {
                    System.out.println(flag.get());
                    if (flag.get()%2 == 1){
                        throw new RuntimeException("手动异常!");
                    }
                    e.setEntityValue(e.getEntityValue() + flag.incrementAndGet());
                    return e;
                })
                .flatMap(e -> testEntityRepository.save(e))
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
                ;
    }


}
