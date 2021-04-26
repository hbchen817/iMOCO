package com.rexense.wholehouse.event;

public class RefreshRoomName {
    private String name;

    public RefreshRoomName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
