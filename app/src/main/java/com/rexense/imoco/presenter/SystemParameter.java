package com.rexense.imoco.presenter;

import android.content.Context;

import com.rexense.imoco.R;
import com.rexense.imoco.model.ESystemParameter;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 系统参数
 */
public class SystemParameter {
    private static ESystemParameter mSystemParameter;

    // 初始化
    public static void initProcess(Context context) {
        mSystemParameter = new ESystemParameter();
        mSystemParameter.setBrand(context.getString(R.string.app_brand));
        mSystemParameter.setBrandShow(context.getString(R.string.app_brand_show));
        mSystemParameter.setIsAddDemoDevice(context.getString(R.string.app_is_add_xz_device));
    }

    // 获取系统参数实例
    public static ESystemParameter getInstance() {
        return mSystemParameter;
    }
}
