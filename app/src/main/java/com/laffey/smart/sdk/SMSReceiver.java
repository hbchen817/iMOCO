package com.laffey.smart.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.vise.log.ViseLog;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 进行获取短信的操作
        ViseLog.d("短信 number =  , body = ");
        getMsg(context, intent);
    }

    private void getMsg(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String number = sms.getOriginatingAddress();
            String body = sms.getMessageBody();
            ViseLog.d("短信 number = " + number + " , body = " + body);
        }
    }
}
