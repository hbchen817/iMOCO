package com.rexense.imoco.presenter;

import com.rexense.imoco.model.ESystemParameter;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 系统参数
 */
public class SystemParameter {
    public static ESystemParameter mSystemParameter;

    // 初始化
    public static void initProcess() {
        mSystemParameter = new ESystemParameter();
    }

    // 获取系统参数实例
    public static ESystemParameter getInstance() {
        return mSystemParameter;
    }
}
