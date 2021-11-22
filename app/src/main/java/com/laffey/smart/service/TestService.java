package com.laffey.smart.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.vise.log.ViseLog;

public class TestService extends Service {

    private PowerManager.WakeLock mWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TestService");

        mWakeLock.acquire();

        RealtimeDataReceiver.addEventCallbackHandler("TestServiceCallback", new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Constant.MSG_CALLBACK_LNEVENTNOTIFY) {
                    // 删除网关下的场景
                    JSONObject jsonObject = JSONObject.parseObject((String) msg.obj);
                    ViseLog.d("网关返回删除结果 LocalSceneActivity = " + jsonObject.toJSONString());
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("ManageSceneNotification".equals(identifier)) {
                        String type = value.getString("Type");
                        String status = value.getString("Status");
                        // status  0: 成功  1: 失败
                        if ("0".equals(status)) {
                            // type  1: 增加场景  2: 编辑场景  3: 删除场景
                            if ("3".equals(type)) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        testScene("152", 1);
                                    }
                                }, 2000);
                            } else if ("1".equals(type)) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        testScene("152", 3);
                                    }
                                }, 2000);
                            }
                        }
                    }
                }
                return false;
            }
        }));
        testScene("152", 3);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // yfsGov6Dv7Xw1NsdqTWo000000
        ViseLog.d("yfsGov6Dv7Xw1NsdqTWo000000");
        return null;
    }

    private void testScene(String sceneId, int type) {
        SceneManager.manageSceneService("yfsGov6Dv7Xw1NsdqTWo000000", sceneId, type,
                new Handler(), new Handler(), new Handler());
    }

    @Override
    public void onDestroy() {
        if (mWakeLock != null) {
            mWakeLock.release();

            mWakeLock = null;
        }
        super.onDestroy();
    }
}
