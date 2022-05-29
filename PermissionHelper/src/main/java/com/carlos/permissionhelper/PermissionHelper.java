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
import androidx.annotation.Nullable;
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

import org.reactivestreams.Publisher;
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
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.functions.Function;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.schedulers.Schedulers;

public class PermissionHelper {
    private static final String[] GROUP_CALENDAR = new String[]{"android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR"};
    private static final String[] GROUP_CONTACTS = new String[]{"android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS", "android.permission.GET_ACCOUNTS"};
    private static final String[] GROUP_LOCATION = new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    private static final String[] GROUP_PHONE = new String[]{"android.permission.READ_PHONE_STATE", "android.permission.READ_PHONE_NUMBERS", "android.permission.CALL_PHONE", "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "com.android.voicemail.permission.ADD_VOICEMAIL", "android.permission.USE_SIP", "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.ANSWER_PHONE_CALLS"};
    private static final String[] GROUP_SMS = new String[]{"android.permission.SEND_SMS", "android.permission.RECEIVE_SMS", "android.permission.READ_SMS", "android.permission.RECEIVE_WAP_PUSH", "android.permission.RECEIVE_MMS"};
    private static final String[] GROUP_STORAGE = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};


    private final static String PERMISSION_REQUEST_RECORD = "PERMISSION_REQUEST_RECORD";
    private SimpleCallback mSimpleCallback;
    private FullCallback mFullCallback;
    private boolean ignore = true;//48小时内请求过的权限不再重复请求
    private boolean goSetting;//跳转系统权限设置页面
    private CharSequence goSettingMsg;//跳转系统权限设置页面弹框描述内容
    /**
     * 权限申请记录
     */
    private final HashMap<String, Long> permissionRecords;
    /**
     * 要请求的权限
     */
    private final List<String> requestPermissions;
    private HashMap<String, String> requestReasons;
    /**
     * 分组申请
     */
    private boolean useGroupRequest = true;

    public static PermissionHelper builder() {
        return new PermissionHelper();
    }


    private PermissionHelper() {
        requestPermissions = new ArrayList<>();
        requestReasons = new HashMap<>();
        permissionRecords = getPermissionRequestRecords();
    }


