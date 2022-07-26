package com.lx.controller;

import com.lx.event.AsyncTestEvent;
import com.lx.event.TestEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    public RouterFunction<ServerResponse> eventRounters() {
        return RouterFunctions.route()
                .GET(PATH_PREFIX + "test", this::test)
                .build();
    }

    private ApplicationEventPublisher eventPublisher;

    public Mono<ServerResponse> test(ServerRequest serverRequest) {

        return Mono.just(serverRequest.queryParam("eleId"))
                .flatMap(eleId -> {
                    TestEvent testEvent = new TestEvent(this, eleId.get());
                    AsyncTestEvent asyncTestEvent = new AsyncTestEvent(this, eleId.get());
                    eventPublisher.publishEvent(testEvent);
                    eventPublisher.publishEvent(asyncTestEvent);
                    return Mono.empty().then(Mono.fromCallable(testEvent::getEleId));
                })
                .flatMap(e -> ServerResponse.ok().bodyValue(e))
        ;
    }
}
