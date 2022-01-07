package com.laffey.smart.utility;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.laffey.smart.BuildConfig;

public class GsonUtil {

    public static String toJson(Object o) {
        if (BuildConfig.DEBUG) {
            try {
                return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(o);
            } catch (Exception e) {
                return JSONObject.toJSONString(o);
            }
        } else {
            return JSONObject.toJSONString(o);
        }
    }

    public static Gson create() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }
}
