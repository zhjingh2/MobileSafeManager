package com.sysu.zhangjinghao.mobilesafemanager.appmanager.utils;

import android.content.Context;

/**
 * Created by zhangjinghao on 16/6/9.
 */
public class DensityUtils {
    //dip转换为px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    //px转换为dip
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }
}
