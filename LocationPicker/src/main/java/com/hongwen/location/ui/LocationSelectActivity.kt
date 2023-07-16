package com.hongwen.location.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hongwen.location.adapter.LocationSelectAdapter
import com.hongwen.location.databinding.ActivityLocationSelectBinding
import com.hongwen.location.db.DBManager
import com.hongwen.location.model.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by chenlu at 2023/7/13 16:59
 */
class LocationSelectActivity : AppCompatActivity() {

    private lateinit var bind: ActivityLocationSelectBinding
    private lateinit var adapter: LocationSelectAdapter

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


        initWidgets()

        iniData()

    }

    private fun initWidgets() {
        bind.recyclerView.setHasFixedSize(true)
        bind.recyclerView.layoutManager = LinearLayoutManager(this)


    }

    private fun iniData() {
        lifecycleScope.launch {

            val items = withContext(Dispatchers.IO) {
                val dbManager = DBManager(this@LocationSelectActivity)
                val allCities = dbManager.allCities

                allCities
            }

            val hotItems = ArrayList<Location>()
            bind.recyclerView.adapter = LocationSelectAdapter(items, hotItems).also { adapter = it }

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
            WindowInsetsCompat.Builder(insets)
                .setInsets(
                    WindowInsetsCompat.Type.systemBars(),
                    Insets.of(0, 0, 0, systemBarsInsets.bottom)
                )
                .build()
        }
    }

}