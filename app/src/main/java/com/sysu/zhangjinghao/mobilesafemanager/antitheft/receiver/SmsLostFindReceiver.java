package com.sysu.zhangjinghao.mobilesafemanager.antitheft.receiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.service.GPSLocationService;

/**
 * Created by zhangjinghao on 16/5/30.
 */
public class SmsLostFindReceiver extends BroadcastReceiver {
    private SharedPreferences mSharedPreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        mSharedPreferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        boolean isProtecting = mSharedPreferences.getBoolean("isProtecting", true);
        if(isProtecting) {
            //获取超级管理员
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            Object[] objects = (Object[])intent.getExtras().get("pdus");
            for(Object object: objects) {
                SmsMessage smsManager = SmsMessage.createFromPdu((byte[]) object);
                String sender = smsManager.getOriginatingAddress();
                String body = smsManager.getMessageBody();
                String safeNumber = mSharedPreferences.getString("safeNumber", null);
                if(!TextUtils.isEmpty(safeNumber) && sender.equals(safeNumber)) {
                    if(body.equals("#*location*#")) {
                        Log.i("TAG", "返回信息位置");
                        //获取位置，放到服务Service里面去实现
                        Intent service = new Intent(context, GPSLocationService.class);
                        context.startService(service);
                        abortBroadcast();
                    } else if(body.equals("#*alarm*#")) {
                        Log.i("TAG", "播放报警音乐");
                        MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                        player.setVolume(1.0f, 1.0f);
                        player.start();
                        abortBroadcast();
                    } else if(body.equals("#*wipedata*#")) {
                        Log.i("TAG", "远程清除数据");
                        devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                        abortBroadcast();
                    } else if(body.equals("#*lockscreen*#")) {
                        Log.i("TAG", "远程锁屏");
                        devicePolicyManager.resetPassword("123", 0);
                        devicePolicyManager.lockNow();
                        abortBroadcast();
                    }
                }

            }
        }
    }
}
