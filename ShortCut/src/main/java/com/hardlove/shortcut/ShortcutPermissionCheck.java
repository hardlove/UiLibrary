package com.hardlove.shortcut;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.hardlove.shortcut.ShortcutPermission.PERMISSION_ASK;
import static com.hardlove.shortcut.ShortcutPermission.PERMISSION_DENIED;
import static com.hardlove.shortcut.ShortcutPermission.PERMISSION_GRANTED;
import static com.hardlove.shortcut.ShortcutPermission.PERMISSION_UNKNOWN;

/**
 * 检查快捷方式权限工具类
 */
class ShortcutPermissionCheck {
    private static final String TAG = "ShortcutPermissionCheck";
    private static final String MARK = Build.MANUFACTURER.toLowerCase();


    /**
     * 检查快捷方式权限是否已开启
     *
     * @param context
     * @return
     */
    @ShortcutPermission.PermissionResult
    public static int check(Context context) {
        Log.d(TAG, "manufacturer = " + MARK + ", api level= " + Build.VERSION.SDK_INT);
        int result = PERMISSION_UNKNOWN;
        try {
            if (MARK.contains("huawei")) {
                result = checkOnEMUI(context);
            } else if (MARK.contains("xiaomi")) {
                result = checkOnMIUI(context);
            } else if (MARK.contains("oppo")) {
                result = checkOnOPPO(context);
            } else if (MARK.contains("vivo")) {
                result = checkOnVIVO(context);
            } else if (MARK.contains("samsung") || MARK.contains("meizu")) {
                result = PERMISSION_GRANTED;
            }
        } catch (Exception e) {
            //修复vivo Z3 检测权限时crash问题
            // java.lang.SecurityException: Permission Denial: opening provider com.bbk.launcher2.data.LauncherProvider from ProcessRecord{f8f30af 16137:com.qiutinghe.change/u0a172} (pid=16137, uid=10172)
            // requires com.bbk.launcher2.permission.READ_SETTINGS or com.bbk.l
            e.printStackTrace();
        }
        return result;
    }


    @ShortcutPermission.PermissionResult
    public static int checkOnEMUI(@NonNull Context context) {
        Log.d(TAG, "checkOnEMUI");
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        try {
            Class<?> PermissionManager = Class.forName("com.huawei.hms.permission.PermissionManager");
            Method canSendBroadcast = PermissionManager.getDeclaredMethod("canSendBroadcast", Context.class, Intent.class);
            boolean invokeResult = (boolean) canSendBroadcast.invoke(PermissionManager, context, intent);
            Log.d(TAG, "EMUI check permission canSendBroadcast invoke result = " + invokeResult);
            if (invokeResult) {
                return PERMISSION_GRANTED;
            } else {
                return PERMISSION_DENIED;
            }
        } catch (ClassNotFoundException e) {//Mutil-catch require API level 19
            Log.e(TAG, e.getMessage(), e);
            return PERMISSION_UNKNOWN;
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage(), e);
            return PERMISSION_UNKNOWN;
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage(), e);
            return PERMISSION_UNKNOWN;
        } catch (InvocationTargetException e) {
            Log.e(TAG, e.getMessage(), e);
            return PERMISSION_UNKNOWN;
        }
    }

    @ShortcutPermission.PermissionResult
    public static int checkOnVIVO(@NonNull Context context) {
        Log.d(TAG, "checkOnVIVO");
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            Log.d(TAG, "contentResolver is null");
            return PERMISSION_UNKNOWN;
        }
        Uri parse = Uri.parse("content://com.bbk.launcher2.settings/favorites");
        Cursor query = contentResolver.query(parse, null, null, null, null);
        if (query == null) {
            Log.d(TAG, "cursor is null (Uri : content://com.bbk.launcher2.settings/favorites)");
            return PERMISSION_UNKNOWN;
        }
        try {
            while (query.moveToNext()) {
                String titleByQueryLauncher = query.getString(query.getColumnIndexOrThrow("title"));
                Log.d(TAG, "title by query is " + titleByQueryLauncher);
                if (!TextUtils.isEmpty(titleByQueryLauncher) && titleByQueryLauncher.equals(getAppName(context))) {
                    int value = query.getInt(query.getColumnIndexOrThrow("shortcutPermission"));
                    Log.d(TAG, "permission value is " + value);
                    if (value == 1 || value == 17) {
                        return PERMISSION_DENIED;
                    } else if (value == 16) {
                        return PERMISSION_GRANTED;
                    } else if (value == 18) {
                        return PERMISSION_ASK;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            query.close();
        }
        return PERMISSION_UNKNOWN;
    }

    @ShortcutPermission.PermissionResult
    public static int checkOnMIUI(@NonNull Context context) {
        Log.d(TAG, "checkOnMIUI");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return PERMISSION_UNKNOWN;
        }
        try {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            String pkg = context.getApplicationContext().getPackageName();
            int uid = context.getApplicationInfo().uid;
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getDeclaredMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
            Object invoke = checkOpNoThrowMethod.invoke(mAppOps, 10017, uid, pkg);//the ops of INSTALL_SHORTCUT is 10017
            if (invoke == null) {
                Log.d(TAG, "MIUI check permission checkOpNoThrowMethod(AppOpsManager) invoke result is null");
                return PERMISSION_UNKNOWN;
            }
            String result = invoke.toString();
            Log.d(TAG, "MIUI check permission checkOpNoThrowMethod(AppOpsManager) invoke result = " + result);
            switch (result) {
                case "0":
                    return PERMISSION_GRANTED;
                case "1":
                    return PERMISSION_DENIED;
                case "5":
                    return PERMISSION_ASK;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return PERMISSION_UNKNOWN;
        }
        return PERMISSION_UNKNOWN;
    }

    @ShortcutPermission.PermissionResult
    public static int checkOnOPPO(@NonNull Context context) {
        Log.d(TAG, "checkOnOPPO");
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            Log.d(TAG, "contentResolver is null");
            return PERMISSION_UNKNOWN;
        }
        Uri parse = Uri.parse("content://settings/secure/launcher_shortcut_permission_settings");
        Cursor query = contentResolver.query(parse, null, null, null, null);
        if (query == null) {
            Log.d(TAG, "cursor is null (Uri : content://settings/secure/launcher_shortcut_permission_settings)");
            return PERMISSION_UNKNOWN;
        }
        try {
            String pkg = context.getApplicationContext().getPackageName();
            while (query.moveToNext()) {
                String value = query.getString(query.getColumnIndex("value"));
                Log.d(TAG, "permission value is " + value);
                if (!TextUtils.isEmpty(value)) {
                    if (value.contains(pkg + ", 1")) {
                        return PERMISSION_GRANTED;
                    }
                    if (value.contains(pkg + ", 0")) {
                        return PERMISSION_DENIED;
                    }
                }
            }
            return PERMISSION_UNKNOWN;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return PERMISSION_UNKNOWN;
        } finally {
            query.close();
        }
    }

    private static String getAppName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            return pi == null ? null : pi.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
