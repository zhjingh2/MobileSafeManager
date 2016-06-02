package com.sysu.zhangjinghao.mobilesafemanager.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.home.utils.MyUtils;
import com.sysu.zhangjinghao.mobilesafemanager.home.utils.VersionUpdateUtils;

/**
 * Created by zhangjinghao on 16/5/20.
 */
public class SplashActivity extends Activity {
    //本地版本号
    private String mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        mVersion = MyUtils.getVersion(getApplicationContext());

        ((TextView)findViewById(R.id.tv_splash_version)).setText("版本号：" + mVersion);

        final VersionUpdateUtils versionUpdateUtils = new VersionUpdateUtils(mVersion, SplashActivity.this);
        new Thread() {
            @Override
            public void run() {
                //获取服务器版本号
                versionUpdateUtils.getCloudVersion();
            }
        }.start();

    }
}
