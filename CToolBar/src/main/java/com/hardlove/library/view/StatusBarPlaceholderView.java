package com.hardlove.library.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


/**
 * =====================================
 * Copyright (C)
 * 作   者: CL
 * 版   本：1.0.0
 * 创建日期：2019-10-24 09:47
 * 修改日期：2023-6-29
 * 描   述：系统状态栏占位View(拥有沉浸式状态栏中布局中填充状态栏位置，5.0以上才支持，5.0以下直接隐藏)
 * =====================================
 */
public class StatusBarPlaceholderView extends View {

    private static final String TAG = "StatusBarView";
    private static final int STATUS_BAR_HEIGHT = 25;//25dp
    private static final String STATUS_BAR_HEIGHT_KEY = "status_bar_height_key";
    private int statusBarHeight;
    private SharedPreferences sharedPreferences;


    public StatusBarPlaceholderView(Context context) {
        this(context, null);

    }

    public StatusBarPlaceholderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    private void initView(Context context) {
        sharedPreferences = context.getSharedPreferences("system_status_bar_config", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//5.0及以上支持
            this.setVisibility(VISIBLE);
        } else {
            this.setVisibility(GONE);//5.0以下不支持沉浸式直接隐藏
        }

    }


    /**
     * 获取系统状态栏高度方式一：
     * 使用WindowInsetsCompat获取
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (statusBarHeight == 0) {
            statusBarHeight = getStatusBarHeight();
        }
        if (statusBarHeight == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                WindowInsetsCompat windowInsets = ViewCompat.getRootWindowInsets(this);
                if (windowInsets != null) {
                    statusBarHeight = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
                    if (statusBarHeight > 0) {
                        sharedPreferences.edit().putInt(STATUS_BAR_HEIGHT_KEY, statusBarHeight).apply();
                        Log.e(TAG, "保存系统状态栏高度，statusBarHeight：" + statusBarHeight);
                    }
                }
            }
        }
    }


    /**
     * 获取系统状态栏高度方式二：
     * <p>
     * Rect frame = new Rect();
     * activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
     * statusBarHeight = frame.top;
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (statusBarHeight == 0) {
            measureStatusBarHeight();
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

        if (statusBarHeight > 0) {
            Log.d(TAG, "填充状态栏高度：" + statusBarHeight + " SDK VERSION: " + Build.VERSION.SDK_INT);
            setMeasuredDimension(widthMeasureSpec, statusBarHeight);
        } else {
            setMeasuredDimension(widthMeasureSpec, dip2px(getContext(), STATUS_BAR_HEIGHT));
        }

    }

    private int getStatusBarHeight() {
        return sharedPreferences.getInt(STATUS_BAR_HEIGHT_KEY, 0);

    }


    /**
     * 状态栏高度标识位
     */
    public static final String FLAG_STATUS_BAR_HEIGHT = "status_bar_height";
    /**
     * 导航栏竖屏高度标识位
     */
    public static final String FLAG_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    /**
     * 导航栏横屏高度标识位
     */
    public static final String FLAG_NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";

    /**
     * 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context, FLAG_STATUS_BAR_HEIGHT);
    }

    private static int getInternalDimensionSize(Context context, String key) {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);

                if (sizeTwo >= sizeOne) {
                    return sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                    float f = sizeOne * densityTwo / densityOne;
                    return (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
                }
            }
        } catch (Exception ignored) {
            return 0;
        }
        return result;
    }
}
