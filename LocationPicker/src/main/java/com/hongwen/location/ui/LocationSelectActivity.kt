package com.hongwen.location.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
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

        // 设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.apply {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = Color.TRANSPARENT
            }
        }

    }


}