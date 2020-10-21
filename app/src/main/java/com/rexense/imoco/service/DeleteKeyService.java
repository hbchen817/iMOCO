package com.rexense.imoco.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.ItemUser;
import com.rexense.imoco.presenter.LockManager;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.view.LockDetailActivity;

import java.lang.ref.WeakReference;

public class DeleteKeyService extends Service {
    public DeleteKeyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("InitService", getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), "InitService").build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RealtimeDataReceiver.addEventCallbackHandler("LockEventCallback", new LockHandler());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class LockHandler extends Handler {

        private String mLockUserId;
        private int mLockType;
        private String mIotId;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY:
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    JSONObject params = jsonObject.getJSONObject("params");
                    JSONObject value = params.getJSONObject("value");
                    String identifier = params.getString("identifier");
                    switch (identifier) {
                        case "KeyDeletedNotification":
                            mLockUserId = value.getString("KeyID");
                            mLockType = value.getIntValue("LockType");
                            mIotId = value.getString("iotId");
                            LockManager.getUserByKey(mLockUserId, mLockType, mIotId, null, null, this);
                            break;
                        default:
                            break;
                    }
                    break;
                case Constant.MSG_CALLBACK_KEY_USER_GET:
                    if (!TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject user = JSON.parseObject((String) msg.obj);
                        LockManager.userKeyUnbind(user.getString("userId"), mLockUserId, mLockType, mIotId, null, null, this);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
