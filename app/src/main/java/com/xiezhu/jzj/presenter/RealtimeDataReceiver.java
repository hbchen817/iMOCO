package com.xiezhu.jzj.presenter;

import android.os.Handler;

import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.ERealtimeData;
import com.xiezhu.jzj.sdk.LongConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 实时数据接收器
 */
public class RealtimeDataReceiver {
    // 初始化处理
    public static void initProcess() {
        // 处理要订阅的所有主题
        List<String> receiveTopics = new ArrayList<String>();
        receiveTopics.add(Constant.TOPIC_PROPERTYNOTIFY);
        receiveTopics.add(Constant.TOPIC_EVENTNOTIFY);
        receiveTopics.add(Constant.TOPIC_THINGEVENTNOTIFY);
        receiveTopics.add(Constant.TOPIC_STATUSNOTIFY);
        receiveTopics.add(Constant.TOPIC_SUBDEVICEJOINNOTIFY);
        receiveTopics.add(Constant.TOPIC_OTAUPGRADENOTITY);

        LongConnection.initProcess(receiveTopics);
        LongConnection.startReceiveData();
    }

    // 添加子设备入网回调处理器
    public static boolean addJoinCallbackHandler(String key, Handler handler) {
        ERealtimeData.callbackHandlerEntry entry = new ERealtimeData.callbackHandlerEntry(handler,
                Constant.TOPIC_SUBDEVICEJOINNOTIFY, Constant.MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY);
        return LongConnection.addCallbackHandler(key, entry);
    }

    // 添加属性回调处理器
    public static boolean addPropertyCallbackHandler(String key, Handler handler) {
        ERealtimeData.callbackHandlerEntry entry = new ERealtimeData.callbackHandlerEntry(handler,
                Constant.TOPIC_PROPERTYNOTIFY, Constant.MSG_CALLBACK_LNPROPERTYNOTIFY);
        return LongConnection.addCallbackHandler(key, entry);
    }

    // 添加事件回调处理器
    public static boolean addEventCallbackHandler(String key, Handler handler) {
        ERealtimeData.callbackHandlerEntry entry = new ERealtimeData.callbackHandlerEntry(handler,
                Constant.TOPIC_EVENTNOTIFY, Constant.MSG_CALLBACK_LNEVENTNOTIFY);
        return LongConnection.addCallbackHandler(key, entry);
    }

    // 添加物模型事件回调处理器（用于处理网关解绑）
    public static boolean addThingEventCallbackHandler(String key, Handler handler) {
        ERealtimeData.callbackHandlerEntry entry = new ERealtimeData.callbackHandlerEntry(handler,
                Constant.TOPIC_THINGEVENTNOTIFY, Constant.MSG_CALLBACK_LNTHINGEVENTNOTIFY);
        return LongConnection.addCallbackHandler(key, entry);
    }

    // 添加设备状态回调处理器
    public static boolean addStatusCallbackHandler(String key, Handler handler) {
        ERealtimeData.callbackHandlerEntry entry = new ERealtimeData.callbackHandlerEntry(handler,
                Constant.TOPIC_STATUSNOTIFY, Constant.MSG_CALLBACK_LNSTATUSNOTIFY);
        return LongConnection.addCallbackHandler(key, entry);
    }

    // 添加OTA升级回调处理器
    public static boolean addOTACallbackHandler(String key, Handler handler) {
        ERealtimeData.callbackHandlerEntry entry = new ERealtimeData.callbackHandlerEntry(handler,
                Constant.TOPIC_OTAUPGRADENOTITY, Constant.MSG_CALLBACK_LNOTAUPGRADENOTIFY);
        return LongConnection.addCallbackHandler(key, entry);
    }

    // 删除回调处理器
    public static boolean deleteCallbackHandler(String key) {
        return LongConnection.deleteCallbackHandler(key);
    }

    // 获取是否连接
    public static boolean getIsConnected() {
        return LongConnection.getIsConnected();
    }
}
