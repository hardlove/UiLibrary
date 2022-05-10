package com.carlos.permissionhelper;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.reflect.TypeToken;

import org.reactivestreams.Subscription;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.schedulers.Schedulers;

public class PermissionHelper {
    private final static String PERMISSION_REQUEST_RECORD = "PERMISSION_REQUEST_RECORD";
    private SimpleCallback mSimpleCallback;
    private FullCallback mFullCallback;
    private boolean ignore = true;//48小时内请求过的权限不再重复请求
    private boolean goSetting;//跳转系统权限设置页面
    /**
     * 权限申请记录
     */
    private HashMap<String, Long> permissionRecords;
    /**
     * 要请求的权限
     */
    private String[] requestPermissions;
    private HashMap<String, String> requestReasons;


    public PermissionHelper(@NonNull final String... permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            permissionList.addAll(Arrays.asList(PermissionConstants.getPermissions(permission)));
        }
        requestPermissions = permissionList.toArray(new String[0]);
        permissionRecords = getPermissionRequestRecords();
    }

    public static PermissionHelper permission(@NonNull final String... permissions) {
        return new PermissionHelper(permissions);
    }

    public PermissionHelper addReasons(@NonNull final String... reasons) {
        if (requestPermissions.length != reasons.length) {
            throw new InvalidParameterException("requestPermissionReasons.size() != reasons.length");
        }

        requestReasons = new HashMap<>();
        for (int i = 0; i < reasons.length; i++) {
            requestReasons.put(requestPermissions[i], reasons[i]);
        }
        return this;
    }

    public PermissionHelper goSettingUI(boolean goSetting) {
        this.goSetting = goSetting;
        return this;
    }

    /**
     * 获取权限申请记录
     * key:权限名称
     * value:时间戳 毫秒
     */
    private static HashMap<String, Long> getPermissionRequestRecords() {
        String json = SPStaticUtils.getString(PERMISSION_REQUEST_RECORD);
        HashMap<String, Long> records = GsonUtils.fromJson(json, TypeToken.getParameterized(HashMap.class, String.class, Long.class).getType());
        if (records == null) {
            records = new HashMap<>();
        }
        return records;
    }

    /**
     * 记录已经请求过的权限以及请求的时间
     *
     * @param permission
     */
    private static void addRequestedPermission(String... permission) {
        if (permission == null || permission.length == 0) return;
        String json = SPStaticUtils.getString(PERMISSION_REQUEST_RECORD);
        HashMap<String, Long> records = GsonUtils.fromJson(json, TypeToken.getParameterized(HashMap.class, String.class, Long.class).getType());
        if (records == null) {
            records = new HashMap<>();
        }
        for (String s : permission) {
            records.put(s, System.currentTimeMillis());
        }
        SPStaticUtils.put(PERMISSION_REQUEST_RECORD, GsonUtils.toJson(records));
    }

    public void request() {
        if (requestPermissions == null || requestPermissions.length == 0) {
            if (mFullCallback != null) {
                mFullCallback.onGranted(Collections.emptyList());
            }
            if (mSimpleCallback != null) {
                mSimpleCallback.onGranted();
            }
            return;
        }

        Set<Map.Entry<String, Long>> entrySet = permissionRecords.entrySet();
        Iterator<Map.Entry<String, Long>> iterator = entrySet.iterator();
        //总共需要申请的权限
        List<String> o1 = new ArrayList<>(Arrays.asList(requestPermissions));
        //48小时内已经申请过的权限
        List<String> o2 = new ArrayList<>();
        if (ignore) {
            while (iterator.hasNext()) {
                Map.Entry<String, Long> next = iterator.next();
                String key = next.getKey();
                if (o1.contains(key)) {
                    //该权限已经请求过，检查是否已超过48小时
                    if (System.currentTimeMillis() - next.getValue() < 48 * 60 * 60 * 1000) {
                        o2.add(key);
                    }
                }
            }

            //移除不需要重复申请的权限
            o1.removeAll(o2);
        }

        String[] temp = o1.toArray(new String[0]);


        if (temp == null || temp.length == 0) {
            checkPermissionResult(Arrays.asList(requestPermissions));

            return;
        }

        Flowable.fromArray(temp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<String>() {
                    QueueSubscription<String> subscription;
                    ReasonDialog dialog;
                    final FragmentActivity currentActivity = (FragmentActivity) ActivityUtils.getTopActivity();

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        subscription = (QueueSubscription) s;
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(String permission) {
                        LogUtils.dTag("XXX", "onNext~~~~~~~" + permission);
                        if (requestReasons != null) {
                            String reason = requestReasons.get(permission);
                            if (!TextUtils.isEmpty(reason)) {
                                if (dialog == null) {
                                    dialog = showReasonDialog(currentActivity, reason);
                                } else {
                                    dialog.updateReason(reason);
                                }
                            }
                        }


                        //还有其它需要申请的权限，添加权限请求记录
                        if (ignore) {
                            addRequestedPermission(permission);
                        }
                        PermissionUtils.permission(permission)
                                .callback(new PermissionUtils.SimpleCallback() {
                                    @Override
                                    public void onGranted() {
                                        if (subscription.isEmpty()) {
                                            if (dialog != null) {
                                                dialog.dismiss();
                                                dialog = null;
                                            }
                                            checkPermissionResult(Arrays.asList(requestPermissions));
                                        } else {
                                            subscription.request(1);
                                        }

                                    }

                                    @Override
                                    public void onDenied() {
                                        if (subscription.isEmpty()) {
                                            if (dialog != null) {
                                                dialog.dismiss();
                                                dialog = null;
                                            }
                                            checkPermissionResult(Arrays.asList(requestPermissions));
                                        } else {
                                            subscription.request(1);
                                        }
                                    }
                                })
                                .request();
                    }

                    @Override
                    public void onError(Throwable t) {
                        LogUtils.dTag("XXX", "onError~~~~~~~");

                    }

                    @Override
                    public void onComplete() {
                        LogUtils.dTag("XXX", "onComplete~~~~~~~");

                    }
                });
    }

    private void checkPermissionResult(List<String> permissions) {
        //申请的权限48小时内已经全部申请过
        List<String> deniedForever = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        List<String> granted = new ArrayList<>();
        boolean flag = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(Utils.getApp(), permission) != PackageManager.PERMISSION_GRANTED) {
                denied.add(permission);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!ActivityUtils.getTopActivity().shouldShowRequestPermissionRationale(permission)) {
                        deniedForever.add(permission);
                    }
                }
                flag = false;
            } else {
                granted.add(permission);
            }
        }

        if (mFullCallback != null) {
            if (flag) {
                mFullCallback.onGranted(permissions);
            } else {
                if (!denied.isEmpty()) {
                    //部分已授权的
                    mFullCallback.onGranted(granted);
                }
                mFullCallback.onDenied(deniedForever, denied);
                if (goSetting) {
                    showOpenAppSettingDialog(ActivityUtils.getTopActivity());
                }
            }
        }
        if (mSimpleCallback != null) {
            if (flag) {
                mSimpleCallback.onGranted();
            } else {
                mSimpleCallback.onDenied();
                if (goSetting) {
                    showOpenAppSettingDialog(ActivityUtils.getTopActivity());
                }
            }
        }
    }


    /**
     * 48小时内请求过的权限不再请求
     *
     * @param ignore true:48小时内请求过的权限不再请求
     * @return
     */
    public PermissionHelper ignoreRequestedIn48H(boolean ignore) {
        this.ignore = ignore;
        return this;
    }


    /**
     * Set the simple call back.
     *
     * @param callback the simple call back
     * @return the single {@link PermissionUtils} instance
     */
    public PermissionHelper callback(final SimpleCallback callback) {
        mSimpleCallback = callback;
        return this;
    }

    /**
     * Set the full call back.
     *
     * @param callback the full call back
     * @return the single {@link PermissionUtils} instance
     */
    public PermissionHelper callback(final FullCallback callback) {
        mFullCallback = callback;
        return this;
    }


    public interface FullCallback {
        void onGranted(@NonNull List<String> var1);

        void onDenied(@NonNull List<String> var1, @NonNull List<String> var2);
    }

    public interface SimpleCallback {
        void onGranted();

        void onDenied();
    }

    public static class ReasonDialog extends Dialog {
        private TextView tvReason;
        private String reason;

        public ReasonDialog(@NonNull Context context, String reason) {
            super(context);
            this.reason = reason;
            setContentView(R.layout.dialog_permission_reason);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            tvReason = findViewById(R.id.tv_reason);
            tvReason.setText(Html.fromHtml(reason));

        }

        @Override
        public void show() {
            super.show();
            Window window = getWindow();
            if (window != null) {
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.TOP;
                params.width = (int) (displayMetrics.widthPixels * 1.0f);
                window.setBackgroundDrawable(new ColorDrawable(0x00000000));
                window.setAttributes(params);
            }
        }

        public static ReasonDialog newInstance(Context context, String reason) {
            return new ReasonDialog(context, reason);
        }

        public void updateReason(String reason) {
            if (tvReason != null) {
                tvReason.setText(Html.fromHtml(reason));
            }
        }

    }

    public static ReasonDialog showReasonDialog(FragmentActivity activity, String reason) {
        ReasonDialog dialog = ReasonDialog.newInstance(activity, reason);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    /**
     * 判断是否具有某权限
     *
     * @param object
     * @param perms
     * @return
     */
    public static boolean hasPermissions(@NonNull Object object, @NonNull String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(Utils.getApp(), perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }

        return true;
    }

    public static void showOpenAppSettingDialog(Context context) {
        showOpenAppSettingDialog(context, "注意", "您已限制授权我们申请的权限，请选择“允许”，否则该功能将无法正常使用！");
    }

    public static void showOpenAppSettingDialog(Context context, String title, String content) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .setPositiveButton("去设置", (dialog, which) -> PermissionUtils.launchAppDetailsSettings()).show();
    }
}
