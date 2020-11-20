package com.xiezhu.jzj.sdk;

import android.content.Context;
import android.os.Bundle;

import com.aliyun.iot.aep.component.router.Router;

import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-24 15:29
 * Description: 插件
 */
public class Plugin {
    // 调用
    public static void call(Context context, String url, Map<String, Object> parameter) {
        Bundle bundle = null;
        if(parameter != null && parameter.size() > 0) {
            bundle = new Bundle();
            for (String key : parameter.keySet()) {
                bundle.putString(key, parameter.get(key).toString());
            }
        }
        if(bundle != null) {
            Router.getInstance().toUrl(context, url, bundle);
        } else {
            Router.getInstance().toUrl(context, url);
        }
    }
}

