package com.sysu.zhangjinghao.mobilesafemanager.home.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import com.sysu.zhangjinghao.mobilesafemanager.HomeActivity;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.home.entity.VersionEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangjinghao on 16/5/21.
 */
public class VersionUpdateUtils {

    private static final int MESSAGE_NET_ERROR = 101;
    private static final int MESSAGE_IO_ERROR = 102;
    private static final int MESSAGE_JSON_ERROR = 103;
    private static final int MESSAGE_SHOWN_DIALOG = 104;
    private static final int MESSAGE_ENTERHOME = 105;

    private Activity mContext;
    private VersionEntity versionEntity;
    private String mVersion;
    private ProgressDialog mProgressDialog;

    //构造函数
    public VersionUpdateUtils(String mVersion, Activity mContext) {
        this.mVersion = mVersion;
        this.mContext = mContext;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_IO_ERROR:
                    Toast.makeText(mContext, "IO异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_ERROR:
                    Toast.makeText(mContext, "JSON解析异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_NET_ERROR:
                    Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_SHOWN_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                    mContext.finish();
                    break;
            }
        }
    };

    public void getCloudVersion() {
        try {
            HttpClient client = new DefaultHttpClient();
            //连接超时
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
            //请求超时
            HttpConnectionParams.setSoTimeout(client.getParams(), 5000);

            HttpGet httpGet = new HttpGet("http://192.168.1.31:8080/updateinfo.html");
            HttpResponse execute = client.execute(httpGet);


            if(execute.getStatusLine().getStatusCode() == 200) { //请求和响应都成功了
                HttpEntity entity = execute.getEntity();
                String result = EntityUtils.toString(entity, "gbk");

                //创建JSONObject对象
                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                versionEntity.versionCode = jsonObject.getString("code");
                versionEntity.description = jsonObject.getString("des");
                versionEntity.apkUrl = jsonObject.getString("apkurl");

                if(!mVersion.equals(versionEntity.versionCode)) { //版本号不一致
                    handler.sendEmptyMessage(MESSAGE_SHOWN_DIALOG);
                }
            }

        } catch (ClientProtocolException e) {
            handler.sendEmptyMessage(MESSAGE_NET_ERROR);
            e.printStackTrace();
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_ENTERHOME);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_ERROR);
            e.printStackTrace();
        }

    }

    //弹出更新提示对话框
    private void showUpdateDialog(final VersionEntity versionEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("检查到新版本：" + versionEntity.versionCode);
        builder.setMessage(versionEntity.description);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initProgressDialog();
                downloadNewApk(versionEntity.apkUrl);
            }
        });
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });
        builder.show();
    }

    //初始化进度条对话框
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("准备下载");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    //下载新版本
    protected void downloadNewApk(String apkurl) {
        DownloadUtils downloadUtils = new DownloadUtils();
        downloadUtils.downapk(apkurl, "/mnt/sdcard/mobilesafe2.0.apk", new DownloadUtils.MyCallBack() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                mProgressDialog.dismiss();
                MyUtils.installApk(mContext);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                mProgressDialog.setMessage("下载失败");
                mProgressDialog.dismiss();
                enterHome();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                mProgressDialog.setMax((int) total);
                mProgressDialog.setMessage("下载中");
                mProgressDialog.setProgress((int) current);
            }
        });
    }

    private void enterHome() {
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME, 2000);
    }
}
