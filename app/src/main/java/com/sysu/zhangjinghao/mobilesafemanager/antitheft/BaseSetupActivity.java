package com.sysu.zhangjinghao.mobilesafemanager.antitheft;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;

/**
 * Created by zhangjinghao on 16/5/28.
 */
public abstract class BaseSetupActivity extends Activity {
    public SharedPreferences sharedPreferences;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(Math.abs(velocityX) < 200) { // 无效动作过滤
                    Toast.makeText(getApplicationContext(), "无效动作，移动太慢", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(e2.getRawX() - e1.getRawX() > 200) {
                    showPre();
                    overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
                    return true;
                }
                if(e1.getRawX() - e2.getRawX() > 200) {
                    showNext();
                    overridePendingTransition(R.anim.next_in, R.anim.next_out);
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    //模版方法模式，将实现放到子类
    public abstract void showPre();
    public abstract void showNext();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void startActivityAndFinishSelf(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }
}
