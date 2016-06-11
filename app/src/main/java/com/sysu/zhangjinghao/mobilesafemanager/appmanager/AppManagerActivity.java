package com.sysu.zhangjinghao.mobilesafemanager.appmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.appmanager.adapter.AppManagerAdapter;
import com.sysu.zhangjinghao.mobilesafemanager.appmanager.entity.AppInfo;
import com.sysu.zhangjinghao.mobilesafemanager.appmanager.utils.AppInfoParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjinghao on 16/6/9.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    TextView tvPhoneMemory;
    TextView tvSDMemory;
    TextView tvAppNum;
    ListView lvAppManager;

    private AppManagerAdapter adapter;
    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;

    private UninstallReceiver receiver;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 10:
                    adapter = new AppManagerAdapter(userAppInfos, systemAppInfos, AppManagerActivity.this);
                    lvAppManager.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                case 15:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_manager);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        ImageView ivLeft = (ImageView)findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);
        tvPhoneMemory = (TextView)findViewById(R.id.tv_phone_memory_app_manager);
        tvSDMemory = (TextView)findViewById(R.id.tv_sd_memory_app_manager);
        tvAppNum = (TextView)findViewById(R.id.tv_app_number);
        lvAppManager = (ListView)findViewById(R.id.lv_app_manager);

        receiver = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver, intentFilter);

        //获取手机和SD卡剩余内存
        getMemoryFromPhone();
        //初始化数据
        initData();

        lvAppManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (adapter != null) {
                    new Thread() {
                        @Override
                        public void run() {
                            AppInfo currentInfo = (AppInfo) adapter.getItem(position);
                            boolean flag = currentInfo.isSelected;
                            for (AppInfo info : userAppInfos) {
                                info.isSelected = false;
                            }
                            for (AppInfo info : systemAppInfos) {
                                info.isSelected = false;
                            }
                            if (currentInfo != null) {
                                if (flag) {
                                    currentInfo.isSelected = false;
                                } else {
                                    currentInfo.isSelected = true;
                                }
                                mHandler.sendEmptyMessage(15);
                            }
                        }
                    }.start();
                }
            }
        });
        lvAppManager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem >= userAppInfos.size()+1) {
                    tvAppNum.setText("系统程序："+systemAppInfos.size()+"个");
                } else {
                    tvAppNum.setText("用户程序："+userAppInfos.size()+"个");
                }
            }
        });
    }

    private void getMemoryFromPhone() {
        long avail_sd = Environment.getExternalStorageDirectory().getFreeSpace();
        long avail_rom = Environment.getDataDirectory().getFreeSpace();
        String str_avail_sd = Formatter.formatFileSize(this, avail_sd);
        String str_avail_rom = Formatter.formatFileSize(this, avail_rom);
        tvPhoneMemory.setText("剩余手机内存：" + str_avail_rom);
        tvSDMemory.setText("剩余SD卡内存：" + str_avail_sd);
    }

    private void initData() {
        appInfos = new ArrayList<AppInfo>();
        userAppInfos = new ArrayList<AppInfo>();
        systemAppInfos = new ArrayList<AppInfo>();
        new Thread() {
            @Override
            public void run() {
                appInfos.clear();
                userAppInfos.clear();
                systemAppInfos.clear();
                appInfos.addAll(AppInfoParser.getAppInfos(AppManagerActivity.this));
                for(AppInfo info : appInfos) {
                    if(info.isUserApp) {
                        userAppInfos.add(info);
                    } else {
                        systemAppInfos.add(info);
                    }
                }
                mHandler.sendEmptyMessage(10);
            }
        }.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_button:
                finish();
                break;
        }
    }

    class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
