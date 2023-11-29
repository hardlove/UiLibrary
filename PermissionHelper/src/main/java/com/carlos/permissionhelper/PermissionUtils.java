package com.carlos.permissionhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PermissionUtils {
    private static final String TAG = "PermissionUtils";
    private final Context mContext;
    /*用户申请的权限*/
    private final String[] mPermissionsParam;
    private SimpleCallback mSimpleCallback;
    private FullCallback mFullCallback;
    private ThemeCallback mThemeCallback;
    private List<String> mPermissionsRequest;
    private List<String> mPermissionsGranted;
    private List<String> mPermissionsDenied;
    private List<String> mPermissionsDeniedForever;

    private PermissionUtils(Activity activity,String[] permissions) {
        this.mPermissionsParam = permissions;
        mContext = activity;
    }

    public static PermissionUtils permission(Activity activity,final String... permissions) {
        return new PermissionUtils(activity,permissions);
    }

    public static void launchAppDetailsSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public PermissionUtils callback(final SimpleCallback callback) {
        mSimpleCallback = callback;
        return this;
    }

    public interface SimpleCallback {
        void onGranted();

        void onDenied();
    }

    public interface FullCallback {
        void onGranted(@NonNull List<String> granted);

        void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied);
    }

    public interface ThemeCallback {
        void onActivityCreate(@NonNull Activity activity);
    }

    public void request() {
        if (mPermissionsParam == null || mPermissionsParam.length == 0) {
            Log.w(TAG, "No permissions to request.");
            return;
        }
        //还需要申请的权限
        mPermissionsRequest = new ArrayList<>();
        //已经被授予的权限
        mPermissionsGranted = new ArrayList<>();
        //已经被拒绝的权限
        mPermissionsDenied = new ArrayList<>();
        //被永久拒绝的权限
        mPermissionsDeniedForever = new ArrayList<>();

        Pair<List<String>, List<String>> requestAndDeniedPermissions = getRequestAndDeniedPermissions(mPermissionsParam);
        //需要申请的权限
        Set<String> permissions = new LinkedHashSet<>(requestAndDeniedPermissions.first);
        //直接被拒绝的权限（未在Manifest中声明）
        mPermissionsDenied.addAll(requestAndDeniedPermissions.second);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionsGranted.addAll(permissions);
            requestCallback();
        } else {
            for (String permission : permissions) {
                if (isGranted(permission)) {
                    mPermissionsGranted.add(permission);
                } else {
                    mPermissionsRequest.add(permission);
                }
            }
            if (mPermissionsRequest.isEmpty()) {
                requestCallback();
            } else {
                startPermissionActivity();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startPermissionActivity() {
        TranslucentActivity.TransActivityDelegate delegate = new TranslucentActivity.TransActivityDelegate() {
            private static final int REQUEST_CODE = 0x1010;

            @Override
            public void onCreated(@NonNull TranslucentActivity activity, @Nullable Bundle savedInstanceState) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
                if (mThemeCallback != null) {
                    mThemeCallback.onActivityCreate(activity);
                }
                activity.requestPermissions(mPermissionsRequest.toArray(new String[0]), REQUEST_CODE);
            }

            @Override
            public void onRequestPermissionsResult(@NonNull TranslucentActivity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
                if (REQUEST_CODE == requestCode) {
                    handleRequestPermissionsResult(activity);
                    activity.finish();
                }
            }


        };
        TranslucentActivity.start(mContext, delegate);
    }

    private void handleRequestPermissionsResult(final Activity activity) {
        getPermissionsStatus(activity);
        requestCallback();
    }


    private void requestCallback() {
        if (mSimpleCallback != null) {
            if (mPermissionsDenied.isEmpty()) {
                mSimpleCallback.onGranted();
            } else {
                mSimpleCallback.onDenied();
            }
            mSimpleCallback = null;
        }
        if (mFullCallback != null) {
            if (mPermissionsRequest.size() == 0
                    || mPermissionsGranted.size() > 0) {
                mFullCallback.onGranted(mPermissionsGranted);
            }
            if (!mPermissionsDenied.isEmpty()) {
                mFullCallback.onDenied(mPermissionsDeniedForever, mPermissionsDenied);
            }
            mFullCallback = null;
        }
        mThemeCallback = null;
    }

    private void getPermissionsStatus(final Activity activity) {
        for (String permission : mPermissionsRequest) {
            if (isGranted(permission)) {
                mPermissionsGranted.add(permission);
            } else {
                mPermissionsDenied.add(permission);
                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    mPermissionsDeniedForever.add(permission);
                }
            }
        }
    }

    private Pair<List<String>, List<String>> getRequestAndDeniedPermissions(final String... permissionsParam) {
        List<String> requestPermissions = new ArrayList<>();
        List<String> deniedPermissions = new ArrayList<>();
        //获取APP在Manifest中声明的权限
        List<String> appPermissions = getPermissions();
        for (String param : permissionsParam) {
            boolean isIncludeInManifest = false;
            if (appPermissions.contains(param)) {
                requestPermissions.add(param);
                isIncludeInManifest = true;
            }

            if (!isIncludeInManifest) {
                deniedPermissions.add(param);
                Log.e("PermissionUtils", "U should add the permission of " + param + " in manifest.");
            }
        }
        return Pair.create(requestPermissions, deniedPermissions);
    }

    private boolean isGranted(final String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(mContext, permission);
    }

    /*获取APP在Manifest中声明的权限*/
    public List<String> getPermissions() {
        return getPermissions(mContext.getPackageName());
    }

    /**
     * Return the permissions used in application.
     *
     * @param packageName The name of the package.
     * @return the permissions used in application
     */
    public List<String> getPermissions(final String packageName) {
        PackageManager pm = mContext.getPackageManager();
        try {
            String[] permissions = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
            if (permissions == null) return Collections.emptyList();
            return Arrays.asList(permissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
