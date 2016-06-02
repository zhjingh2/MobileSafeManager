package com.sysu.zhangjinghao.mobilesafemanager.home.utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * Created by zhangjinghao on 16/5/21.
 */
public class DownloadUtils {

    public void downapk(String url, String targetFile, final MyCallBack myCallBack) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, targetFile, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                myCallBack.onSuccess(responseInfo);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                myCallBack.onFailure(e, s);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                myCallBack.onLoading(total, current, isUploading);
            }
        });
    }

    interface  MyCallBack {
        //下载成功时调用
        void onSuccess(ResponseInfo<File> arg0);
        //下载失败时调用
        void onFailure(HttpException arg0, String arg1);
        //下载中调用
        void onLoading(long total, long current, boolean isUploading);
    }
}
