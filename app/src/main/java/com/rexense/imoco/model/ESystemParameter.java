package com.rexense.imoco.model;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 系统参数实体
 */
public class ESystemParameter {
    // 是否登录
    private boolean mIsLogin;
    // 品牌
    private String mBrand;
    // 显示品牌
    private String mBrandShow;
    // 家Id
    private String mHomeId;
    // 家名称
    private String mHomeName;
    // 是否刷新数据
    private boolean mIsRefreshData;
    // 是否添加携住设备
    private String mIsAddXZDevice;

    // 构造
    public ESystemParameter(){
        this.mIsLogin = false;
        this.mHomeId = "";
        this.mIsRefreshData = false;
    }

    // 设置是否登录
    public void setIsLogin(boolean isLogin) {
        this.mIsLogin = isLogin;
    }

    // 设置品牌
    public void setBrand(String brand){
        this.mBrand = brand;
    }

    // 设置显示品牌
    public void setBrandShow(String brand){
        this.mBrandShow = brand;
    }

    // 设置家Id
    public void setHomeId(String homeId) {
        this.mHomeId = homeId;
    }

    // 设置家Id
    public void setHomeName(String homeName) {
        this.mHomeName = homeName;
    }

    // 设置是否刷新数据
    public void setIsRefreshData(boolean isRefreshData) {
        this.mIsRefreshData = isRefreshData;
    }

    // 设置是否添加携住设备
    public void setIsAddDemoDevice(String isAddXZDevice){
        this.mIsAddXZDevice = isAddXZDevice;
    }

    // 获取是否登录
    public boolean getIsLogin() {
        return this.mIsLogin;
    }

    // 获取品牌
    public String getBrand(){
        return  this.mBrand;
    }

    // 获取显示品牌
    public String getBrandShow(){
        return  this.mBrandShow;
    }

    // 获取家Id
    public String getHomeId() {
        return this.mHomeId == null ? "" : this.mHomeId;
    }

    // 获取家名称
    public String getHomeName() {
        return this.mHomeName == null ? "" : this.mHomeName;
    }

    // 获取是否刷新数据
    public boolean getIsRefreshData() {
        return this.mIsRefreshData;
    }

    // 获取是否添加携住设备
    public String getIsAddXZDevice(){
        return this.mIsAddXZDevice;
    }
}

