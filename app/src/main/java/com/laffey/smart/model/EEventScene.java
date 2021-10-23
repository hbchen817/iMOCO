package com.laffey.smart.model;

import com.alibaba.fastjson.JSONObject;

public class EEventScene implements Cloneable{
    private String target;
    private String gatewayId;
    private String gatewayMac;
    private JSONObject appParams;
    private ItemScene scene;

    public JSONObject getAppParams() {
        return appParams;
    }

    public void setAppParams(JSONObject appParams) {
        this.appParams = appParams;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public ItemScene getScene() {
        return scene;
    }

    public void setScene(ItemScene scene) {
        this.scene = scene;
    }

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        this.gatewayMac = gatewayMac;
    }

    @Override
    public EEventScene clone() throws CloneNotSupportedException {
        EEventScene scene = null;
        try {
            scene = (EEventScene) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return scene;
    }
}
