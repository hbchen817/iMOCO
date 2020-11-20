package com.xiezhu.jzj.presenter;

import android.content.Context;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.ESystemParameter;

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
        mSystemParameter.setIsHidePrivacyPolicy(context.getString(R.string.app_is_hide_privacy_policy));
        mSystemParameter.setIsHideUserDeal(context.getString(R.string.app_is_hide_user_deal));
    }

    // 获取系统参数实例
    public static ESystemParameter getInstance() {
        return mSystemParameter;
    }
}
