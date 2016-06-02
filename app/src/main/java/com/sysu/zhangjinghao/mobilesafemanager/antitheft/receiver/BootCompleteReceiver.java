package com.sysu.zhangjinghao.mobilesafemanager.antitheft.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sysu.zhangjinghao.mobilesafemanager.MyApplication;

/**
 * Created by zhangjinghao on 16/5/29.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ((MyApplication)context.getApplicationContext()).checkSIM();
        Log.d("TAG","开启冻");
    }
}
