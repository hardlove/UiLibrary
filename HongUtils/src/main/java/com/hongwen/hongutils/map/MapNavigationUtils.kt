package com.hongwen.hongutils.map

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/**
 * ==================================================
 * Author：CL
 * 日期:2023/9/28
 * 说明：地图导航工具类
 * ==================================================
 **/
object MapNavigationUtils {
    /**
     * 调起高德地图搜索
     */
    fun searchPoi(context: Context, keywords: String, dev: Int = 0) {
        //https://lbs.amap.com/api/amap-mobile/guide/android/search (高德地图手机版)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data =
            Uri.parse("androidamap://poi?sourceApplication=${context.packageName}&keywords=${keywords}&dev=${dev}")
        if (intent.resolveActivity(context.packageManager) != null) {

            context.startActivity(intent)

        } else {
            //https://lbs.amap.com/api/uri-api/guide/search/search
            // callnative: 是否尝试调起高德地图APP并在APP中查看，0表示不调起，1表示调起, 默认值为0
            intent.data =
                Uri.parse("https://m.amap.com/search/view/keywords=${keywords}&type=nearby&src=${context.packageName}&coordinate=gaode&callnative=1")
            context.startActivity(intent)
        }
    }

    /**
     * 是否已安装APP
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val pm: PackageManager = context.packageManager
        return try {
            pm.getApplicationInfo(packageName, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}