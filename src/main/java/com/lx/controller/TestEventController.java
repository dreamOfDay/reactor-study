package com.lx.controller;

import com.lx.event.AsyncTestEvent;
import com.lx.event.TestEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: jyu
 * @Date: 2022/7/25
 * @Description:
 **/
@Configuration
@AllArgsConstructor
public class TestEventController {
    private static final String PATH_PREFIX = "/event/";

    @Bean("eventRounters")
    public RouterFunction<ServerResponse> redisRounters() {
        return RouterFunctions.route()
                .GET(PATH_PREFIX + "test", this::test)
                .build();
    }

    private ApplicationContext applicationContext;

    public Mono<ServerResponse> test(ServerRequest serverRequest) {

        return Mono.just(serverRequest.queryParam("eleId"))
                .map(eleId -> {
                    // 推送一个同步事件一个异步事件
                    TestEvent testEvent = new TestEvent(this, eleId.get());
                    AsyncTestEvent asyncTestEvent = new AsyncTestEvent(this, eleId.get());
                    applicationContext.publishEvent(testEvent);
                    applicationContext.publishEvent(asyncTestEvent);
                    return testEvent.getEleId();
                })
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
        ;
    }
}
