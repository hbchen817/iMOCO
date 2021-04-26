package com.rexense.wholehouse.sdk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.presenter.MocoApplication;
import com.rexense.wholehouse.view.MsgCenterActivity;

import java.util.Date;
import java.util.Map;

public class MyMessageReceiver extends MessageReceiver {
    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "receiver";
    private int mNotificationId = 820;

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        Log.e("MyMessageReceiver", "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        Log.e("MyMessageReceiver", "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        sendNotification(cPushMessage.getTitle(), cPushMessage.getContent());
    }

    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
        if (title != null && title.contains("向您共享设备")) {
            Intent intent = new Intent(context, MsgCenterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("current_pos", 1);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.e("MyMessageReceiver", "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.e("MyMessageReceiver", "onNotificationRemoved");
    }

    /**
     * 发送通知
     */
    private void sendNotification(String title, String message) {
        long[] vibrate = new long[]{0, 500};
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) MocoApplication.sContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MocoApplication.sContext)
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(MocoApplication.sContext.getResources(), R.mipmap.ic_launcher, null))
                //设置通知标题
                .setContentTitle(title)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(new Date().getTime())
                //设置通知内容
                .setContentText(message)
                .setAutoCancel(true)
                .setOngoing(false)
                .setVibrate(vibrate);

        // 此处必须兼容android O设备，否则系统版本在O以上可能不展示通知栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MocoApplication.sContext.getPackageName(),
                    "传感器警报",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setBypassDnd(true);    //设置绕过免打扰模式
            channel.enableVibration(true);
            channel.setVibrationPattern(vibrate);
            notifyManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }
        Notification notification = builder.build();
        mNotificationId += 1;
        notifyManager.notify(mNotificationId, notification);
    }
}
