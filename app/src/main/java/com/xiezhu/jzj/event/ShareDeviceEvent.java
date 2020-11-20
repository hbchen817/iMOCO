package com.xiezhu.jzj.event;

public class ShareDeviceEvent {
    private String name;

    public ShareDeviceEvent(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