    /**
     * 新增Permission
     *
     * @param permissions 权限名
     * @return
     */
    public PermissionHelper addPermission(@NonNull String... permissions) {
        requestPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * 新增Permission：一对一
     *
     * @param permission 权限名
     * @param reason     请求权限对于说明
     * @return
     */
    public PermissionHelper addPermission(@NonNull String permission, @Nullable String reason) {
        requestPermissions.add(permission);
        if (!TextUtils.isEmpty(reason)) {
            requestReasons.put(permission, reason);
        }
        return this;
    }

    /**
     * 新增Permission:多对一
     *
     * @param permissions 权限名
     * @param reason      请求权限对于说明
     * @return
     */
    public PermissionHelper addPermission(@NonNull List<String> permissions, @Nullable String reason) {
        for (String permission : permissions) {
            requestPermissions.add(permission);
            if (!TextUtils.isEmpty(reason)) {
                requestReasons.put(permission, reason);
            }
        }
        return this;
    }

    /**
     * 新增Permission:多对一
     *
     * @param permissions 权限名
     * @param reasons     请求权限对于说明
     * @return
     */
    public PermissionHelper addPermission(@NonNull List<String> permissions, @NonNull List<String> reasons) {
        if (permissions.size() != reasons.size()) {
            throw new InvalidParameterException("权限与对应说明长度不等！");
        }
        requestPermissions.addAll(permissions);
        for (int i = 0; i < permissions.size(); i++) {
            if (!TextUtils.isEmpty(reasons.get(i))) {
                requestReasons.put(permissions.get(i), reasons.get(i));
            }
        }

        return this;
    }

    /**
     * @param goSetting 是否跳转系统权限设置页面
     * @return
     */
    public PermissionHelper goSettingUI(boolean goSetting) {
        this.goSetting = goSetting;
        return this;
    }

    /**
     * @param sequence 跳转系统权限设置页面弹框描述内容
     * @return
     */
    public PermissionHelper goSettingMsg(CharSequence sequence) {
        this.goSettingMsg = sequence;
        return this;
    }

    /**
     * 禁止分组申请，即按单个权限申请
     *
     * @param disable
     * @return
     */
    public PermissionHelper disableGroupRequest(boolean disable) {
        this.useGroupRequest = !disable;
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
        if (requestPermissions == null || requestPermissions.size() == 0) {
            if (mFullCallback != null) {
                mFullCallback.onGranted();
            }
            if (mSimpleCallback != null) {
                mSimpleCallback.onGranted();
            }
            return;
        }

        Set<Map.Entry<String, Long>> entrySet = permissionRecords.entrySet();
        Iterator<Map.Entry<String, Long>> iterator = entrySet.iterator();
        //总共需要申请的权限
        List<String> o1 = new ArrayList<>(requestPermissions);
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
            checkPermissionResult(requestPermissions);
            return;
        }

        Flowable.fromArray(temp)
                // TODO: 2022/5/28 分组后再请求权限:同一group类的permission是否已有被拒绝的，如果有则不再申请该group类的权限
                .groupBy(permission -> {
                    if (useGroupRequest) {
                        // TODO: 2022/5/28 将权限分组
                        try {
                            String[] split = permission.split("_");
                            String key = split[split.length - 1];
                            return key;
                        } catch (Exception e) {
                            return permission;
                        }
                    } else {
                        return permission;
                    }
                })
                .flatMap((Function<GroupedFlowable<String, String>, Publisher<List<String>>>) groupedFlowable -> groupedFlowable.toList().toFlowable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<List<String>>() {
                    QueueSubscription<String> subscription;
                    ReasonDialog dialog;
                    final FragmentActivity currentActivity = (FragmentActivity) ActivityUtils.getTopActivity();

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        subscription = (QueueSubscription) s;
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(List<String> permission) {
                        LogUtils.dTag("XXX", "onNext~~~~~~~" + permission);


                        if (requestReasons != null) {
                            String reason = requestReasons.get(permission.get(0));
                            if (!TextUtils.isEmpty(reason)) {
                                if (dialog == null) {
                                    dialog = showReasonDialog(currentActivity, reason);
                                } else {
                                    dialog.updateReason(reason);
                                }
                                dialog.hide();
                                dialog.tvReason.postDelayed(() -> {
                                    if (dialog != null) {
                                        dialog.show();
                                    }
                                }, 500);
                            }
                        }


                        //还有其它需要申请的权限，添加权限请求记录
                        if (ignore) {
                            addRequestedPermission(permission.toArray(new String[0]));
                        }

                        //添加到权限申请记录
                        for (String s : permission) {
                            permissionRecords.put(s, System.currentTimeMillis());
                        }

                        PermissionUtils.permission(permission.toArray(new String[0]))
                                .callback(new PermissionUtils.SimpleCallback() {
                                    @Override
                                    public void onGranted() {
                                        if (dialog != null) {
                                            dialog.dismiss();
                                            dialog = null;
                                        }
                                        if (subscription.isEmpty()) {
                                            checkPermissionResult(requestPermissions);
                                        } else {
                                            subscription.request(1);
                                        }

                                    }

                                    @Override
                                    public void onDenied() {
                                        if (dialog != null) {
                                            dialog.dismiss();
                                            dialog = null;
                                        }
                                        if (subscription.isEmpty()) {
                                            checkPermissionResult(requestPermissions);
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

    boolean isShowing = false;

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
                mFullCallback.onGranted();
            } else {
                mFullCallback.onDenied(deniedForever, denied, granted);
                if (goSetting) {
                    if (!isShowing) {
                        showOpenAppSettingDialog(ActivityUtils.getTopActivity(), "注意", goSettingMsg);
                    }
                    isShowing = true;
                }
            }
        }
        if (mSimpleCallback != null) {
            if (flag) {
                mSimpleCallback.onGranted();
            } else {
                mSimpleCallback.onDenied();
                if (goSetting) {
                    if (!isShowing) {
                        showOpenAppSettingDialog(ActivityUtils.getTopActivity(), "注意", goSettingMsg);
                    }
                    isShowing = true;
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
        /**
         * 授予所有权限
         */
        void onGranted();

        /**
         * 部分权限被拒绝
         *
         * @param deniedForever 被永久拒绝
         * @param denied        被拒绝
         * @param granted       已经授予
         */
        void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied, @NonNull List<String> granted);
    }

    public interface SimpleCallback {
        /**
         * 授予所有权限
         */
        void onGranted();

        /**
         * 拒绝授予权限
         */
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
     * @param context
     * @param perms
     * @return
     */
    public static boolean hasPermissions(@NonNull Context context, @NonNull String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }

        return true;
    }


    public static void showOpenAppSettingDialog(Context context, String title, CharSequence content) {
        if (TextUtils.isEmpty(content)) {
            content = "您已限制授权我们申请的权限，请选择“允许”，否则该功能将无法正常使用！";
        }
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .setPositiveButton("去设置", (dialog, which) -> PermissionUtils.launchAppDetailsSettings()).show();
    }
}
