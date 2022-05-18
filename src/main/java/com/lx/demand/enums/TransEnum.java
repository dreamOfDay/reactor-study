package com.lx.demand.enums;

import com.lx.demand.trans.JsonTranslater;
import com.lx.demand.trans.Translater;
import com.lx.demand.trans.XmlTranslater;
import lombok.Getter;

/**
 * @Author: jyu
 * @Date: 2022/5/18
 * @Description:
 **/
@Getter
public enum TransEnum {
    JSON("json", JsonTranslater.class),
    XML("xml", XmlTranslater.class)
    ;


    private String name;
    private Class<?> clazz;

    TransEnum(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

}
