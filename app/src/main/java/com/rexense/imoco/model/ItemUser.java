package com.rexense.imoco.model;

import com.rexense.imoco.typefactory.TypeFactory;

/**
 * @author Gary
 * @time 2020/10/13 15:25
 */

public class ItemUser implements Visitable {

    private String ID;

    private String name;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}