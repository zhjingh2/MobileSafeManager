package com.sysu.zhangjinghao.mobilesafemanager.cacheclean;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.cacheclean.adapter.CacheCleanAdapter;
import com.sysu.zhangjinghao.mobilesafemanager.cacheclean.entity.CacheInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjinghao on 16/6/12.
 */
public class CacheClearListActivity extends Activity implements OnClickListener{

    protected static final int SCANNING = 100;
    protected static final int FINISH = 101;

    private PackageManager packageManager;

    private Thread thread;

    private TextView tvRecomand;
    private TextView tvCanClean;
    private ListView lvCache;
    private Button btnCache;
    private AnimationDrawable animationDrawable;

    private CacheCleanAdapter adapter;
    private List<CacheInfo> mCacheInfos = new ArrayList<CacheInfo>();
    private List<CacheInfo> tempCacheInfos = new ArrayList<CacheInfo>();

    private long cacheMemory = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SCANNING:
                    //扫描中
                    PackageInfo info = (PackageInfo)msg.obj;
                    tvRecomand.setText("正在扫描："+info.packageName);
                    tvCanClean.setText("已扫描缓存："+ Formatter.formatFileSize(CacheClearListActivity.this, cacheMemory));

                    mCacheInfos.clear();
                    mCacheInfos.addAll(tempCacheInfos);
                    adapter.notifyDataSetChanged();
                    lvCache.setSelection(mCacheInfos.size());
                    break;
                case FINISH:
                    //扫描完成
                    animationDrawable.stop();//停止动画
                    if(cacheMemory > 0) {
                        btnCache.setEnabled(true);
                    } else {
                        btnCache.setEnabled(false);
                        Toast.makeText(CacheClearListActivity.this, "您的手机清洁如新", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cache_clear_list);
        packageManager = getPackageManager();

        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        ImageView ivLeft = (ImageView)findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);
        ((TextView)findViewById(R.id.tv_title)).setText("缓存扫描");

        tvRecomand = (TextView)findViewById(R.id.tv_recommend_clean);
        tvCanClean = (TextView)findViewById(R.id.tv_can_clean);
        lvCache = (ListView)findViewById(R.id.lv_scan_cache);
        btnCache = (Button)findViewById(R.id.btn_clean_all);
        btnCache.setOnClickListener(this);

        animationDrawable = (AnimationDrawable)findViewById(R.id.iv_broom).getBackground();
        animationDrawable.setOneShot(false);
        animationDrawable.start();

        adapter = new CacheCleanAdapter(mCacheInfos, this);
        lvCache.setAdapter(adapter);

        fillData();
    }

    private void fillData() {
        thread = new Thread() {
            @Override
            public void run() {
                tempCacheInfos.clear();
                List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
                for(PackageInfo packageInfo : packageInfos) {

                    getCacheSize(packageInfo);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = packageInfo;
                    msg.what = SCANNING;
                    mHandler.sendMessage(msg);
                }

                Message msg = Message.obtain();
                msg.what = FINISH;
                mHandler.sendMessage(msg);
            }
        };
        thread.start();
    }

    private void getCacheSize(PackageInfo packageInfo) {
        try {
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(packageManager, packageInfo.packageName, new MyPackObserver(packageInfo));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animationDrawable.stop();

        if(thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_button:
                finish();
                break;
            case R.id.btn_clean_all:
                if(cacheMemory > 0) {
                    Intent intent = new Intent(this, CleanCacheActivity.class);
                    intent.putExtra("cacheMemory", cacheMemory);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    private class MyPackObserver extends android.content.pm.IPackageStatsObserver.Stub {

        private PackageInfo info;

        public MyPackObserver(PackageInfo info) {
            this.info = info;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;
            if(cacheSize > 0) {
                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.cacheSize = cacheSize;
                cacheInfo.packageName = info.packageName;
                cacheInfo.appIcon = info.applicationInfo.loadIcon(packageManager);
                cacheInfo.appName = info.applicationInfo.loadLabel(packageManager).toString();

                tempCacheInfos.add(cacheInfo);
                cacheMemory += cacheSize;
            }
        }
    }
}
