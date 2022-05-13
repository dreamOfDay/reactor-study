package com.lx.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

/**
 * @Author: jyu
 * @Date: 2022/5/13
 * @Description: 映射数据库表，ddl如下：
 * <pre>
 *     create table test_entity
 * (
 *     entity_id    int primary key auto_increment comment 'id',
 *     entity_name  varchar(64) not null comment '名称',
 *     entity_value varchar(64) null comment '起始位置'
 * ) comment = '测试实体表'
 *     charset = utf8;
 * </pre>
 **/
@Table(name = "test_entity")
@Data
@Builder
public class TestEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer entityId;

    @Column
    private String entityName;

    @Column
    private String entityValue;
}
