package com.hardlove.library.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;


/**
 * =====================================
 * Copyright (C)
 * 作   者: CL
 * 版   本：1.0.0
 * 创建日期：2019-10-24 09:47
 * 修改日期：
 * 描   述：系统状态栏占位View(拥有沉浸式状态栏中布局中填充状态栏位置，4.4以上才支持，4.4以下直接隐藏)
 * =====================================
 */
public class StatusBarPlaceholderView extends View {

    private static final String TAG = "StatusBarView";
    private static final int STATUS_BAR_HEIGHT = 25;//25dp
    private static final String STATUS_BAR_HEIGHT_KEY = "status_bar_height_key";
    private int statusBarHeight;
    private SharedPreferences sharedPreferences;


    public StatusBarPlaceholderView(Context context) {
        super(context);
        initView(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4及以上支持
            this.setVisibility(VISIBLE);
        } else {
            this.setVisibility(GONE);//4.4以下不支持沉浸式直接隐藏
        }

    }

    public StatusBarPlaceholderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);


    }

    private void initView(Context context) {
        sharedPreferences = context.getSharedPreferences("system_status_bar_config", Context.MODE_PRIVATE);
        statusBarHeight = getStatusBarHeight();
    }


//    @TargetApi(28)
//    public void getNotchParams() {
//        final View decorView = ((Activity) getContext()).getWindow().getDecorView();
//
//        decorView.post(new Runnable() {
//            @Override
//            public void run() {
//                DisplayCutout displayCutout = decorView.getRootWindowInsets().getDisplayCutout();
//                Log.e("TAG", "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
//                Log.e("TAG", "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
//                Log.e("TAG", "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
//                Log.e("TAG", "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());
//
//                List<Rect> rects = displayCutout.getBoundingRects();
//                if (rects == null || rects.size() == 0) {
//                    Log.e("TAG", "不是刘海屏");
//                } else {
//                    Log.e("TAG", "刘海屏数量:" + rects.size());
//                    for (Rect rect : rects) {
//                        Log.e("TAG", "刘海屏区域：" + rect);
//                    }
//                }
//            }
//        });
//    }

//    /**
//     * 获取状态栏高度
//     *
//     * @param context context
//     * @return 状态栏高度
//     */
//    private  int getStatusBarHeight(Context context) {
//        // 获得状态栏高度
//        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
//        return context.getResources().getDimensionPixelSize(resourceId);
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (statusBarHeight == 0) {
            measureStatusBarHeight();
            Log.d(TAG, "requestLayout~~~~~~~");
            requestLayout();
        }

    }

    /**
     * 测量系统状态栏高度
     */
    private void measureStatusBarHeight() {

        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;

            if (statusBarHeight > 0) {
                sharedPreferences.edit().putInt(STATUS_BAR_HEIGHT_KEY, statusBarHeight).apply();
                Log.e(TAG, "保存系统状态栏高度，statusBarHeight：" + statusBarHeight);
            }

            Log.e(TAG, "onWindowFocusChanged~~ " + " statusBarHeight:" + statusBarHeight);


        }
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure~~~~~");


        if (statusBarHeight > 0) {
            Log.d(TAG, "填充状态栏高度：" + statusBarHeight + " SDK VERSION: " + Build.VERSION.SDK_INT);
            setMeasuredDimension(widthMeasureSpec, statusBarHeight);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout~~~~~");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw~~~~~");
    }

    private int getStatusBarHeight() {
        return sharedPreferences.getInt(STATUS_BAR_HEIGHT_KEY, 0);

    }
}
