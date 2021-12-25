package com.rexense.smart.model;

public class ItemBinding {
    private String Function;
    private String SrcAddr;
    private String SrcEndpointId;
    private String DstAddrMode;
    private String DstAddr;
    private String DstEndpointId;

    public ItemBinding() {
    }

    public ItemBinding(String function, String srcAddr, String srcEndpointId, String dstAddrMode, String dstAddr, String dstEndpointId) {
        Function = function;
        SrcAddr = srcAddr;
        SrcEndpointId = srcEndpointId;
        DstAddrMode = dstAddrMode;
        DstAddr = dstAddr;
        DstEndpointId = dstEndpointId;
    }

    public String getFunction() {
        return Function;
    }

    public void setFunction(String function) {
        Function = function;
    }

    public String getSrcAddr() {
        return SrcAddr;
    }

    public void setSrcAddr(String srcAddr) {
        SrcAddr = srcAddr;
    }

    public String getSrcEndpointId() {
        return SrcEndpointId;
    }

    public void setSrcEndpointId(String srcEndpointId) {
        SrcEndpointId = srcEndpointId;
    }

    public String getDstAddrMode() {
        return DstAddrMode;
    }

    public void setDstAddrMode(String dstAddrMode) {
        DstAddrMode = dstAddrMode;
    }

    public String getDstAddr() {
        return DstAddr;
    }

    public void setDstAddr(String dstAddr) {
        DstAddr = dstAddr;
    }

    public String getDstEndpointId() {
        return DstEndpointId;
    }

    public void setDstEndpointId(String dstEndpointId) {
        DstEndpointId = dstEndpointId;
    }
}
