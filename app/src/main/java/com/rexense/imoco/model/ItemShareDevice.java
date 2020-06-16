package com.rexense.imoco.model;


import com.rexense.imoco.typefactory.TypeFactory;

/**
 * @author fanyy
 * @date 2018/5/22
 */
public class ItemShareDevice implements Visitable {
    private String id;
    private String deviceName;
    private int status;//右侧图片 0不显示 1 箭头 2 未选择 3 已选择
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int type (TypeFactory typeFactory){
        return typeFactory.type(this);
    }
}
