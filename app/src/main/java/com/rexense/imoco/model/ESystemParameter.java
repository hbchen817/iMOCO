package com.rexense.imoco.model;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 系统参数实体
 */
public class ESystemParameter {
    // 是否登录
    private boolean mIsLogin;
    // 家Id
    private String mHomeId;
    // 家名称
    private String mHomeName;
    // 是否刷新数据
    private boolean mIsRefreshData;

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

    // 获取是否登录
    public boolean getIsLogin() {
        return this.mIsLogin;
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
}

