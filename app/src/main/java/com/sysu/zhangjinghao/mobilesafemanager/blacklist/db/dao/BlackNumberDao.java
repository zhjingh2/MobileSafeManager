package com.sysu.zhangjinghao.mobilesafemanager.blacklist.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.sysu.zhangjinghao.mobilesafemanager.blacklist.db.BlackNumberOpenHelper;
import com.sysu.zhangjinghao.mobilesafemanager.blacklist.entity.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjinghao on 16/6/1.
 * 操作黑名单数据库的工具类
 */
public class BlackNumberDao {
    private BlackNumberOpenHelper mBlackNumberOpenHelper;

    public BlackNumberDao(Context context) {
        mBlackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    //增
    public boolean add(BlackContactInfo blackContactInfo) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(blackContactInfo.number.startsWith("+86")) {
            blackContactInfo.number = blackContactInfo.number.substring(3, blackContactInfo.number.length());
        }
        values.put("number", blackContactInfo.number);
        values.put("name", blackContactInfo.name);
        values.put("mode", blackContactInfo.mode);
        long rowID = db.insert("blacknumber", null, values);
        if(rowID == -1) {
            return false;
        } else {
            return true;
        }
    }
    //删
    public boolean delete(BlackContactInfo blackContactInfo) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        int rowID = db.delete("blacknumber", "number=?", new String[]{blackContactInfo.number});
        if(rowID == -1) {
            return false;
        } else {
            return true;
        }
    }
    //查
    public List<BlackContactInfo> getPageBlackNumber(int startPos, int offset) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        List<BlackContactInfo> list = new ArrayList<BlackContactInfo>();
        Cursor cursor = db.rawQuery("select number,mode,name " +
                "from blacknumber " +
                "limit ?" +
                "offset ?", new String[] {String.valueOf(offset), String.valueOf(startPos)});
        while(cursor.moveToNext()) {
            BlackContactInfo temp = new BlackContactInfo();
            temp.number = cursor.getString(0);
            temp.mode = cursor.getInt(1);
            temp.name = cursor.getString(2);
            list.add(temp);
        }
        cursor.close();
        db.close();
        SystemClock.sleep(30);
        return list;
    }

    //判断号码是否在黑名单中
    public boolean isNumberExist(String desNumber) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", null, "number=?", new String[] {desNumber}, null, null, null);
        if(cursor.moveToNext()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    //根据number查询mode
    public int getBlackContactMode(String desNumber) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{desNumber}, null, null, null);
        int mode = 0;
        if(cursor.moveToNext()) {
            mode = cursor.getInt(cursor.getColumnIndex("mode"));
        }
        cursor.close();
        db.close();
        return mode;
    }

    //获取数据库黑名单的总条目个数
    public int getTotalCount() {
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select COUNT(*)" +
                "from blacknumber", null);
        if(cursor.moveToNext()) {
            int count = cursor.getInt(0);
            cursor.close();
            db.close();
            return count;
        }
        cursor.close();
        db.close();
        return 0;
    }

}
