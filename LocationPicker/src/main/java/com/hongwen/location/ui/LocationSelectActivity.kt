package com.hongwen.location.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hongwen.location.databinding.ActivityLocationSelectBinding

/**
 * Created by chenlu at 2023/7/13 16:59
 */
class LocationSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //    window?.apply {
        //        decorView.systemUiVisibility =
        //            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //        //去掉状态栏蒙版背景,使背景透明
        //        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //        statusBarColor = Color.TRANSPARENT
        //        //设置导航栏颜
        //        window.navigationBarColor = Color.WHITE
        //    }
        //}

        /**
         * 沉浸式状态栏方法二：
         */
        //检查设备版本是否支持沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor()
            //setImmersiveSystemUiVisibility(binding.root)
            setImmersiveSystemUiVisibility(window.decorView)
        }


        binding.btnConfirm.setOnClickListener {


            val locationSelectDialogFragment = LocationSelectDialogFragment()
            locationSelectDialogFragment.show(supportFragmentManager, "select")
        }

    }


    // 设置状态栏颜色为透明
    private fun setStatusBarColor() {
        window?.statusBarColor = Color.RED
    }

    /**
     * 沉浸式状态栏实现 一
     * 视图根布局加上
    android:fitsSystemWindows="true" 可自动为根布局添加导航栏高度的paddingBottom,避免被导航栏覆盖
     */
    private fun setImmersiveSystemUiVisibility(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            Log.d(
                "Carlos",
                "systemBarsInsets.bottom:" + systemBarsInsets.bottom + "  rect:" + systemBarsInsets
            )
            WindowInsetsCompat.Builder(insets)
                .setInsets(
                    WindowInsetsCompat.Type.systemBars(),
                    // android:fitsSystemWindows="true" 可自动为根布局添加导航栏高度的paddingBottom,避免被导航栏覆盖
                    Insets.of(0, 0, 0, systemBarsInsets.bottom)
                )
                .build()
        }
    }

}