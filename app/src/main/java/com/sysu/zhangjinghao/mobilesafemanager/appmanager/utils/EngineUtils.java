package com.sysu.zhangjinghao.mobilesafemanager.appmanager.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;
import com.sysu.zhangjinghao.mobilesafemanager.appmanager.entity.AppInfo;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zhangjinghao on 16/6/9.
 * 业务工具类
 */
public class EngineUtils {
    //分享应用
    public static void shareApplication(Context context, AppInfo appInfo) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件，名字叫做："+appInfo.appName+"下载路径：https://xxx/details?id=?"+appInfo.packageName);
        context.startActivity(intent);
    }

    //开启应用程序
    public static void startApplication(Context context, AppInfo appInfo) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(appInfo.packageName);
        if(intent != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "该应用没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }

    //开启应用设置页面
    public static void settingAppDetail(Context context, AppInfo appInfo) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:"+appInfo.packageName));
        context.startActivity(intent);
    }

    //卸载应用
    public static void uninstallApplication(Context context, AppInfo appInfo) {
        if(appInfo.isUserApp) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + appInfo.packageName));
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "暂时不提供此功能", Toast.LENGTH_SHORT).show();
        }
    }
}
