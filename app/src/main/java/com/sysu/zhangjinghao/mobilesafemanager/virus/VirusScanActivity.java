package com.sysu.zhangjinghao.mobilesafemanager.virus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhangjinghao on 16/6/11.
 */
public class VirusScanActivity extends Activity implements View.OnClickListener{
    private TextView tvLastTime;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_virus_scan);
        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        ((TextView)findViewById(R.id.tv_title)).setText("病毒查杀");
        ImageView ivLeft = (ImageView)findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);
        tvLastTime = (TextView)findViewById(R.id.tv_last_scan_time);
        findViewById(R.id.rl_all_scan_virus).setOnClickListener(this);

        copyDB("antivirus.db");
    }

    public void copyDB(final String dbName) {
        new Thread() {
            public void run() {
                try {
                    File file = new File(getFilesDir(), dbName);
                    if(file.exists() && file.length() > 0) {
                        Log.i("VirusScanActivity", "数据库已经存在！");
                        return;
                    }
                    InputStream is = getAssets().open(dbName);
                    FileOutputStream fos = openFileOutput(dbName, MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_button:
                finish();
                break;
            case R.id.rl_all_scan_virus:
                startActivity(new Intent(this, VirusScanSpeedActivity.class));
                break;
        }
    }
}
