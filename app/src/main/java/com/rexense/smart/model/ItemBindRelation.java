package com.rexense.smart.model;

public class ItemBindRelation {
    private String mac;
    private String endpoint;
    // private boolean mainBind;//True-为主绑定 False-不为主绑定
    private String groupId;
    private String subDevMac;

    public ItemBindRelation() {
    }

    public String getSubDevMac() {
        return subDevMac;
    }

    public void setSubDevMac(String subDevMac) {
        this.subDevMac = subDevMac;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /*public boolean getMainBind() {
        return mainBind;
    }

    public void setMainBind(boolean mainBind) {
        this.mainBind = mainBind;
    }*/

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
