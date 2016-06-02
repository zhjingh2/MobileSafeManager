package com.sysu.zhangjinghao.mobilesafemanager.blacklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.ContactSelectActivity;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.db.dao.BlackNumberDao;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.entity.BlackContactInfo;

/**
 * Created by zhangjinghao on 16/6/1.
 */
public class AddBlackNumberActivity extends Activity implements View.OnClickListener {

    private CheckBox cbSms;
    private CheckBox cbTel;
    private EditText etNum;
    private EditText etName;
    private BlackNumberDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_black_number);
        dao = new BlackNumberDao(this);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
        ((TextView)findViewById(R.id.tv_title)).setText("添加黑名单");
        ImageView ivLeft = (ImageView)findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);
        cbSms = (CheckBox)findViewById(R.id.cb_black_number_sms);
        cbTel = (CheckBox)findViewById(R.id.cb_black_number_tel);
        etNum = (EditText)findViewById(R.id.et_black_number);
        etName = (EditText)findViewById(R.id.et_black_name);
        findViewById(R.id.btn_add_black_num).setOnClickListener(this);
        findViewById(R.id.btn_add_from_contact).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_button:
                finish();
                break;
            case R.id.btn_add_black_num:
                String strNumber = etNum.getText().toString().trim();
                String strName = etName.getText().toString().trim();
                if(TextUtils.isEmpty(strName) || TextUtils.isEmpty(strNumber)) {
                    Toast.makeText(this, "电话号码和手机号不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    BlackContactInfo temp = new BlackContactInfo();
                    temp.number = strNumber;
                    temp.name = strName;
                    if(cbSms.isChecked() && cbTel.isChecked()) {
                        temp.mode = 3;
                    } else if(cbSms.isChecked() && !cbTel.isChecked()) {
                        temp.mode = 2;
                    } else if(!cbSms.isChecked() && cbTel.isChecked()) {
                        temp.mode = 1;
                    } else {
                        Toast.makeText(this, "请选择拦截模式", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!dao.isNumberExist(temp.number)) {
                        dao.add(temp);
                    } else {
                        Toast.makeText(this, "该号码已经被添加至黑名单", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                break;
            case R.id.btn_add_from_contact:
                startActivityForResult(new Intent(this, ContactSelectActivity.class), 0);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            String phone = data.getStringExtra("phone");
            String name = data.getStringExtra("name");
            etName.setText(name);
            etNum.setText(phone);
        }
    }
}
