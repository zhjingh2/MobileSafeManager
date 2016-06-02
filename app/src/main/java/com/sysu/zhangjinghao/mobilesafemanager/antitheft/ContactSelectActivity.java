package com.sysu.zhangjinghao.mobilesafemanager.antitheft;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.adapter.ContactAdapter;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.entity.ContactInfo;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.utils.ContactInfoParser;

import java.util.List;

/**
 * Created by zhangjinghao on 16/5/29.
 */
public class ContactSelectActivity extends Activity implements View.OnClickListener {
    private ListView mListView;
    private List<ContactInfo> contactInfoList;

    private ContactAdapter contactAdapter;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    if(contactInfoList != null) {
                        contactAdapter = new ContactAdapter(ContactSelectActivity.this, contactInfoList);
                        mListView.setAdapter(contactAdapter);
                    }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_select);
        ((TextView)findViewById(R.id.tv_title)).setText("选择联系人");
        ImageView ivLeft = (ImageView)findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
        mListView = (ListView)findViewById(R.id.lv_contact);

        new Thread() {
            @Override
            public void run() {
                contactInfoList = ContactInfoParser.getSystemContactInfos(ContactSelectActivity.this);//获取系统通讯录
                contactInfoList.addAll(ContactInfoParser.getSimContactInfos(ContactSelectActivity.this));//获取SIM卡通讯录
                mHandler.sendEmptyMessage(10);
            }
        }.start();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactInfo item = (ContactInfo)contactAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("phone", item.phone);
                intent.putExtra("name", item.name);
                setResult(0, intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_button:
                finish();
                break;
        }
    }
}
