package com.laffey.smart.model;

public class EEventScene {
    private String target;
    private String gatewayId;
    private ItemScene scene;

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
}
