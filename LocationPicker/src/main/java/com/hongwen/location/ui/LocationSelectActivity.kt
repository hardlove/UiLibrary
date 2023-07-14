package com.hongwen.location.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import com.hongwen.location.databinding.ActivityLocationSelectBinding

/**
 * Created by chenlu at 2023/7/13 16:59
 */
class LocationSelectActivity : FragmentActivity() {

    private lateinit var bind: ActivityLocationSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLocationSelectBinding.inflate(layoutInflater)

        setContentView(bind.root)

        /**
         * 沉浸式状态栏方法一：
         * 1.添加
         * <!--状态栏半透明,实现沉浸式状态栏-->
         * <item name="android:windowTranslucentStatus">true</item>
         * <!-- Status bar color.-->
         * <item name="android:statusBarColor">@android:color/transparent</item>
         * 2.onCreate 中添加
         * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         * window?.apply {
         * decorView.systemUiVisibility =
         * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
         * statusBarColor = Color.TRANSPARENT
         * }
         * }
         */


        /**
         * 沉浸式状态栏方法二：
         */
        // 检查设备版本是否支持沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor()
            setSystemUiVisibility()
        }




    }


    // 设置状态栏颜色为透明
    private fun setStatusBarColor() {
        window?.statusBarColor = Color.TRANSPARENT
    }

    // 设置系统UI可见性以实现沉浸式状态栏效果
    private fun setSystemUiVisibility() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            Log.d("Carlos","systemBarsInsets.bottom:"+systemBarsInsets.bottom+"  rect:"+systemBarsInsets)
            WindowInsetsCompat.Builder(insets)
                .setInsets(WindowInsetsCompat.Type.systemBars(),
                    Insets.of(0, 0, 0, systemBarsInsets.bottom))
                .build()
        }
    }

}