package com.rexense.smart.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 配置助手
 */
public class Configure {
	// 获取配置项值
	public static String getItem(Context context, String key, String defaultValue) {
		SharedPreferences pre = context.getSharedPreferences("Config", Context.MODE_PRIVATE);
		if(pre == null) {
			return null;
		} else {
			return pre.getString(key, defaultValue);
		}
	}

	// 设置配置项值
	public static void setItem(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences("Config", Context.MODE_PRIVATE);
		//获取编辑器
		SharedPreferences.Editor editor = sp.edit();
		//存入String型数据
		editor.putString(key, value);
		editor.commit();
	}
}
