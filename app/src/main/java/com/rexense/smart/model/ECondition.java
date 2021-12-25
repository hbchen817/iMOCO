package com.rexense.smart.model;

public class ECondition {
    private String target;// 目标activity
    private String iotId;
    private String keyNickName;
    private ItemScene.Condition condition;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getKeyNickName() {
        return keyNickName;
    }

    public void setKeyNickName(String keyNickName) {
        this.keyNickName = keyNickName;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public ItemScene.Condition getCondition() {
        return condition;
    }

    public void setCondition(ItemScene.Condition condition) {
        this.condition = condition;
    }
}
