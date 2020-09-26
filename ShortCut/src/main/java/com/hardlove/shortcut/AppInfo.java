package com.hardlove.shortcut;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CacheMemoryStaticUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.Utils;

import java.io.Serializable;

/**
 * Author：CL
 * 日期:2020/6/17
 * 说明：
 **/
public class AppInfo implements Serializable {
    private String appName = "";
    private String packageName = "";
    private String versionName = "";
    private long versionCode;
    private int appIconResID;
    private transient Drawable appIcon = null;


    //Failed to allocate a 262156 byte allocation with 45680 free bytes and 44KB until OOM
    public static AppInfo fromPackageInfo(PackageInfo packageInfo) {
        AppInfo appInfo = new AppInfo();
        PackageManager pm = Utils.getApp().getPackageManager();
        appInfo.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
        appInfo.packageName = packageInfo.packageName;
        appInfo.versionName = packageInfo.versionName;
        appInfo.versionCode = packageInfo.versionCode;
        //优先从缓存中读取
        appInfo.appIcon = CacheMemoryStaticUtils.get(appInfo.getPackageName());
        if (appInfo.appIcon == null) {
            appInfo.appIcon = packageInfo.applicationInfo.loadIcon(pm);
            CacheMemoryStaticUtils.put(appInfo.getPackageName(), appInfo.appIcon);
        }

        appInfo.appIconResID = AppUtils.getAppIconId(packageInfo.packageName);
        return appInfo;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getAppIcon() {
        if (appIcon == null) {
            appIcon = AppUtils.getAppIcon(packageName);
        }
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }


    public int getAppIconResID() {
        return appIconResID;
    }

    public void setAppIconResID(int appIconResID) {
        this.appIconResID = appIconResID;
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
