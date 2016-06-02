package com.sysu.zhangjinghao.mobilesafemanager.antitheft.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;

/**
 * Created by zhangjinghao on 16/5/26.
 */
public class SetupPasswordDialog extends Dialog implements View.OnClickListener {
    private TextView tvTitle;
    public EditText etFirstPassword;
    public EditText etAffirmPassword;

    private MyCallBack myCallBack;

    public void setMyCallBack(MyCallBack myCallBack) {
        this.myCallBack = myCallBack;
    }

    public SetupPasswordDialog(Context context) {
        super(context, R.style.dialog_custom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_password_dialog);

        tvTitle = (TextView)findViewById(R.id.tv_setup_password_title);
        etFirstPassword = (EditText)findViewById(R.id.et_first_password);
        etAffirmPassword = (EditText)findViewById(R.id.et_affirm_password);
        
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                myCallBack.cancel();
                break;
            case R.id.btn_ok:
                myCallBack.ok();
                break;
        }
    }



    public interface MyCallBack {
        void ok();
        void cancel();
    }
}
