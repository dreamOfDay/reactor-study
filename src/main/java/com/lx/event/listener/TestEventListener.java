package com.lx.event.listener;

import com.lx.event.AsyncTestEvent;
import com.lx.event.TestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author: jyu
 * @Date: 2022/7/25
 * @Description:
 **/
@Slf4j
@Component
public class TestEventListener {

    /**
     * 真实场景，这里可以是一个service业务层监听对应事件，比如当前service是清空用户token，可以监听用户login out的事件
     *
     * @param testEvent
     */
    @EventListener
    public void testEventListen(TestEvent testEvent){
        log.info("get data from TestEvent#getEleId: {}",testEvent.getEleId());
    }

    /**
     * 测试异步监听，注意可以自行看看console种的thread，如果需要使用自定义线程池可以在pushEvent之前指定线程池
     * @param asyncTestEvent
     */
    @Async
    @EventListener
    public void asyncTestEventListen(AsyncTestEvent asyncTestEvent){
        log.info("get data from AsyncTestEvent#getEleId: {}",asyncTestEvent.getEleId());
    }
}
