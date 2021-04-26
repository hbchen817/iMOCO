package com.rexense.wholehouse.event;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-13 15:29
 * Description: 事件实体
 */
public class EEvent {
    // 名称
    public String name;
    // 参数
    public String parameter;

    // 构造
    public EEvent(String name) {
        this.name = name;
    }
    public EEvent(String name, String parameter) {
        this.name = name;
        this.parameter = parameter;
    }
}