package com.sysu.zhangjinghao.mobilesafemanager.appmanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.appmanager.entity.AppInfo;
import com.sysu.zhangjinghao.mobilesafemanager.appmanager.utils.DensityUtils;
import com.sysu.zhangjinghao.mobilesafemanager.appmanager.utils.EngineUtils;

import java.util.Formatter;
import java.util.List;

/**
 * Created by zhangjinghao on 16/6/10.
 */
public class AppManagerAdapter extends BaseAdapter {

    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private Context mContext;

    public AppManagerAdapter(List<AppInfo> userAppInfos, List<AppInfo> systemAppInfos, Context mContext) {
        this.userAppInfos = userAppInfos;
        this.systemAppInfos = systemAppInfos;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return userAppInfos.size()+systemAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        if(position == 0) { //位置0应该显示的是用户程序的个数
            return null;
        } else if(position == userAppInfos.size()+1) {
            return null;
        }
        AppInfo appInfo;
        if(position < userAppInfos.size()+1) { //用户程序
            appInfo = userAppInfos.get(position-1);
        } else { //系统程序
            int location = position - userAppInfos.size() - 2;
            appInfo = systemAppInfos.get(location);
        }
        return appInfo;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0) {
            TextView textView = new TextView(mContext);
            textView.setBackgroundColor(mContext.getResources().getColor(android.R.color.darker_gray));
            textView.setPadding(DensityUtils.dip2px(mContext, 5), DensityUtils.dip2px(mContext, 5),
                    DensityUtils.dip2px(mContext, 5), DensityUtils.dip2px(mContext, 5));
            textView.setTextColor(mContext.getResources().getColor(android.R.color.black));
            textView.setText("用户程序："+userAppInfos.size()+"个");
            return textView;
        } else if(position == userAppInfos.size()+1) {
            TextView textView = new TextView(mContext);
            textView.setBackgroundColor(mContext.getResources().getColor(android.R.color.darker_gray));
            textView.setPadding(DensityUtils.dip2px(mContext, 5), DensityUtils.dip2px(mContext, 5),
                    DensityUtils.dip2px(mContext, 5), DensityUtils.dip2px(mContext, 5));
            textView.setTextColor(mContext.getResources().getColor(android.R.color.black));
            textView.setText("系统程序："+systemAppInfos.size()+"个");
            return textView;
        }

        AppInfo appInfo;
        if(position < userAppInfos.size()+1) {
            appInfo = userAppInfos.get(position-1);
        } else {
            appInfo = systemAppInfos.get(position-userAppInfos.size()-2);
        }

        ViewHolder viewHolder;
        if(convertView != null && convertView instanceof LinearLayout) {
            viewHolder = (ViewHolder)convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_list_app_manager, null);
            viewHolder.ivAppIcon = (ImageView)convertView.findViewById(R.id.iv_app_icon);
            viewHolder.tvAppLocation = (TextView)convertView.findViewById(R.id.tv_app_place);
            viewHolder.tvAppSize = (TextView)convertView.findViewById(R.id.tv_app_size);
            viewHolder.tvAppName = (TextView)convertView.findViewById(R.id.tv_app_name);
            viewHolder.tvLaunchApp = (TextView)convertView.findViewById(R.id.tv_launch_app);
            viewHolder.tvSetting = (TextView)convertView.findViewById(R.id.tv_setting_app);
            viewHolder.tvShareApp = (TextView)convertView.findViewById(R.id.tv_share_app);
            viewHolder.tvUninstall = (TextView)convertView.findViewById(R.id.tv_uninstall_app);
            viewHolder.llAppOption = (LinearLayout)convertView.findViewById(R.id.ll_option_app);
            convertView.setTag(viewHolder);
        }

        if(appInfo != null) {
            viewHolder.tvAppLocation.setText(appInfo.getAppLocation(appInfo.isInRom));
            viewHolder.ivAppIcon.setImageDrawable(appInfo.icon);
            viewHolder.tvAppSize.setText(android.text.format.Formatter.formatFileSize(mContext, appInfo.appSize));
            viewHolder.tvAppName.setText(appInfo.appName);
            if(appInfo.isSelected) {
                viewHolder.llAppOption.setVisibility(View.VISIBLE);
            } else {
                viewHolder.llAppOption.setVisibility(View.GONE);
            }
        }

        MyClickListener myClickListener = new MyClickListener(appInfo);
        viewHolder.tvLaunchApp.setOnClickListener(myClickListener);
        viewHolder.tvSetting.setOnClickListener(myClickListener);
        viewHolder.tvShareApp.setOnClickListener(myClickListener);
        viewHolder.tvUninstall.setOnClickListener(myClickListener);

        return convertView;
    }

    static class ViewHolder {
        TextView tvLaunchApp;
        TextView tvShareApp;
        TextView tvUninstall;
        TextView tvSetting;
        ImageView ivAppIcon;
        TextView tvAppLocation;
        TextView tvAppSize;
        TextView tvAppName;
        LinearLayout llAppOption;
    }

    class MyClickListener implements View.OnClickListener {

        private AppInfo appInfo;

        public MyClickListener(AppInfo appInfo) {
            this.appInfo = appInfo;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_launch_app:
                    //启动应用
                    EngineUtils.startApplication(mContext, appInfo);
                    break;
                case R.id.tv_share_app:
                    //分享应用
                    EngineUtils.shareApplication(mContext, appInfo);
                    break;
                case R.id.tv_setting_app:
                    //设置应用
                    EngineUtils.settingAppDetail(mContext, appInfo);
                    break;
                case R.id.tv_uninstall_app:
                    //卸载应用
                    if(appInfo.packageName.equals(mContext.getPackageName())) {
                        Toast.makeText(mContext, "您无权卸载此应用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    EngineUtils.uninstallApplication(mContext, appInfo);
                    break;
            }
        }
    }
}
