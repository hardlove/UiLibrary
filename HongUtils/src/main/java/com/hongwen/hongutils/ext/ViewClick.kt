package com.hongwen.hongutils.ext

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import com.hongwen.hongutils.R

/**
 * Created by chenlu at 2023/3/16 17:23
 */
fun View.isFastClick(): Boolean {
    val last: Long = getTag(R.id.view_click_tag) as? Long ?: 0L
    val millis = System.currentTimeMillis()
    setTag(R.id.view_click_tag, millis)
    return millis - last < 500
}
fun Activity.isFastClick(): Boolean {
    val decorView = window.decorView
    return decorView.isFastClick()
}
fun Fragment.isFastClick(): Boolean {
    return requireView().isFastClick()
}