package com.lx.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @Author: jyu
 * @Date: 2022/7/25
 * @Description:
 **/
public class TestEvent extends ApplicationEvent {

    private String eleId;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public TestEvent(Object source, String eleId) {
        super(source);
        this.eleId = eleId;
    }

    public String getEleId() {
        return eleId;
    }
}
