package com.sysu.zhangjinghao.mobilesafemanager.blacklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.zhangjinghao.mobilesafemanager.R;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.adapter.BlackContactAdapter;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.db.dao.BlackNumberDao;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.entity.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjinghao on 16/6/1.
 */
public class SecurityPhoneActivity extends Activity implements View.OnClickListener{

    private FrameLayout flHaveBlackNumber;
    private FrameLayout flNoBlackNumber;
    private ListView lvBlackNumbers;

    private List<BlackContactInfo> pageBlackNumberList = new ArrayList<BlackContactInfo>();
    private int currentCount = 0;
    private final int pageSize = 15;
    private int totalNumber;
    private BlackNumberDao dao;
    private BlackContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_securityphone);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
        ((TextView)findViewById(R.id.tv_title)).setText("通讯卫士");
        ImageView ivLeft = (ImageView)findViewById(R.id.iv_left_button);
        ivLeft.setOnClickListener(this);
        ivLeft.setImageResource(R.drawable.back);
        flNoBlackNumber = (FrameLayout)findViewById(R.id.fl_no_black_number);
        flHaveBlackNumber = (FrameLayout)findViewById(R.id.fl_have_black_number);
        findViewById(R.id.btn_add_black_number).setOnClickListener(this);
        lvBlackNumbers = (ListView)findViewById(R.id.lv_black_numbers);
        lvBlackNumbers.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE://停止滑动状态
                        int lastVisiblePosition = lvBlackNumbers.getLastVisiblePosition();
                        if(lastVisiblePosition == pageBlackNumberList.size()-1) {
                            if(currentCount >= totalNumber) {
                                Toast.makeText(SecurityPhoneActivity.this, "没有更多的数据了", Toast.LENGTH_SHORT).show();
                            } else {
                                List<BlackContactInfo> tempList = dao.getPageBlackNumber(currentCount, pageSize);
                                currentCount += tempList.size();
                                pageBlackNumberList.addAll(tempList);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        //填充数据
        dao = new BlackNumberDao(SecurityPhoneActivity.this);
        totalNumber = dao.getTotalCount();
        if(totalNumber == 0) {
            flHaveBlackNumber.setVisibility(View.GONE);
            flNoBlackNumber.setVisibility(View.VISIBLE);
        } else {
            flHaveBlackNumber.setVisibility(View.VISIBLE);
            flNoBlackNumber.setVisibility(View.GONE);

            currentCount = 0;
            if(pageBlackNumberList.size() > 0) {
                pageBlackNumberList.clear();
            }
            List<BlackContactInfo> tempList = dao.getPageBlackNumber(currentCount, pageSize);
            currentCount += tempList.size();
            pageBlackNumberList.addAll(tempList);
            adapter = new BlackContactAdapter(pageBlackNumberList, this);
            adapter.setBlackContactCallBack(new BlackContactAdapter.BlackContactCallBack() {
                @Override
                public void dataSizeReduced() {//当List中有数据删除时回调此函数
                    currentCount--;
                    totalNumber = dao.getTotalCount();
                    if(totalNumber == 0) { //数据库中已经没有了
                        flHaveBlackNumber.setVisibility(View.GONE);
                        flNoBlackNumber.setVisibility(View.VISIBLE);
                    } else if(totalNumber != 0 && currentCount == 0) { //数据库中还有，当前List中没有了 ->去数据库中取数据到List中
                        List<BlackContactInfo> tempList = dao.getPageBlackNumber(currentCount, pageSize);
                        currentCount += tempList.size();
                        pageBlackNumberList.addAll(tempList);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            lvBlackNumbers.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(totalNumber != dao.getTotalCount()) { //离开后又回到Activity时，数据发生变化
            if(dao.getTotalCount() == 0) {
                flHaveBlackNumber.setVisibility(View.GONE);
                flNoBlackNumber.setVisibility(View.VISIBLE);
            } else {
                flHaveBlackNumber.setVisibility(View.VISIBLE);
                flNoBlackNumber.setVisibility(View.GONE);

                currentCount = 0;
                pageBlackNumberList.clear();
                List<BlackContactInfo> tempList = dao.getPageBlackNumber(currentCount, pageSize);
                currentCount += tempList.size();
                pageBlackNumberList.addAll(tempList);
                adapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_button:
                finish();
                break;
            case R.id.btn_add_black_number:
                startActivity(new Intent(this, AddBlackNumberActivity.class));
                break;
        }
    }
}
