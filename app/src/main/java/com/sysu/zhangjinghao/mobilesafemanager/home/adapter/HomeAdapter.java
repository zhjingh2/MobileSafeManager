package com.sysu.zhangjinghao.mobilesafemanager.home.adapter;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;

import org.w3c.dom.Text;

/**
 * Created by zhangjinghao on 16/5/20.
 */
public class HomeAdapter extends BaseAdapter {
    private int[] imageId = {
            R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.trojan, R.drawable.sysoptimize, R.drawable.taskmanager,
            R.drawable.netmanager, R.drawable.atools, R.drawable.settings
    };
    private String[] names = {
            "手机防盗", "通讯卫士", "软件管家",
            "手机杀毒", "缓存清理", "进程管理",
            "流量统计", "高级工具", "设置中心"
    };
    private Context mContext;

    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_home, null);
        ImageView ivIcon  = (ImageView)view.findViewById(R.id.iv_icon);
        TextView tvName = (TextView)view.findViewById(R.id.tv_name);
        ivIcon.setImageResource(imageId[position]);
        tvName.setText(names[position]);
        return view;
    }
}
