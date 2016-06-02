package com.sysu.zhangjinghao.mobilesafemanager.antitheft.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.entity.ContactInfo;

import java.util.List;

/**
 * Created by zhangjinghao on 16/5/30.
 */
public class ContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<ContactInfo> contactInfos;

    public ContactAdapter(Context mContext, List<ContactInfo> contactInfos) {
        this.mContext = mContext;
        this.contactInfos = contactInfos;
    }

    @Override
    public int getCount() {
        return contactInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return contactInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_list_contact_select, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            viewHolder.tvPhone = (TextView)convertView.findViewById(R.id.tv_phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvName.setText(contactInfos.get(position).name);
        viewHolder.tvPhone.setText(contactInfos.get(position).phone);
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvPhone;
    }
}
