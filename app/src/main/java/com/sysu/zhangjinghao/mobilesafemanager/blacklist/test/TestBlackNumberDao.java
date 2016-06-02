package com.sysu.zhangjinghao.mobilesafemanager.blacklist.test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.sysu.zhangjinghao.mobilesafemanager.blacklist.db.dao.BlackNumberDao;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.entity.BlackContactInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by zhangjinghao on 16/6/1.
 */
public class TestBlackNumberDao extends AndroidTestCase {
    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getContext();
    }

    //测试添加
    public void testAdd() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        Random random = new Random(8979);
        for(long i=0; i<30; i++) {
            BlackContactInfo info = new BlackContactInfo();
            info.number = "135"+(10000000+i);
            info.name = "zhangsan"+i;
            info.mode = random.nextInt(3)+1;
            dao.add(info);
        }
    }

    //测试删除
    public void testDelete() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        BlackContactInfo info = new BlackContactInfo();
        for(long i=1; i<5; i++) {
            info.number = "135"+(10000000+i);
            dao.delete(info);
        }
    }

    //测试分页查询
    public void testGetPageBlackNumber() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        List<BlackContactInfo> list = dao.getPageBlackNumber(2, 5);
        for(int i=0; i<list.size(); i++) {
            Log.i("TAG", "list:" + list.get(i).number);
        }
    }

    //测试根据号码查询黑名单信息
    public void testGetBlackContactMode() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        int mode = dao.getBlackContactMode("13510000014");
        Log.i("TAG", "mode:"+mode);
    }

    //测试数据总条目
    public void testGetTotalNumber() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        int total = dao.getTotalCount();
        Log.i("TAG", "count:"+total);
    }

    //测试号码是否在数据库中
    public void testIsNumberExist() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean isExist = dao.isNumberExist("13510000022");
        Log.i("TAG", "isExist:"+isExist);
    }
}
