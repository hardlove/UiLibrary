package com.hongwen.hongutils.ext

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

fun Activity.startURL(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
fun Fragment.startURL(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}