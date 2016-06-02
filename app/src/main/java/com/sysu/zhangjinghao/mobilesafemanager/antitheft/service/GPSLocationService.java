package com.sysu.zhangjinghao.mobilesafemanager.antitheft.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zhangjinghao on 16/5/30.
 */
public class GPSLocationService extends Service {
    private LocationManager locationManager;
    private MyListener myListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myListener = new MyListener();

        //criteria查询条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        String name = locationManager.getBestProvider(criteria, true);
        Toast.makeText(this, "最好的位置提供者:" + name, Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(name, 0, 0, myListener);
    }

    private class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            StringBuilder sb = new StringBuilder();
            sb.append("accuracy:"+location.getAccuracy()+"\n");
            sb.append("speed:"+location.getSpeed()+"\n");
            sb.append("jingdu:"+location.getLongitude()+"\n");
            sb.append("weidu:"+location.getLatitude()+"\n");
            String result = sb.toString();
            //Log.d("TAG", result);
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String safeNumber = sharedPreferences.getString("safeNumber", "");
            SmsManager.getDefault().sendTextMessage(safeNumber, null, result, null, null);
            stopSelf();//结束服务
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
