package com.rexense.imoco.presenter;

import android.content.Context;

import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.sdk.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 用户账号助手
 */
public class PluginHelper {
    // 云端定时器
    public static void cloudTimer(Context context, String iotId, String productKey) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("iotId", iotId);
        parameters.put("productKey", productKey);
        Plugin.call(context, Constant.PLUGIN_URL_COUNDTIMER, parameters);
    }

    // 创建场景
    public static void createScene(Context context, String sceneType, String homeId){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("sceneType", sceneType == null || sceneType.length() == 0 ? CScene.TYPE_IFTTT : sceneType);
        parameters.put("homeId", homeId);
        parameters.put("catalogId", 1);
        Plugin.call(context, Constant.PLUGIN_URL_SCENE, parameters);
    }

    // 编辑场景
    public static void editScene(Context context, String sceneType, String catalogId, String homeId, String sceneId){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("sceneType", sceneType == null || sceneType.length() == 0 ? CScene.TYPE_IFTTT : sceneType);
        parameters.put("sceneId", sceneId);
        parameters.put("homeId", homeId);
        parameters.put("catalogId", catalogId);
        Plugin.call(context, Constant.PLUGIN_URL_SCENE, parameters);
    }
}
