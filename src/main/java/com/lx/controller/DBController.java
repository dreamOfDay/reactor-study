package com.lx.controller;

import com.lx.entity.TestEntity;
import com.lx.repository.TestEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Table;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: jyu
 * @Date: 2022/5/13
 * @Description: 尝试了几种数据库的操作方式
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
                .build();
    }

    private final TestEntityRepository testEntityRepository;
//    private final R2dbcRepository<TestEntity,Integer> testEntityRepository;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

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
        // select *
        return databaseClient
                .select()
                .from("test_entity")
                .as(TestEntity.class)
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


    //todo 事务模式

}
