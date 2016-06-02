package com.sysu.zhangjinghao.mobilesafemanager.blacklist.entity;

/**
 * Created by zhangjinghao on 16/6/1.
 */
public class BlackContactInfo {
    //黑名单号码
    public String number;
    //黑名单联系人名称
    public String name;
    //黑名单拦截模式
    public int mode;

    public String getModeString(int mode) {
        switch (mode) {
            case 1:
                return "电话拦截";
            case 2:
                return "短信拦截";
            case 3:
                return "电话、短信拦截";
        }
        return "";
    }
}
