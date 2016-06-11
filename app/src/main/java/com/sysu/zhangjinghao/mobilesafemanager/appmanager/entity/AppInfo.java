package com.sysu.zhangjinghao.mobilesafemanager.appmanager.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by zhangjinghao on 16/6/9.
 */
public class AppInfo {

    public String packageName;

    public Drawable icon;

    public String appName;

    public String apkPath;

    public long appSize;

    public boolean isInRom;

    public boolean isUserApp;

    public boolean isSelected = false;

    public String getAppLocation(boolean isInRom) {
        if (isInRom) {
            return "手机内存";
        } else {
            return "外部存储";
        }
    }

}

