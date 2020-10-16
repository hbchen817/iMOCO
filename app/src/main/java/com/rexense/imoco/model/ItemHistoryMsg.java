package com.rexense.imoco.model;

import com.rexense.imoco.typefactory.TypeFactory;

/**
 * @author Gary
 * @time 2020/10/15 17:08
 */

public class ItemHistoryMsg implements Visitable{

    private String time;
    private String type;
    private String content;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}