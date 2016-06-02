package com.sysu.zhangjinghao.mobilesafemanager.antitheft;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;

/**
 * Created by zhangjinghao on 16/5/28.
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
        ((RadioButton)findViewById(R.id.rb_first)).setChecked(true);
    }

    @Override
    public void showPre() {
        Toast.makeText(this, "当前页面已经是第一页", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNext() {
        startActivityAndFinishSelf(Setup2Activity.class);
    }
}
