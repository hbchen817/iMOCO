package com.rexense.wholehouse.model;

import com.rexense.wholehouse.typefactory.TypeFactory;

import java.io.Serializable;

/**
 * @author Gary
 * @time 2020/10/19 10:10
 */

public class ItemUserKey implements Visitable , Serializable {

    private boolean haveHeader;

    private String lockUserId;//钥匙ID

    private int lockUserType;//钥匙类型

    private int lockUserPermType;//钥匙权限

    private String userId;//虚拟用户ID

    private String keyNickName;//钥匙昵称

    private String userName;//虚拟用户名称

    public boolean isHaveHeader() {
        return haveHeader;
    }

    public void setHaveHeader(boolean haveHeader) {
        this.haveHeader = haveHeader;
    }

    public String getLockUserId() {
        return lockUserId;
    }

    public void setLockUserId(String lockUserId) {
        this.lockUserId = lockUserId;
    }

    public int getLockUserType() {
        return lockUserType;
    }

    public void setLockUserType(int lockUserType) {
        this.lockUserType = lockUserType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeyNickName() {
        return keyNickName;
    }

    public void setKeyNickName(String keyNickName) {
        this.keyNickName = keyNickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLockUserPermType() {
        return lockUserPermType;
    }

    public void setLockUserPermType(int lockUserPermType) {
        this.lockUserPermType = lockUserPermType;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}