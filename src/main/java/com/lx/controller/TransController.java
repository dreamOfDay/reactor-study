package com.lx.controller;

import com.lx.demand.enums.TransEnum;
import com.lx.demand.trans.Translater;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: jyu
 * @Date: 2022/5/18
 * @Description:
 **/
@Configuration
@AllArgsConstructor
public class TransController {

    @Bean("transRounters")
    public RouterFunction<ServerResponse> transRouters() {
        return RouterFunctions.route()
                .GET("trans", this::trans)
                .build();
    }

    private final ApplicationContext applicationContext;

    private Mono<ServerResponse> trans(ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParam("trans").orElseGet(() -> "JSON"))
                .map(TransEnum::valueOf)
                .map(TransEnum::getClazz)
                .map(clazz -> applicationContext.getBean(clazz))
                .flatMap(translater -> ServerResponse.ok().bodyValue(((Translater)translater).trans()));
    }
}
