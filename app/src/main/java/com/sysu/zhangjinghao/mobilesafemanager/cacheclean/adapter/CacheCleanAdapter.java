package com.sysu.zhangjinghao.mobilesafemanager.cacheclean.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.cacheclean.entity.CacheInfo;

import java.util.List;

/**
 * Created by zhangjinghao on 16/6/12.
 */
public class CacheCleanAdapter extends BaseAdapter {

    private List<CacheInfo> data;
    private Context mContext;

    public CacheCleanAdapter(List<CacheInfo> data, Context mContext) {
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
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_list_cache_clean, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon_cache_clean);
            holder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name_cache_clean);
            holder.tvCacheSize = (TextView) convertView.findViewById(R.id.tv_app_size_cache_clean);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        CacheInfo cacheInfo = data.get(position);
        holder.ivIcon.setImageDrawable(cacheInfo.appIcon);
        holder.tvAppName.setText(cacheInfo.appName);
        holder.tvCacheSize.setText(Formatter.formatFileSize(mContext, cacheInfo.cacheSize));
        return convertView;
    }

    static class ViewHolder{
        ImageView ivIcon;
        TextView  tvAppName;
        TextView tvCacheSize;
    }
}
