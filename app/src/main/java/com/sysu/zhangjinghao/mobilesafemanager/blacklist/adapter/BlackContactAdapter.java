package com.sysu.zhangjinghao.mobilesafemanager.blacklist.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.db.dao.BlackNumberDao;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.entity.BlackContactInfo;

import java.util.List;

/**
 * Created by zhangjinghao on 16/6/1.
 */
public class BlackContactAdapter extends BaseAdapter {
    private List<BlackContactInfo> data;
    private Context mContext;
    private BlackNumberDao dao;
    private BlackContactCallBack blackContactCallBack;

    public void setBlackContactCallBack(BlackContactCallBack blackContactCallBack) {
        this.blackContactCallBack = blackContactCallBack;
    }

    public BlackContactAdapter(List<BlackContactInfo> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        dao = new BlackNumberDao(mContext);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_list_blackcontact, null);
            viewHolder = new ViewHolder();
            viewHolder.tvMode = (TextView)convertView.findViewById(R.id.tv_black_mode);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_black_name);
            viewHolder.vContact = (View)convertView.findViewById(R.id.view_black_icon);
            viewHolder.vDelete = (View)convertView.findViewById(R.id.view_black_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvName.setText(data.get(position).name+"("+data.get(position).number+")");
        viewHolder.tvMode.setText(data.get(position).getModeString(data.get(position).mode));
        viewHolder.tvName.setTextColor(mContext.getResources().getColor(android.R.color.holo_purple));
        viewHolder.tvMode.setTextColor(mContext.getResources().getColor(android.R.color.holo_purple));
        viewHolder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDelete = dao.delete(data.get(position));
                if(isDelete) {
                    data.remove(data.get(position));
                    notifyDataSetChanged();
                    blackContactCallBack.dataSizeReduced();
                } else {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvMode;
        View vContact;
        View vDelete;
    }

    public interface BlackContactCallBack {
        void dataSizeReduced();
    }
}
