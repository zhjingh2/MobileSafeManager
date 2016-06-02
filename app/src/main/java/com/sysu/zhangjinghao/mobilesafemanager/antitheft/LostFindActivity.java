package com.sysu.zhangjinghao.mobilesafemanager.antitheft;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sysu.zhangjinghao.mobilesafemanager.R;

/**
 * Created by zhangjinghao on 16/5/27.
 */
public class LostFindActivity extends Activity implements View.OnClickListener {

    private SharedPreferences mSharedPreferences;
    private TextView tvProtectingStatus;
    private ToggleButton toggleButton;
    private RelativeLayout rlGotoWizard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lostfind);

        //判断是否走过向导
        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        if(!mSharedPreferences.getBoolean("hasSetup", false)) {
            Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }

        TextView tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText("手机防盗");
        ImageView ivLeft = (ImageView)findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
        ((TextView)findViewById(R.id.tv_safe_phone)).setText(mSharedPreferences.getString("safeNumber", ""));
        toggleButton = (ToggleButton)findViewById(R.id.togglebtn_lostfind);
        tvProtectingStatus = (TextView)findViewById(R.id.tv_lostfind_protect_status);
        rlGotoWizard = (RelativeLayout)findViewById(R.id.rl_goto_setup_wizard);
        rlGotoWizard.setOnClickListener(this);
        boolean isProtecting = mSharedPreferences.getBoolean("isProtecting", true);
        if(isProtecting) {
            tvProtectingStatus.setText("防盗保护已经开启");
            toggleButton.setChecked(true);
        } else {
            tvProtectingStatus.setText("防盗保护没有开启");
            toggleButton.setChecked(false);
        }
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tvProtectingStatus.setText("防盗保护已经开启");
                } else {
                    tvProtectingStatus.setText("防盗保护没有开启");
                }
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("isProtecting", isChecked);
                editor.commit();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_goto_setup_wizard:
                Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_left_button:
                finish();
                break;
        }
    }
}
