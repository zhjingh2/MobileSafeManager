package com.sysu.zhangjinghao.mobilesafemanager.antitheft.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sysu.zhangjinghao.mobilesafemanager.R;

/**
 * Created by zhangjinghao on 16/5/26.
 */
public class InputPasswordDialog extends Dialog implements View.OnClickListener {
    private TextView tvTitle;
    public EditText etInputPassword;
    private Button btnConfirm;
    private Button btnCancel;

    private MyCallBack myCallBack;

    public void setMyCallBack(MyCallBack myCallBack) {
        this.myCallBack = myCallBack;
    }

    public InputPasswordDialog(Context context) {
        super(context, R.style.dialog_custom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_password_dialog);

        tvTitle = (TextView) findViewById(R.id.tv_input_password_title);
        etInputPassword = (EditText) findViewById(R.id.et_input_password);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnCancel = (Button) findViewById(R.id.btn_dismiss);

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                myCallBack.confirm();
                break;
            case R.id.btn_dismiss:
                myCallBack.cancel();
                break;
        }
    }

    public interface MyCallBack{
        void confirm();
        void cancel();
    }
}
