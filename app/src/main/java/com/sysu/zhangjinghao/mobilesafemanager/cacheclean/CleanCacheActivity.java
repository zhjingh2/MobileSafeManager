package com.sysu.zhangjinghao.mobilesafemanager.cacheclean;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by zhangjinghao on 16/6/12.
 */
public class CleanCacheActivity extends Activity implements View.OnClickListener{
    protected static final int CLEANNING = 100;
    protected static final int CLEAN_FINISH = 10;

    private AnimationDrawable animationDrawable;
    private long cacheMemory;
    private TextView tvMemory;
    private TextView tvMemoryUnit;
    private PackageManager packageManager;
    private FrameLayout flCleanCache;
    private FrameLayout flFinishClean;
    private TextView tvSize;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLEANNING:
                    long memory = (long)msg.obj;
                    formatMemory(memory);
                    if(memory==cacheMemory){
                        animationDrawable.stop();//停止动画
                        flCleanCache.setVisibility(View.GONE);
                        flFinishClean.setVisibility(View.VISIBLE);
                        tvSize.setText("成功清理："+ Formatter.formatFileSize(CleanCacheActivity.this, cacheMemory));
                    }
                    break;
            }
        }
    };

    private void formatMemory(long memory) {
        String cacheMemoryStr = Formatter.formatFileSize(this, memory);
        String memoryStr;
        String memoryUnit;
        if(memory >900){
            memoryStr = cacheMemoryStr.substring(0, cacheMemoryStr.length()-2);
            memoryUnit = cacheMemoryStr.substring(cacheMemoryStr.length()-2, cacheMemoryStr.length());
        }else{
            memoryStr = cacheMemoryStr.substring(0, cacheMemoryStr.length()-1);
            memoryUnit = cacheMemoryStr.substring(cacheMemoryStr.length()-1, cacheMemoryStr.length());
        }
        tvMemory.setText(memoryStr);
        tvMemoryUnit.setText(memoryUnit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_clean_cache);
        packageManager = getPackageManager();

        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        ((TextView) findViewById(R.id.tv_title)).setText("缓存清理");
        ImageView ivLeft = (ImageView) findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);
        tvMemory = (TextView) findViewById(R.id.tv_clean_cache_memory);
        tvMemoryUnit = (TextView) findViewById(R.id.tv_clean_cache_memory_unit);
        flCleanCache = (FrameLayout) findViewById(R.id.fl_clean_cache);
        flFinishClean = (FrameLayout) findViewById(R.id.fl_finish_clean);
        tvSize = (TextView) findViewById(R.id.tv_clean_memory_size);
        findViewById(R.id.btn_finish_clean_cache).setOnClickListener(this);

        animationDrawable = (AnimationDrawable)findViewById(R.id.iv_trash_bin_cache_clean).getBackground();
        animationDrawable.setOneShot(false);
        animationDrawable.start();

        Intent intent = getIntent();
        cacheMemory = intent.getLongExtra("cacheMemory", 0);

        initData();
    }

    private void initData() {
        cleanAll();//实际清理很快
        new Thread(){
            public void run() {
                long memory = 0;
                while(memory < cacheMemory){ //模拟清理的过程
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Random rand = new Random();
                    int i = rand.nextInt(1024);
                    memory += 1024*i;
                    if(memory > cacheMemory){
                        memory = cacheMemory;
                    }
                    Message message = Message.obtain();
                    message.what = CLEANNING;
                    message.obj = memory;
                    mHandler.sendMessageDelayed(message,200);
                }
            }
        }.start();
    }

    private void cleanAll(){
        Method[] methods = PackageManager.class.getMethods();
        for(Method method:methods){
            if("freeStorageAndNotify".equals(method.getName())){
                try {
                    method.invoke(packageManager, Integer.MAX_VALUE, new ClearCacheObserver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ;
            }
        }
        Toast.makeText(this, "清理完毕", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_button:
                finish();
                break;
            case R.id.btn_finish_clean_cache:
                finish();
                break;
        }
    }

    class ClearCacheObserver extends android.content.pm.IPackageDataObserver.Stub {
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            Log.i("STUB", "ClearCacheObserver监听到清理完毕");
        }
    }

}
