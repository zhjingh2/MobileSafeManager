package com.sysu.zhangjinghao.mobilesafemanager.blacklist.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zhangjinghao on 16/6/1.
 */
public class InterceptCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean blackNumStatus = sharedPreferences.getBoolean("blackNumStatus", true);
        if(!blackNumStatus) {
            return;
        }
        BlackNumberDao dao = new BlackNumberDao(context);
        if(!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String incomingNumber = "";
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (telephonyManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingNumber = intent.getStringExtra("incoming_number");
                    int blackContactMode = dao.getBlackContactMode(incomingNumber);
                    if(blackContactMode == 1 || blackContactMode == 3) { //需要拦截
                        Uri uri = Uri.parse("content://call_log/calls");
                        context.getContentResolver().registerContentObserver(uri, true,
                                new CallLogObserver(new Handler(), incomingNumber, context));

                        //挂断电话，需要复制两个AIDL
                        try {
                            Class cls = context.getClassLoader().loadClass("android.os.ServiceManager");
                            Method method = cls.getDeclaredMethod("getService", String.class);
                            IBinder iBinder = (IBinder)method.invoke(null, Context.TELEPHONY_SERVICE);
                            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                            iTelephony.endCall();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
    }

    //通过内容观察者观察数据库变化
    private class CallLogObserver extends ContentObserver {
        private String incomingNumber;
        private Context mContext;

        public CallLogObserver(Handler handler, String incomingNumber, Context mContext) {
            super(handler);
            this.incomingNumber = incomingNumber;
            this.mContext = mContext;
        }

        @Override
        public void onChange(boolean selfChange) {//观察到数据库内容变化调用的方法
            Log.i("TAG", "呼叫记录数据库的内容变化了");
            mContext.getContentResolver().unregisterContentObserver(this);

            //清除呼叫记录
            ContentResolver resolver = mContext.getContentResolver();
            Uri uri = Uri.parse("content://call_log/calls");
            Cursor cursor = resolver.query(uri, new String[] {"_id"}, "number=?", new String[] {incomingNumber}, "_id desc limit 1");
            if(cursor.moveToNext()) {
                String id = cursor.getString(0);
                resolver.delete(uri, "_id=?", new String[] {id});
            }

            super.onChange(selfChange);
        }
    }
}
