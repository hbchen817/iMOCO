package com.laffey.smart.event;

public class RefreshMsgCenter {
    private int type;//1 共享设备消息 0 其他消息

    public RefreshMsgCenter(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
