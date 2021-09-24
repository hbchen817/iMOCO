package com.laffey.smart.model;

public class ItemBinding {
    private String function;
    private String srcAddr;
    private String srcEndpointId;
    private String dstAddrMode;
    private String dstAddr;
    private String dstEndpointId;

    public ItemBinding(String function, String srcAddr, String srcEndpointId, String dstAddrMode, String dstAddr, String dstEndpointId) {
        this.function = function;
        this.srcAddr = srcAddr;
        this.srcEndpointId = srcEndpointId;
        this.dstAddrMode = dstAddrMode;
        this.dstAddr = dstAddr;
        this.dstEndpointId = dstEndpointId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getSrcAddr() {
        return srcAddr;
    }

    public void setSrcAddr(String srcAddr) {
        this.srcAddr = srcAddr;
    }

    public String getSrcEndpointId() {
        return srcEndpointId;
    }

    public void setSrcEndpointId(String srcEndpointId) {
        this.srcEndpointId = srcEndpointId;
    }

    public String getDstAddrMode() {
        return dstAddrMode;
    }

    public void setDstAddrMode(String dstAddrMode) {
        this.dstAddrMode = dstAddrMode;
    }

    public String getDstAddr() {
        return dstAddr;
    }

    public void setDstAddr(String dstAddr) {
        this.dstAddr = dstAddr;
    }

    public String getDstEndpointId() {
        return dstEndpointId;
    }

    public void setDstEndpointId(String dstEndpointId) {
        this.dstEndpointId = dstEndpointId;
    }
}
