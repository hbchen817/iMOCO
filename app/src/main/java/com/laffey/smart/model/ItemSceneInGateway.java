package com.laffey.smart.model;

import com.alibaba.fastjson.JSONObject;

public class ItemSceneInGateway {
    private String gwMac;
    private JSONObject appParams;
    private ItemScene sceneDetail;

    public String getGwMac() {
        return gwMac;
    }

    public void setGwMac(String gwMac) {
        this.gwMac = gwMac;
    }

    public JSONObject getAppParams() {
        return appParams;
    }

    public void setAppParams(JSONObject appParams) {
        this.appParams = appParams;
    }

    public ItemScene getSceneDetail() {
        return sceneDetail;
    }

    public void setSceneDetail(ItemScene sceneDetail) {
        this.sceneDetail = sceneDetail;
    }
}
