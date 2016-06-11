package com.sysu.zhangjinghao.mobilesafemanager.appmanager.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.sysu.zhangjinghao.mobilesafemanager.appmanager.entity.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjinghao on 16/6/9.
 */
public class AppInfoParser {
    //获取手机里面的所有的应用程序
    public static List<AppInfo> getAppInfos(Context context) {
        //得到一个包管理器
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<AppInfo> list = new ArrayList<AppInfo>();
        for(PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            appInfo.packageName = packageInfo.packageName;
            appInfo.icon = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.apkPath = packageInfo.applicationInfo.sourceDir;
            File file = new File(appInfo.apkPath);
            appInfo.appSize = file.length();

            int flags = packageInfo.applicationInfo.flags;//二进制映射
            if((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0) {
                //外部存储
                appInfo.isInRom = false;
            } else {
                //手机内存
                appInfo.isInRom = true;
            }
            if((ApplicationInfo.FLAG_SYSTEM & flags) != 0) {
                //系统应用
                appInfo.isUserApp = false;
            } else {
                //用户应用
                appInfo.isUserApp = true;
            }

            list.add(appInfo);
        }
        return list;
    }
}
