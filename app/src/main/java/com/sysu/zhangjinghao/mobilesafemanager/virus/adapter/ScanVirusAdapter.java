package com.sysu.zhangjinghao.mobilesafemanager.virus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.virus.entity.ScanAppInfo;

import java.util.List;

/**
 * Created by zhangjinghao on 16/6/11.
 */
public class ScanVirusAdapter extends BaseAdapter {
    private List<ScanAppInfo> data;
    private Context mContext;

    public ScanVirusAdapter(List<ScanAppInfo> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_list_applock, null);
            viewHolder = new ViewHolder();
            viewHolder.ivAppIcon = (ImageView)convertView.findViewById(R.id.iv_app_icon);
            viewHolder.tvAppName = (TextView)convertView.findViewById(R.id.tv_app_name);
            viewHolder.ivScanIcon = (ImageView)convertView.findViewById(R.id.iv_lock);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ScanAppInfo scanAppInfo = data.get(position);
        if(!scanAppInfo.isVirus){
            viewHolder.ivScanIcon.setBackgroundResource(R.drawable.blue_right_icon);
            viewHolder.tvAppName.setTextColor(mContext.getResources().getColor(android.R.color.black));
            viewHolder.tvAppName.setText(scanAppInfo.appName);
        }else{
            viewHolder.tvAppName.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            viewHolder.tvAppName.setText(scanAppInfo.appName+"("+scanAppInfo.desc+")");
        }
        viewHolder.ivAppIcon.setImageDrawable(scanAppInfo.appIcon);
        return convertView;
    }

    static class ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName;
        ImageView ivScanIcon;
    }
}
