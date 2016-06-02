package com.sysu.zhangjinghao.mobilesafemanager.blacklist.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;

import com.sysu.zhangjinghao.mobilesafemanager.blacklist.db.dao.BlackNumberDao;

/**
 * Created by zhangjinghao on 16/6/1.
 */
public class InterceptSmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean blackNumStatus = sharedPreferences.getBoolean("blackNumStatus", true);
        if(!blackNumStatus) {
            return;
        }

        BlackNumberDao dao = new BlackNumberDao(context);
        Object[] objects = (Object[])intent.getExtras().get("pdus");
        for(Object obj : objects) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
            String sender = smsMessage.getOriginatingAddress();
            String body = smsMessage.getMessageBody();
            if(sender.startsWith("+86")) {
                sender = sender.substring(3, sender.length());
            }
            int mode = dao.getBlackContactMode(sender);
            if(mode == 2 || mode == 3) {
                //拦截广播
                abortBroadcast();
            }
        }
    }
}
