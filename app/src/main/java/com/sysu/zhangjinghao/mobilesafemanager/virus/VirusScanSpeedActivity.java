
package com.sysu.zhangjinghao.mobilesafemanager.virus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.virus.adapter.ScanVirusAdapter;
import com.sysu.zhangjinghao.mobilesafemanager.virus.dao.AntiVirusDao;
import com.sysu.zhangjinghao.mobilesafemanager.virus.entity.ScanAppInfo;
import com.sysu.zhangjinghao.mobilesafemanager.virus.utils.MD5Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by zhangjinghao on 16/6/11.
 */
public class VirusScanSpeedActivity extends Activity implements View.OnClickListener{

    protected static final int SCAN_BEGIN = 100;
    protected static final int SCANNING = 101;
    protected static final int SCAN_FINISH = 102;

    private PackageManager packageManager;
    private SharedPreferences mSharedPreferences;

    private TextView tvProcess;
    private TextView tvScansApp;
    private Button btnCancelScan;
    private ListView lvScanApps;
    private ScanVirusAdapter adapter;
    private ImageView ivScanningIcon;
    private RotateAnimation rotateAnimation;

    private List<ScanAppInfo> mScanAppInfos = new ArrayList<ScanAppInfo>();

    private boolean flag; //标示停止扫描按钮有无按下
    private boolean isStop;

    private int total; //总共app数量
    private int process;//当前检测到第几个app

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_BEGIN:
                    tvScansApp.setText("初始化杀毒引擎中。。。");
                    break;
                case SCANNING:
                    ScanAppInfo info = (ScanAppInfo)msg.obj;
                    tvScansApp.setText("正在扫描："+info.appName);
                    int speed = msg.arg1;
                    tvProcess.setText((speed*100/total)+"%");
                    mScanAppInfos.add(info);
                    adapter.notifyDataSetChanged();
                    lvScanApps.setSelection(mScanAppInfos.size());//让ListView滑动到最底端
                    break;
                case SCAN_FINISH:
                    tvScansApp.setText("扫描完成！");
                    ivScanningIcon.clearAnimation();
                    btnCancelScan.setBackgroundResource(R.drawable.scan_complete);
                    //保存此次扫描的时间
                    saveScanTime();
                    break;
            }
        }
    };

    private void saveScanTime() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        currentTime = "上次查杀时间："+currentTime;
        editor.putString("lastVirusScan", currentTime);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_virus_scan_speed);
        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        packageManager = getPackageManager();

        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        ((TextView)findViewById(R.id.tv_title)).setText("病毒查杀进度");
        ImageView ivLeft = (ImageView)findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);

        tvProcess = (TextView)findViewById(R.id.tv_scan_process);
        tvScansApp = (TextView)findViewById(R.id.tv_scans_app);
        btnCancelScan = (Button)findViewById(R.id.btn_cancel_scan);
        btnCancelScan.setOnClickListener(this);
        lvScanApps = (ListView)findViewById(R.id.lv_scan_apps);
        adapter = new ScanVirusAdapter(mScanAppInfos, this);
        lvScanApps.setAdapter(adapter);
        ivScanningIcon = (ImageView)findViewById(R.id.iv_scanning_icon);
        startAnim();

        scanVirus();

    }

    private void scanVirus() {
        flag = true;
        isStop = false;
        process = 0;
        mScanAppInfos.clear();

        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = SCAN_BEGIN;
                mHandler.sendMessage(msg);
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                total = installedPackages.size();
                for(PackageInfo info : installedPackages) {
                    if(!flag) {
                        isStop = true;
                        return;
                    }
                    String apkPath = info.applicationInfo.sourceDir;
                    //检查获取这个文件的MD5码
                    String md5Info = MD5Utils.getFileMd5(apkPath);
                    String result = AntiVirusDao.checkVirus(md5Info);

                    msg = Message.obtain();
                    msg.what = SCANNING;
                    ScanAppInfo scanAppInfo = new ScanAppInfo();
                    if(result == null) {
                        scanAppInfo.desc = "扫描安全";
                        scanAppInfo.isVirus = false;
                    } else {
                        scanAppInfo.desc = result;
                        scanAppInfo.isVirus = true;
                    }
                    process++;
                    scanAppInfo.packageName = info.packageName;
                    scanAppInfo.appName = info.applicationInfo.loadLabel(packageManager).toString();
                    scanAppInfo.appIcon = info.applicationInfo.loadIcon(packageManager);
                    msg.obj = scanAppInfo;
                    msg.arg1 = process;
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();

    }

    private void startAnim() {
        if(rotateAnimation == null) {
            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(2000);
        ivScanningIcon.startAnimation(rotateAnimation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_button:
                finish();
                break;
            case R.id.btn_cancel_scan:
                if(process == total && process > 0) {
                    //扫描已完成
                    finish();
                } else if(process > 0 && process < total && isStop == false) {
                    //扫描过程中取消扫描
                    ivScanningIcon.clearAnimation();
                    flag = false;
                    btnCancelScan.setBackgroundResource(R.drawable.restart_scan_btn);
                } else if(isStop) {
                    //取消扫描之后的状态，点击则重新扫描
                    startAnim();
                    scanVirus();//重新扫描
                    btnCancelScan.setBackgroundResource(R.drawable.cancel_scan_btn_selector);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
    }
}

