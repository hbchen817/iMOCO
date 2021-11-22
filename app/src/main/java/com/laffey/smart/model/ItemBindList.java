package com.laffey.smart.model;

import java.util.ArrayList;
import java.util.List;

public class ItemBindList {
    private String name;
    private String mac;
    private String groupId;
    private List<ItemBindRelation> bindList = new ArrayList<>();

    public ItemBindList() {
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemBindRelation> getBindList() {
        return bindList;
    }

    public void setBindList(List<ItemBindRelation> bindList) {
        this.bindList = bindList;
    }
}
