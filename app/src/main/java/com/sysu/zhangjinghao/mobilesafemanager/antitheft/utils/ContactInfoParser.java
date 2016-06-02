package com.sysu.zhangjinghao.mobilesafemanager.antitheft.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sysu.zhangjinghao.mobilesafemanager.antitheft.entity.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjinghao on 16/5/30.
 */
public class ContactInfoParser {
    public static List<ContactInfo> getSystemContactInfos(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        List<ContactInfo> infos = new ArrayList<ContactInfo>();
        Cursor cursor = resolver.query(uri, new String[] {"contact_id"}, null, null, null);
        while(cursor.moveToNext()) {
            String id = cursor.getString(0);
            if(id != null) {
                //Log.d("TAG", "联系人id:"+cursor.getString(0));
                ContactInfo info = new ContactInfo();
                info.id = id;
                Cursor dataCursor = resolver.query(
                        dataUri, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);
                while (dataCursor.moveToNext()) {
                    String data1 = dataCursor.getString(0);
                    String mimetype = dataCursor.getString(1);
                    if(mimetype.equals("vnd.android.cursor.item/name")) {
                        //Log.d("TAG", "姓名＝"+data1);
                        info.name = data1;
                    } else if(mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                        //Log.d("TAG", "电话＝"+data1);
                        info.phone = data1;
                    }
                }
                infos.add(info);
                dataCursor.close();
            }
        }
        cursor.close();
        return infos;
    }

    public static List<ContactInfo> getSimContactInfos(Context context){
        Uri uri = Uri.parse("content://icc/adn");
        List<ContactInfo> infos = new ArrayList<ContactInfo>();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if(cursor != null) {
            while(cursor.moveToNext()) {
                ContactInfo info = new ContactInfo();
                int nameFieldColumnIndex = cursor.getColumnIndex("name");
                info.name =  cursor.getString(nameFieldColumnIndex);

                int numberFieldColumnIndex = cursor.getColumnIndex("number");
                info.phone= cursor.getString(numberFieldColumnIndex);
                infos.add(info);
            }
        }
        cursor.close();
        return infos;
    }
}
