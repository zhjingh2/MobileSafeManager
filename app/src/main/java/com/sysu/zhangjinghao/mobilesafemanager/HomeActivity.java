package com.sysu.zhangjinghao.mobilesafemanager;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.antitheft.LostFindActivity;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.dialog.InputPasswordDialog;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.dialog.SetupPasswordDialog;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.receiver.MyDeviceAdminReceiver;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.service.GPSLocationService;
import com.sysu.zhangjinghao.mobilesafemanager.antitheft.utils.MD5Utils;
import com.sysu.zhangjinghao.mobilesafemanager.appmanager.AppManagerActivity;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.SecurityPhoneActivity;
import com.sysu.zhangjinghao.mobilesafemanager.cacheclean.CacheClearListActivity;
import com.sysu.zhangjinghao.mobilesafemanager.home.adapter.HomeAdapter;
import com.sysu.zhangjinghao.mobilesafemanager.virus.VirusScanActivity;

public class HomeActivity extends Activity {
    private GridView gvHome;
    private long mExitTimes = 0;

    private SharedPreferences mSharedPreferences;

    //超级管理员
    private DevicePolicyManager devicePolicyManager;
    //申请权限
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        gvHome = (GridView)findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter(this));
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (hasSetupPassword()) {
                            //弹出输入密码对话框
                            showInputPasswordDialog();
                        } else {
                            //弹出设置密码对话框
                            showSetupPasswordDialog();
                        }
                        break;
                    case 1:
                        startActivity(SecurityPhoneActivity.class);
                        break;
                    case 2:
                        startActivity(AppManagerActivity.class);
                        break;
                    case 3:
                        startActivity(VirusScanActivity.class);
                        break;
                    case 4:
                        startActivity(CacheClearListActivity.class);
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    default:
                        break;
                }
            }
        });

        //获取超级管理员
        devicePolicyManager = (DevicePolicyManager)this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //申请权限
        componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
        //如果没有权限则申请权限
        boolean isActive = devicePolicyManager.isAdminActive(componentName);
        if(!isActive) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "获取超级管理员权限，用于远程锁屏和清除数据");
            startActivity(intent);
        }

    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(HomeActivity.this, cls);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(System.currentTimeMillis()-mExitTimes < 2000) {
                System.exit(0);
            } else {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
                mExitTimes = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean hasSetupPassword() {
        String password = mSharedPreferences.getString("PhoneAntiTheftPWD", null);
        if(!TextUtils.isEmpty(password)) {
            return true;
        }
        return false;
    }

    private void showInputPasswordDialog() {
        String password = mSharedPreferences.getString("PhoneAntiTheftPWD", null);
        if(TextUtils.isEmpty(password)) {
            password = "";
        }

        final InputPasswordDialog inputPasswordDialog = new InputPasswordDialog(HomeActivity.this);
        inputPasswordDialog.setCancelable(true);
        final String finalPassword = password;
        inputPasswordDialog.setMyCallBack(new InputPasswordDialog.MyCallBack() {
            @Override
            public void confirm() {
                String inputPassword = inputPasswordDialog.etInputPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(inputPassword)) {
                    if(finalPassword.equals(MD5Utils.encode(inputPassword))) {
                        inputPasswordDialog.dismiss();
                        startActivity(LostFindActivity.class);
                    } else {
                        Toast.makeText(HomeActivity.this, "密码有误，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancel() {
                inputPasswordDialog.dismiss();
            }
        });
        inputPasswordDialog.show();
    }

    private void showSetupPasswordDialog() {
        final SetupPasswordDialog setupPasswordDialog = new SetupPasswordDialog(HomeActivity.this);
        setupPasswordDialog.setCancelable(true);
        setupPasswordDialog.setMyCallBack(new SetupPasswordDialog.MyCallBack() {
            @Override
            public void ok() {
                String firstPassword = setupPasswordDialog.etFirstPassword.getText().toString().trim();
                String affirmPassword = setupPasswordDialog.etAffirmPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(firstPassword) && !TextUtils.isEmpty(affirmPassword)) {
                    if (firstPassword.equals(affirmPassword)) {
                        //存储密码
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("PhoneAntiTheftPWD", MD5Utils.encode(affirmPassword));
                        editor.commit();

                        setupPasswordDialog.dismiss();
                        showInputPasswordDialog();
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancel() {
                setupPasswordDialog.dismiss();
            }
        });
        setupPasswordDialog.show();
    }
}
