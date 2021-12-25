package com.rexense.smart.model;

public class EAction {
    private String target;// 目标activity
    private String iotId;
    private String keyNickName;
    private ItemScene.Action action;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getKeyNickName() {
        return keyNickName;
    }

    public void setKeyNickName(String keyNickName) {
        this.keyNickName = keyNickName;
    }

    public ItemScene.Action getAction() {
        return action;
    }

    public void setAction(ItemScene.Action action) {
        this.action = action;
    }
}
