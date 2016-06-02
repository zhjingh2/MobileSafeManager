package com.sysu.zhangjinghao.mobilesafemanager.antitheft;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.entity.ContactInfo;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.utils.ContactInfoParser;

/**
 * Created by zhangjinghao on 16/5/29.
 */
public class Setup3Activity extends BaseSetupActivity implements View.OnClickListener{
    private EditText mInputPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        ((RadioButton)findViewById(R.id.rb_third)).setChecked(true);
        findViewById(R.id.btn_add_contact).setOnClickListener(this);
        mInputPhone = (EditText)findViewById(R.id.et_input_phone);
        String safeNumber = sharedPreferences.getString("safeNumber", null);
        if(!TextUtils.isEmpty(safeNumber)) {
            mInputPhone.setText(safeNumber);
        }
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup2Activity.class);
    }

    @Override
    public void showNext() {
        String safeNumber = mInputPhone.getText().toString().trim();
        if(TextUtils.isEmpty(safeNumber)) {
            Toast.makeText(this, "请输入安全号码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("safeNumber", safeNumber);
            editor.commit();
            startActivityAndFinishSelf(Setup4Activity.class);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_contact:
                startActivityForResult(new Intent(this, ContactSelectActivity.class), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            String phone = data.getStringExtra("phone");
            mInputPhone.setText(phone);
        }
    }
}
