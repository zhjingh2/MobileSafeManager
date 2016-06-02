package com.sysu.zhangjinghao.mobilesafemanager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by zhangjinghao on 16/5/29.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        checkSIM();
    }

    public void checkSIM() {
        SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean isProtecting = sharedPreferences.getBoolean("isProtecting", true);
        if(isProtecting) {
            String bindSimSerial = sharedPreferences.getString("sim", "");
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String currentSimSerial = telephonyManager.getSimSerialNumber();
            if(bindSimSerial.equals(currentSimSerial)) {
                Toast.makeText(this, "SIM卡未发生变化，还是您的手机", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SIM卡变化了", Toast.LENGTH_SHORT).show();

                //给安全号码发短信
                String safeNumber = sharedPreferences.getString("safeNumber", "");
                if(!TextUtils.isEmpty(safeNumber)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(safeNumber, null, "您的亲友手机的SIM卡已经被更换！", null, null);
                }
            }
        }
    }

}
