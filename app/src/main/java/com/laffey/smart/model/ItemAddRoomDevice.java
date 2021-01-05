package com.laffey.smart.model;


import com.laffey.smart.typefactory.TypeFactory;

/**
 * @author fanyy
 * @date 2018/5/22
 */
public class ItemAddRoomDevice implements Visitable {
    private String id;
    private String deviceName;
    private String productKey;
    private int type;//0不在此房间，1在此房间

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

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
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
