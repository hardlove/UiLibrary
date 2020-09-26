package com.hardlove.shortcut;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author：CL
 * 日期:2020/6/11
 * 说明：创建桌面快捷方式工具类
 **/
public class ShortCutUtils {
    private static final String TAG = "ShortCutUtils";
    private static final String SHORT_CUT_ACTION = Utils.getApp().getPackageName() + "short_cut_action";

    /**
     * 调整到应用设置界面，打开快捷方式权限
     * @param context
     */
    public static void openPermission(Context context) {
        ShortcutPermissionSetting shortcutPermissionSetting = new ShortcutPermissionSetting(context);
        shortcutPermissionSetting.start();
    }

    /**
     * 检查快捷方式权限是否已开启
     *
     * @param context
     * @return
     */
    public static int checkPermission(Context context) {
        return ShortcutPermissionCheck.check(context);
    }

    /**
     * 给指定主键创建快捷方式
     *
     * @param activity
     * @param name        快捷方式的名称
     * @param bitmap      快捷方式的图标
     * @param packageName 启动的第三方APP包名
     * @param cls         点击桌面快捷键时启动本应用的Activity（在本应用的Activity中去启动第三方的App）
     */
    public static void createShortCut(@NonNull final Activity activity, String name, Bitmap bitmap, String packageName, Class<?> cls, final CreateListener createListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Intent addShortIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//            addShortIntent.putExtra("duplicate", false); //禁止重复添加。 小米系统无效果
            addShortIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);//快捷方式的名字
//            Intent.ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(activity, bitmap);
//            addShortIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource); //快捷方式的图标
            addShortIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap); //快捷方式的图标
            //点击快捷方式打开的页面
            Intent actionIntent = new Intent(Intent.ACTION_MAIN);
            actionIntent.setClass(activity, cls);
            actionIntent.putExtra("package", packageName);
            actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            actionIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
            actionIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加categoryCATEGORY_LAUNCHER 应用被卸载时快捷方式也随之删除
            addShortIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            activity.sendBroadcast(addShortIntent); //设置完毕后发送广播给系统。
            if (createListener != null) {
                createListener.onSuccess();
            }
        } else {
            //获取shortcutManager
            ShortcutManager shortcutManager = (ShortcutManager) activity.getSystemService(Context.SHORTCUT_SERVICE);

            //如果默认桌面支持requestPinShortcut（ShortcutInfo，IntentSender）方法，则返回TRUE。
            if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
                String shortCutID = packageName;//使用包名作为快捷键ID，vivo 部分手机测试发现不能创建同名快捷键

                Intent shortCutIntent = new Intent(Intent.ACTION_VIEW);//快捷方式启动页面
                shortCutIntent.setClass(activity, cls);
                shortCutIntent.putExtra("package", packageName);
                shortCutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //快捷方式创建相关信息。图标名字 id
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(activity, shortCutID)
                        .setIcon(IconCompat.createWithBitmap(bitmap))
                        .setShortLabel(name)
                        .setDisabledMessage("当前快捷方式不可使用")
                        .setIntent(shortCutIntent)
                        .build();

                boolean exist = isShortcutExist(activity, shortCutID);
                if (exist) {
                    boolean succeed = updatePinShortcut(activity, shortcutInfo.toShortcutInfo());
                    if (createListener != null) {
                        if (succeed) {
                            createListener.onUpdateSucceed();
                        } else {
                            createListener.onUpdateFailed();
                        }
                    }
                    LogUtils.dTag(TAG, "快捷键已更新。。。。。。");
                    return;
                }

                //==================================================================================

                final Timer timer = new Timer();
                final BroadcastReceiver shortReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            LogUtils.dTag("收到快捷方式创建成功通知，取消计时, 当前线程isMan：" + ThreadUtils.isMainThread());
                            timer.cancel();
                            activity.unregisterReceiver(this);
                            if (!activity.isFinishing() && createListener != null) {
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        createListener.onSuccess();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                IntentFilter filter = new IntentFilter();
                filter.addAction(SHORT_CUT_ACTION);
                activity.registerReceiver(shortReceiver, filter);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        LogUtils.dTag("计时任务结束，判定为没有权限，创建失败！");
                        activity.unregisterReceiver(shortReceiver);
                        if (createListener != null) {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createListener.onPermissionReject();
                                }
                            });

                        }
                    }
                }, 2000);


                //创建快捷方式时候回调
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0,
                        new Intent(SHORT_CUT_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);
                boolean isSupport = shortcutManager.requestPinShortcut(shortcutInfo.toShortcutInfo(), pendingIntent.getIntentSender());
                if (!isSupport) {//华为是权限未开启，小米上不是
                    timer.cancel();
                    if (createListener != null) {
                        createListener.onPermissionReject();
                    }
                }
            } else {
                if (createListener != null) {
                    createListener.onFail();
                }
            }
        }

    }


    /**
     * 获取指定APP 的启动主件Intent
     *
     * @param pkg
     * @return
     */
    @Nullable
    public static Intent getLaunchIntent(String pkg) {
        final PackageManager packageManager = Utils.getApp().getPackageManager();
        return packageManager.getLaunchIntentForPackage(pkg);
    }

    /**
     * 获取设备安装的所有应用
     * @param mContext
     * @return
     */
    public static List<PackageInfo> getAllInstalledApps(Context mContext) {
        List<PackageInfo> list = new ArrayList<>();
        list.addAll(getAllInstalledSystemApps(mContext));
        list.addAll(getAllInstalledThirdApps(mContext));
        return list;
    }

    /**
     * 获取设备安装的系统应用
     *
     * @param context
     * @return Return a List of all Systems packages that are installed on the device.
     */
    public static List<PackageInfo> getAllInstalledSystemApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        final Iterator<PackageInfo> iterator = packages.iterator();
        while (iterator.hasNext()) {
            PackageInfo next = iterator.next();
            if ((next.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //非系统应用
                iterator.remove();
            } else {
                Intent launchAppIntent = IntentUtils.getLaunchAppIntent(next.applicationInfo.packageName);
                if (launchAppIntent == null) {
                    iterator.remove();
                }
            }

        }
        return packages;

    }

    /**
     * 获取设备安装的第三方应用
     *
     * @param context
     * @return Return a List of all Third packages that are installed on the device.
     */
    public static List<PackageInfo> getAllInstalledThirdApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        final Iterator<PackageInfo> iterator = packages.iterator();
        while (iterator.hasNext()) {
            PackageInfo next = iterator.next();
            if ((next.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //系统应用
                iterator.remove();
            } else {
                Intent launchAppIntent = IntentUtils.getLaunchAppIntent(next.applicationInfo.packageName);
                if (launchAppIntent == null) {
                    iterator.remove();
                }
            }
        }
        return packages;
    }

    /**
     * 判定快捷方式是否存在
     * @param context
     * @param id
     * @return
     */
    public static boolean isShortcutExist(@NonNull Context context, @NonNull String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager mShortcutManager =
                    context.getSystemService(ShortcutManager.class);
            if (mShortcutManager == null) {
                return false;
            }

            List<ShortcutInfo> pinnedShortcuts =
                    mShortcutManager.getPinnedShortcuts();
            for (ShortcutInfo pinnedShortcut : pinnedShortcuts) {
                if (pinnedShortcut.getId().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 更新快捷方式
     * @param context
     * @param info
     * @return
     */
    public static boolean updatePinShortcut(Context context, ShortcutInfo info) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager mShortcutManager =
                    context.getSystemService(ShortcutManager.class);
            if (mShortcutManager == null) {
                return false;
            }

            return mShortcutManager.updateShortcuts(Collections.singletonList(info));
        }
        return false;
    }

    public interface CreateListener {
        void onPermissionReject();

        void onFail();

        void onSuccess();

        void onUpdateSucceed();

        void onUpdateFailed();
    }

}
