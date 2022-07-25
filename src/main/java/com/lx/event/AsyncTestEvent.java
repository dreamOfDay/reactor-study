package com.lx.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author: jyu
 * @Date: 2022/7/25
 * @Description:
 **/
public class AsyncTestEvent extends ApplicationEvent {

    public AsyncTestEvent(Object source, String eleId) {
        super(source);
        this.eleId = eleId;
    }

    private String eleId;

    public String getEleId() {
        return eleId;
    }
}
