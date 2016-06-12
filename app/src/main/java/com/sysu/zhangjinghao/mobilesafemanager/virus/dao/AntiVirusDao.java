package com.sysu.zhangjinghao.mobilesafemanager.virus.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by zhangjinghao on 16/6/11.
 */
public class AntiVirusDao {
    //检测某个MD5是否是病毒
    //返回null表示无毒
    public static String checkVirus(String md5) {
        String desc = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.sysu.zhangjinghao.mobilesafemanager/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[] {md5});
        if(cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return desc;
    }


}
