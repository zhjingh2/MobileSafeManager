package com.sysu.zhangjinghao.mobilesafemanager.antitheft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;

/**
 * Created by zhangjinghao on 16/5/28.
 */
public class Setup2Activity extends BaseSetupActivity implements View.OnClickListener{
    private TelephonyManager mTelephonyManager;
    private Button btnBindSIM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        ((RadioButton)findViewById(R.id.rb_second)).setChecked(true);
        mTelephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        btnBindSIM = (Button)findViewById(R.id.btn_bind_sim);
        btnBindSIM.setOnClickListener(this);

        if(isBind()) {
            btnBindSIM.setEnabled(false);
        } else {
            btnBindSIM.setEnabled(true);
        }
    }

    private boolean isBind() {
        String simStr = sharedPreferences.getString("sim", null);
        if(TextUtils.isEmpty(simStr)) {
            return false;
        }
        return true;
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup1Activity.class);
    }

    @Override
    public void showNext() {
        if(!isBind()) {
            Toast.makeText(this, "您还没绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivityAndFinishSelf(Setup3Activity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_sim:
                bindSIM();
                break;
        }
    }

    private void bindSIM() {
        if(!isBind()) {
            String simSerialNumber = mTelephonyManager.getSimSerialNumber();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("sim", simSerialNumber);
            editor.commit();
            Toast.makeText(this, "SIM卡绑定成功", Toast.LENGTH_SHORT).show();
            btnBindSIM.setEnabled(false);
        } else {
            Toast.makeText(this, "SIM卡已经绑定", Toast.LENGTH_SHORT).show();
            btnBindSIM.setEnabled(false);
        }
    }
}
