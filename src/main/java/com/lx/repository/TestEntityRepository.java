package com.lx.repository;

import com.lx.entity.TestEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

/**
 * @Author: jyu
 * @Date: 2022/5/13
 * @Description: Repository模式操作数据库
 **/
public interface TestEntityRepository extends R2dbcRepository<TestEntity,Integer> {
    Flux<TestEntity> findByEntityName(String name);
}
