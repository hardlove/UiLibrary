package com.carlos.permissionhelper;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.reflect.TypeToken;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    private boolean ignore48H = true;//48小时内请求过的权限不再重复请求
    private boolean onlyRequestOnce = true;//是否永久不重复请求，即只能申请一次
    private boolean goSetting = false;//跳转系统权限设置页面
    private boolean showToast = true;//权限拒绝后是否显示Toast提示,goSetting 为false时生效
    private boolean isAutoRequest = false;//是否自动请求,非用户手动触发
    private OnGoSettingUIListener onGoSettingUIListener;//跳转系统权限设置页面监听
    private String goSettingMsg;//跳转系统权限设置页面弹框描述内容
    private boolean isSplit;//权限说明和权限请求是否分离(即：申请权限前先弹框询问用户是否同意申请)

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
    private int cancelTextColor;
    private int confirmTextColor;


    public static PermissionHelper builder() {
        return new PermissionHelper();
    }

    /**
     * @param permissions
     * @return
     * @see #builder()
     */
    @Deprecated
    public static PermissionHelper permission(@NonNull final String[] permissions) {
        return builder().addPermission(permissions);
    }

    @Deprecated
    public PermissionHelper addReasons(@NonNull final String... reasons) {
        if (requestPermissions.size() != reasons.length) {
            throw new InvalidParameterException("requestPermissionReasons.size() != reasons.length");
        }

        if (requestReasons == null) {
            requestReasons = new HashMap<>();
        }
        for (int i = 0; i < reasons.length; i++) {
            requestReasons.put(requestPermissions.get(i), reasons[i]);
        }
        return this;
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
    public PermissionHelper addPermission(@NonNull List<String> permissions) {
        requestPermissions.addAll(permissions);
        return this;
    }

    /**
     * 新增Permission
     *
     * @param permissions 权限名
     * @return
     */
    public PermissionHelper addPermission(@NonNull String[] permissions) {
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
     * 是否自动触发权限请求(非用户手动点击触发)
     *
     * @param isAutoRequest
     * @return
     */
    public PermissionHelper isAutoRequest(boolean isAutoRequest) {
        this.isAutoRequest = isAutoRequest;
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
     * @param showToast 权限拒绝后是否显示Toast提示,goSetting为false时生效
     * @return
     */
    public PermissionHelper showToast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }


    /**
     * @param goSetting 是否跳转系统权限设置页面
     * @return
     */
    public PermissionHelper goSettingUI(boolean goSetting, OnGoSettingUIListener onGoSettingUIListener) {
        this.goSetting = goSetting;
        this.onGoSettingUIListener = onGoSettingUIListener;
        return this;
    }

    /**
     * @param sequence 跳转系统权限设置页面弹框描述内容
     * @return
     */
    public PermissionHelper goSettingMsg(String sequence) {
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
     * 设置是否是分离式请求
     *
     * @param split true:分离式（即：在请求之前先弹框询问用户是否同意请求权限申请）  false：非分离式（即：不询问用户是否请求权限，而是在请求权限的同时在顶部弹窗说明请求权限原因）
     * @return
     */
    public PermissionHelper setSplit(boolean split) {
        this.isSplit = split;
        return this;
    }

    /**
     * 设置取消按钮文字颜色
     *
     * @param cancelTextColor
     * @return
     */
    public PermissionHelper setCancelTextColor(int cancelTextColor) {
        this.cancelTextColor = cancelTextColor;
        return this;
    }

    /**
     * 设置确定按钮文字颜色
     *
     * @param confirmTextColor
     * @return
     */
    public PermissionHelper setConfirmTextColor(int confirmTextColor) {
        this.confirmTextColor = confirmTextColor;
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
     * 该场景请求权限是否是第一次
     *
     * @param scenarioKey
     * @return
     */
    private static boolean isFirstRequest(String scenarioKey) {
        return SPStaticUtils.getLong(scenarioKey, 0) == 0;
    }

    /**
     * 记录本次场景权限请求的时间
     *
     * @param scenarioKey
     */
    private static void setRequestTime(String scenarioKey) {
        SPStaticUtils.put(scenarioKey, System.currentTimeMillis());
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
        if (ignore48H || onlyRequestOnce) {
            while (iterator.hasNext()) {
                Map.Entry<String, Long> next = iterator.next();
                String key = next.getKey();
                if (o1.contains(key)) {
                    if (onlyRequestOnce) {
                        //只能请求一次
                        o2.add(key);
                    } else if (System.currentTimeMillis() - next.getValue() < 48 * 60 * 60 * 1000) {
                        //该权限已经请求过，检查是否已超过48小时
                        o2.add(key);
                    }

                }
            }

            //移除不需要重复申请的权限
            o1.removeAll(o2);
        }

        String[] temp = o1.toArray(new String[0]);


        if (temp.length == 0) {
            checkPermissionResult(requestPermissions);
            return;
        }

        Flowable.fromArray(temp)
                // TODO: 2022/5/28 分组后再请求权限:同一group类的permission是否已有被拒绝的，如果有则不再申请该group类的权限
                .groupBy(permission -> {
                    if (useGroupRequest) {
                        // TODO: 2022/5/28 将权限分组
                        try {
                            String[] spl = permission.split("_");
                            String key = spl[spl.length - 1];
                            return key;
                        } catch (Exception e) {
                            return permission;
                        }
                    } else {
                        return permission;
                    }
                }).flatMap((Function<GroupedFlowable<String, String>, Publisher<List<String>>>) groupedFlowable -> groupedFlowable.toList().toFlowable()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new FlowableSubscriber<List<String>>() {
                    QueueSubscription<String> subscription;
                    ReasonSimpleDialog simpleDialog;
                    ReasonSelectDialog selectDialog;
                    final Activity currentActivity = ActivityUtils.getTopActivity();

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        subscription = (QueueSubscription) s;
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(List<String> permissionList) {
                        LogUtils.dTag("XXX", "onNext~~~~~~~" + permissionList);

                        //添加到权限申请记录到文件
                        if (ignore48H || onlyRequestOnce) {
                            addRequestedPermission(permissionList.toArray(new String[0]));
                        }
                        //添加到权限申请记录到内存
                        for (String s : permissionList) {
                            permissionRecords.put(s, System.currentTimeMillis());
                        }

                        if (isSplit) {
                            //分离式请求
                            if (requestReasons != null) {
                                String reason = requestReasons.get(permissionList.get(0));
                                if (!TextUtils.isEmpty(reason)) {
                                    if (selectDialog != null && selectDialog.isShowing()) {
                                        selectDialog.dismiss();
                                        selectDialog = null;
                                    }
                                    selectDialog = showReasonSelectDialog(currentActivity, reason, cancelTextColor, confirmTextColor);
                                    selectDialog.setOnDialogClickListener(new ReasonSelectDialog.OnDialogClickListener() {
                                        @Override
                                        public void onCancel() {
                                            if (selectDialog != null && selectDialog.isShowing()) {
                                                selectDialog.dismiss();
                                            }
                                            selectDialog = null;

                                            //下一个
                                            if (subscription.isEmpty()) {
                                                checkPermissionResult(requestPermissions);
                                            } else {
                                                subscription.request(1);
                                            }
                                        }

                                        @Override
                                        public void onConfirm() {
                                            if (selectDialog != null && selectDialog.isShowing()) {
                                                selectDialog.dismiss();
                                            }
                                            selectDialog = null;

                                            performRequestPermission(permissionList);
                                        }
                                    });


                                } else {
                                    performRequestPermission(permissionList);
                                }
                            } else {
                                performRequestPermission(permissionList);
                            }
                        } else {
                            //非分离式请求
                            if (requestReasons != null) {
                                String reason = requestReasons.get(permissionList.get(0));
                                if (!TextUtils.isEmpty(reason)) {
                                    if (simpleDialog == null) {
                                        simpleDialog = showReasonSimpleDialog(currentActivity, reason);
                                    } else {
                                        simpleDialog.updateReason(reason);
                                    }
                                    simpleDialog.hide();
                                    simpleDialog.tvReason.postDelayed(() -> {
                                        if (simpleDialog != null) {
                                            simpleDialog.show();
                                        }
                                    }, 500);
                                }
                            }


                            performRequestPermission(permissionList);
                        }


                    }

                    private void performRequestPermission(List<String> permission) {
                        PermissionUtils.permission(permission.toArray(new String[0])).callback(new PermissionUtils.SimpleCallback() {
                            @Override
                            public void onGranted() {
                                if (simpleDialog != null) {
                                    simpleDialog.dismiss();
                                    simpleDialog = null;
                                }
                                if (subscription.isEmpty()) {
                                    checkPermissionResult(requestPermissions);
                                } else {
                                    subscription.request(1);
                                }

                            }

                            @Override
                            public void onDenied() {
                                if (simpleDialog != null) {
                                    simpleDialog.dismiss();
                                    simpleDialog = null;
                                }
                                if (subscription.isEmpty()) {
                                    checkPermissionResult(requestPermissions);
                                } else {
                                    subscription.request(1);
                                }
                            }
                        }).request();
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
                    String scenarioKey = getCurrentScenarioKey();
                    if (isAutoRequest && !isFirstRequest(scenarioKey)) {
                        //当前场景非用户手动触发请求,并且非第一次请求
                        ToastUtils.showShort(MessageFormat.format("您已拒绝授权我们申请的{0}权限", getDeniedPermissionName()));
                        return;
                    }

                    setRequestTime(scenarioKey);
                    if (!isShowing) {
                        showOpenAppSettingDialog(ActivityUtils.getTopActivity(), "温馨提示", generateGoSettingMsg(), cancelTextColor, confirmTextColor, "取消", "去设置", onGoSettingUIListener);
                    }
                    isShowing = true;
                } else {
                    if (showToast) {
                        ToastUtils.showShort(MessageFormat.format("您已拒绝授权我们申请的{0}权限", getDeniedPermissionName()));
                    }
                }
            }
        }
        String scenarioKey = getCurrentScenarioKey();
        if (mSimpleCallback != null) {
            if (flag) {
                mSimpleCallback.onGranted();
            } else {
                mSimpleCallback.onDenied();
                if (goSetting) {
                    if (isAutoRequest && !isFirstRequest(scenarioKey)) {
                        //当前场景非用户手动触发请求,并且非第一次请求
                        ToastUtils.showShort(MessageFormat.format("您已拒绝授权我们申请的{0}权限", getDeniedPermissionName()));
                        return;
                    }

                    if (!isShowing) {
                        showOpenAppSettingDialog(ActivityUtils.getTopActivity(), "温馨提示", generateGoSettingMsg(), cancelTextColor, confirmTextColor, "取消", "去设置", onGoSettingUIListener);
                    }
                    isShowing = true;
                }else {
                    if (showToast) {
                        ToastUtils.showShort(MessageFormat.format("您已拒绝授权我们申请的{0}权限", getDeniedPermissionName()));
                    }
                }
            }
        }
        setRequestTime(scenarioKey);
    }

    private String generateGoSettingMsg() {
        if (!TextUtils.isEmpty(goSettingMsg)) {
            return goSettingMsg;
        }

        ArrayList<String> list = new ArrayList<>();
        for (String s : requestPermissions) {
            boolean flag = !hasPermissions(Utils.getApp(), s);
            if (flag) {
                String name = getPermissionName(s);
                if (!list.contains(name)) {
                    list.add(name);
                }
            }

        }
        //return MessageFormat.format("您已拒绝我们申请的<font color=\"#FF0000\"><b>{0}</b></font>权限，如需使用该功能，请手动授予权限！", list.toString());
        //return MessageFormat.format("您已拒绝我们申请的<font><b>{0}</b></font>权限，如需使用该功能，请手动授予权限！", list.toString());
        return MessageFormat.format("您已拒绝我们申请的{0}权限，如需使用该功能，请手动授予权限！", list.toString());
    }

    private String getDeniedPermissionName() {
        ArrayList<String> list = new ArrayList<>();
        for (String s : requestPermissions) {
            boolean flag = !hasPermissions(Utils.getApp(), s);
            if (flag) {
                String name = getPermissionName(s);
                if (!list.contains(name)) {
                    list.add(name);
                }
            }

        }
        return list.toString();
    }

    protected String getPermissionName(String permission) {

        String msg = null;
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
            case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                msg = "位置信息";
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                msg = "媒体和文件";
                break;
            case Manifest.permission.CAMERA:
                msg = "照相机";
                break;
            case Manifest.permission.RECORD_AUDIO:
                msg = "麦克风";
                break;
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.ACCESS_NETWORK_STATE:
            case Manifest.permission.ACCESS_WIFI_STATE:
                msg = "设备信息";
                break;
            case Manifest.permission.READ_PHONE_NUMBERS:
                msg = "电话号码";
                break;
            case Manifest.permission.CALL_PHONE:
                msg = "拨打电话";
                break;
            case Manifest.permission.READ_SMS:
            case Manifest.permission.SEND_SMS:
                msg = "短信";
                break;
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
                msg = "通话记录";
                break;
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.WRITE_CALENDAR:
                msg = "日历访";
                break;
            case Manifest.permission.ACTIVITY_RECOGNITION://android 10 新增
                msg = "健身运动";
                break;
            case Manifest.permission.BODY_SENSORS:
                msg = "身体传感器";
                break;
            case Manifest.permission.SET_WALLPAPER:
                msg = "设置壁纸";
                break;
            case Manifest.permission.INSTALL_SHORTCUT:
            case Manifest.permission.UNINSTALL_SHORTCUT:
                msg = "创建桌面快捷方式";
                break;
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
            case "android.permission.SYSTEM_OVERLAY_WINDOW":
                msg = "悬浮窗";
                break;
        }

        return msg;
    }

    /**
     * 获取当前场景请求的标识符
     *
     * @return
     */
    private String getCurrentScenarioKey() {
        return UUID.nameUUIDFromBytes(requestPermissions.toString().getBytes()).toString();
    }


    /**
     * 48小时内请求过的权限不再请求
     *
     * @param ignore48H true:48小时内请求过的权限不再请求
     * @return
     */
    public PermissionHelper ignoreRequestedIn48H(boolean ignore48H) {
        this.ignore48H = ignore48H;
        return this;
    }


    /**
     * 是否永久只能请求一次
     *
     * @param onlyRequestOnce true:永久只能请求一次  为 true时，优先级大于 ignore48H
     * @return
     */
    public PermissionHelper canOnlyRequestOnce(boolean onlyRequestOnce) {
        this.onlyRequestOnce = onlyRequestOnce;
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

    public static class ReasonSimpleDialog extends Dialog {
        private TextView tvReason;
        private String reason;

        public ReasonSimpleDialog(@NonNull Context context, String reason) {
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

        public static ReasonSimpleDialog newInstance(Context context, String reason) {
            return new ReasonSimpleDialog(context, reason);
        }

        public void updateReason(String reason) {
            if (tvReason != null) {
                tvReason.setText(Html.fromHtml(reason));
            }
        }

    }

    public static ReasonSimpleDialog showReasonSimpleDialog(Activity activity, String reason) {
        ReasonSimpleDialog dialog = ReasonSimpleDialog.newInstance(activity, reason);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    public static class ReasonSelectDialog extends Dialog {
        private TextView tvReason;
        private Button btnCancel;
        private Button btnConfirm;
        private CharSequence reason;
        private int cancelTextColor;
        private int confirmTextColor;
        private String cancel;
        private String confirm;

        public ReasonSelectDialog(@NonNull Context context, String reason, int cancelTextColor, int confirmTextColor) {
            super(context);
            this.reason = reason;
            this.cancelTextColor = cancelTextColor;
            this.confirmTextColor = confirmTextColor;
            setContentView(R.layout.dialog_permission_reason_select);
        }

        public ReasonSelectDialog(@NonNull Context context, CharSequence reason, int cancelTextColor, int confirmTextColor, String cancel, String confirm) {
            super(context);
            this.reason = reason;
            this.cancelTextColor = cancelTextColor;
            this.confirmTextColor = confirmTextColor;
            this.cancel = cancel;
            this.confirm = confirm;
            setContentView(R.layout.dialog_permission_reason_select);
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            tvReason = findViewById(R.id.tv_reason);
            btnCancel = findViewById(R.id.btn_cancel);
            btnConfirm = findViewById(R.id.btn_confirm);
            if (!TextUtils.isEmpty(cancel)) {
                btnCancel.setText(cancel);
            }
            if (!TextUtils.isEmpty(confirm)) {
                btnConfirm.setText(confirm);
            }
            if (reason.toString().startsWith("<font>") || reason.toString().contains("<b>") || reason.toString().contains("<br>") || reason.toString().contains("<") || reason.toString().contains(">")) {
                tvReason.setText(Html.fromHtml(reason.toString()));
            } else {
                tvReason.setText(reason);
            }
            if (cancelTextColor != 0) {
                btnCancel.setTextColor(cancelTextColor);
            }
            if (confirmTextColor != 0) {
                btnConfirm.setTextColor(confirmTextColor);
            }

            btnCancel.setOnClickListener(v -> {
                dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
            });
            btnConfirm.setOnClickListener(v -> {
                dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            });


        }

        @Override
        public void show() {
            super.show();
            Window window = getWindow();
            if (window != null) {
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.CENTER;
                params.width = (int) (displayMetrics.widthPixels * 1.0f);
                window.setBackgroundDrawable(new ColorDrawable(0x00000000));
                window.setAttributes(params);
            }
        }

        public static ReasonSelectDialog newInstance(Context context, String reason, int cancelTextColor, int confirmTextColor) {
            return new ReasonSelectDialog(context, reason, cancelTextColor, confirmTextColor);
        }

        public static ReasonSelectDialog newInstance(@NonNull Context context, CharSequence reason, int cancelTextColor, int confirmTextColor, String cancel, String confirm) {
            return new ReasonSelectDialog(context, reason, cancelTextColor, confirmTextColor, cancel, confirm);
        }

        public void updateReason(String reason) {
            if (tvReason != null) {
                tvReason.setText(Html.fromHtml(reason));
            }
        }

        public void setOnDialogClickListener(OnDialogClickListener listener) {
            this.listener = listener;
        }

        private OnDialogClickListener listener;

        public interface OnDialogClickListener {
            void onCancel();

            void onConfirm();
        }


    }

    public static ReasonSelectDialog showReasonSelectDialog(Activity activity, String reason, int cancelTextColor, int confirmTextColor) {
        ReasonSelectDialog dialog = ReasonSelectDialog.newInstance(activity, reason, cancelTextColor, confirmTextColor);
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


    public static void showOpenAppSettingDialog(Context context, String title, String content, int cancelTextColor, int confirmTextColor, String cancel, String confirm, OnGoSettingUIListener onGoSettingUIListener) {
        if (TextUtils.isEmpty(title)) {
            title = "温馨提示";
        }
        if (TextUtils.isEmpty(content)) {
            content = "您已拒绝我们申请的权限，如需使用该功能，请手动授予权限！";
        }
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(title).append("\n").append(content);
        sb.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(18, true), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ReasonSelectDialog selectDialog = ReasonSelectDialog.newInstance(context, sb, cancelTextColor, confirmTextColor, cancel, confirm);
        selectDialog.setOnDialogClickListener(new ReasonSelectDialog.OnDialogClickListener() {
            @Override
            public void onCancel() {
                if (onGoSettingUIListener != null) {
                    onGoSettingUIListener.onCancel();
                }
            }

            @Override
            public void onConfirm() {
                PermissionUtils.launchAppDetailsSettings();
                if (onGoSettingUIListener != null) {
                    onGoSettingUIListener.onConfirm();
                }
            }
        });
        selectDialog.show();


//        new AlertDialog.Builder(context)
//                .setTitle(title)
//                .setMessage(content)
//                .setCancelable(false)
//                .setNegativeButton("取消", (dialog, which) -> {
//                    if (onGoSettingUIListener != null) {
//                        onGoSettingUIListener.onCancel();
//                    }
//                })
//                .setPositiveButton("去设置", (dialog, which) -> {
//                    PermissionUtils.launchAppDetailsSettings();
//                    if (onGoSettingUIListener != null) {
//                        onGoSettingUIListener.onConfirm();
//                    }
//                }).show();
    }

    public interface OnGoSettingUIListener {
        void onConfirm();

        void onCancel();
    }

}
