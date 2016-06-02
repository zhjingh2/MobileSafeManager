package com.sysu.zhangjinghao.mobilesafemanager.antitheft;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sysu.zhangjinghao.mobilesafemanager.R;

/**
 * Created by zhangjinghao on 16/5/29.
 */
public class Setup4Activity extends BaseSetupActivity {
    private TextView tvStatus;
    private ToggleButton toggleBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        ((RadioButton)findViewById(R.id.rb_fourth)).setChecked(true);
        tvStatus = (TextView)findViewById(R.id.tv_setup_status);
        toggleBtn = (ToggleButton)findViewById(R.id.togglebtn_security_function);
        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tvStatus.setText("防盗保护已经开启");
                } else {
                    tvStatus.setText("防盗保护没有开启");
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isProtecting", isChecked);
                editor.commit();
            }
        });
        boolean isProtecting = sharedPreferences.getBoolean("isProtecting", true);
        if(isProtecting) {
            tvStatus.setText("防盗保护已经开启");
            toggleBtn.setChecked(true);
        } else {
            tvStatus.setText("防盗保护没有开启");
            toggleBtn.setChecked(false);
        }
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup3Activity.class);
    }

    @Override
    public void showNext() {
        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.putBoolean("hasSetup", true); //标注为已设置状态
        editor.commit();

        startActivityAndFinishSelf(LostFindActivity.class);
    }
}
