package com.rexense.imoco.sdk;

import android.os.Message;

import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileDownstreamListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.rexense.imoco.model.ERealtimeData;
import com.rexense.imoco.utility.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Creator: xieshaobing
 * creat time: 2020-04-18 15:29
 * Description: 长连接
 */
public class LongConnection {
    private static Map<String, Integer> mTopics;
    private static Lock mLock;
    private static Map<String, ERealtimeData.callbackHandlerEntry> mCallbackHandlerList;
    private static boolean mIsConnected;
    // 定义并创建下行数据监视器
    private static IMobileDownstreamListener mDownstreamListener = new IMobileDownstreamListener() {
        @Override
        public void onCommand(String s, String s1) {
            Logger.d(String.format("Received the data of long connection:\r\n    topic: %s\r\n    data: %s", s, s1));

            if (mCallbackHandlerList == null || mCallbackHandlerList.size() == 0) {
                return;
            }

            mLock.lock();
            try {
                for (String key : mCallbackHandlerList.keySet()) {
                    ERealtimeData.callbackHandlerEntry entry = mCallbackHandlerList.get(key);
                    if (entry != null && s.indexOf(entry.topic) >= 0 && entry.handler != null) {
                        Message msg = new Message();
                        msg.what = entry.messageType;
                        msg.obj = s1;
                        entry.handler.sendMessage(msg);
                    }
                }
            } catch (Exception ex) {
                Logger.e("Failed to receive data");
            } finally {
                mLock.unlock();
            }
        }

        @Override
        public boolean shouldHandle(String s) {
            Logger.d(String.format("Received confirm of handle topic [%s]", s));
            // 允许在onCommand中处理主题
            if(mTopics.containsKey(s)) {
                Logger.d(String.format("Should handle topic [%s]", s));
                return true;
            }
            return false;
        }
    };
    // 定义并创建连接状态监视器
    private static IMobileConnectListener mConnectListener = new IMobileConnectListener() {
        @Override
        public void onConnectStateChange(MobileConnectState mobileConnectState) {
            if (MobileConnectState.CONNECTED == mobileConnectState) {
                mIsConnected = true;
            } else if (MobileConnectState.DISCONNECTED == mobileConnectState) {
                MobileChannel.getInstance().unRegisterDownstreamListener(mDownstreamListener);
                MobileChannel.getInstance().unRegisterConnectListener(mConnectListener);
                mIsConnected = false;
                Logger.e("LongConnection disconnected with the cloud server.");
                // 重新开始接收数据
                startReceiveData();
            }
        }
    };

    // 初始化处理
    public static void initProcess(List<String> topics) {
        mTopics = new HashMap<String, Integer>();
        int i = 0;
        for(String topic : topics) {
            mTopics.put(topic, i++);
        }
        mLock = new ReentrantLock();
        mCallbackHandlerList = new HashMap<String, ERealtimeData.callbackHandlerEntry>();
        mIsConnected = true;
    }

    // 开始接收数据
    public static void startReceiveData() {
        MobileChannel.getInstance().registerDownstreamListener(true, mDownstreamListener);
        MobileChannel.getInstance().registerConnectListener(true, mConnectListener);
        Logger.d("Started receiving data from long connection");
        mIsConnected = true;
    }

    // 添加回调处理器
    public static boolean addCallbackHandler(String key, ERealtimeData.callbackHandlerEntry entry) {
        mLock.lock();
        boolean r = true;
        try {
            if(mCallbackHandlerList.containsKey(key)) {
                mCallbackHandlerList.remove(key);
            }
            mCallbackHandlerList.put(key, entry);
        } catch (Exception ex) {
            r = false;
            Logger.e("Failed to add callback handler of long connection, may is: " + ex.getMessage());
        }
        finally {
            mLock.unlock();
        }
        return r;
    }

    // 删除回调处理器
    public static boolean deleteCallbackHandler(String key) {
        mLock.lock();
        boolean r = true;
        try {
            if(mCallbackHandlerList.containsKey(key)) {
                mCallbackHandlerList.remove(key);
            }
        } catch (Exception ex) {
            r = false;
            Logger.e("Failed to delete callback handler of long connection");
        }
        finally {
            mLock.unlock();
        }
        return r;
    }

    // 获取是否连接
    public static boolean getIsConnected() {
        return mIsConnected;
    }

    // 停止接收数据
    public static void stopReceiveData() {
        if(mIsConnected) {
            MobileChannel.getInstance().registerDownstreamListener(true, mDownstreamListener);
            MobileChannel.getInstance().registerConnectListener(true, mConnectListener);
            Logger.d("Stopped receiving data from long connection");
        }
    }
}
