package com.hongwen.hongutils.map

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast


/**
 * ==================================================
 * Author：CL
 * 日期:2023/9/28
 * 说明：高德地图导航工具类
 * ==================================================
 **/
object GaoDeMapNavigationUtils {
    /**
     * 调起高德地图搜索POI
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
     * @see https://lbs.amap.com/api/amap-mobile/guide/android/navigation
     * @param POI 名称
     */
    fun navigate(context: Context, latitude: Double, longitude: Double, poiname: String? = null) {

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            intent.setPackage("com.autonavi.minimap")

            val url =
                StringBuilder("androidamap://navi?sourceApplication=${context.packageName}&lat=$latitude&lon=$longitude&dev=1")
            if (!poiname.isNullOrBlank()) {
                url.append("&poiname=$poiname")
            }
            intent.setData(Uri.parse(url.toString()))

            context.startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "请安装高德地图软件", Toast.LENGTH_SHORT).show()
        }
    }

}

/**
 * ==================================================
 * Author：CL
 * 日期:2023/9/28
 * 说明：百度地图导航工具类
 * ==================================================
 **/
object BaiduMapNavigationUtils {

    /**
     * 导航到目的地
     * @param location 终点坐标点	可选	坐标类型参考通用参数：coord_type
     * @param coord_type 坐标类型，必选参数。
     * 示例：
     * coord_type= bd09ll
     * 允许的值为：
     * bd09ll（百度经纬度坐标）
     * bd09mc（百度墨卡托坐标）
     * gcj02（经国测局加密的坐标）
     * wgs84（gps获取的原始坐标）
     * 如开发者不传递正确的坐标类型参数，会导致地点坐标位置偏移
     *
     * @param query 终点名称	必选	query=天安门
     *
     * @param type  可选
     * BLK:躲避拥堵(自驾);
     * TIME:最短时间(自驾);
     * DIS:最短路程(自驾);
     * FEE:少走高速(自驾);
     * HIGHWAY:高速优先;
     * DEFAULT:推荐（自驾，地图app不选择偏好）;
     * 默认:地图app所选偏好
     * @param src 调用来源，规则：webapp.companyName.appName
     * @see https://lbsyun.baidu.com/faq/api?title=webapi/uri/andriod
     */
    fun navigate(
        context: Context,
        latitude: Double? = null,
        longitude: Double? = null,
        query: String? = null,
        type: String? = null,
    ) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val url =
                StringBuilder("baidumap://map/navi?src=${context.packageName}")
            if (latitude != null && longitude != null) {
                url.append("&destination=$latitude,$longitude")
            }
            if (!type.isNullOrBlank()) {
                url.append("&type:$type")
            }
            if (!query.isNullOrBlank()) {
                url.append("&query=$query")
            }
            intent.setData(Uri.parse(url.toString()))

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "请安装百度地图软件", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * ==================================================
 * Author：CL
 * 日期:2023/9/28
 * 说明：腾讯地图导航工具类
 * ==================================================
 **/
object TencentMapNavigationUtils {
    /**
     * 导航到目的地
     * @param from 起点名称
     * @param fromcoord 起点坐标，格式：lat,lng （纬度在前，经度在后，逗号分隔） 功能参数值：CurrentLocation ：使用定位点作为起点坐标
     * @param to 终点名称
     * @param tocoord 终点坐标 	tocoord=40.010024,116.392239
     * @param referer 请填写开发者key
     */
    fun navigate(
        context: Context,
        latitude: Double,
        longitude: Double,
        from: String? = null,
        fromcoord: String = "CurrentLocation",
        to: String? = null,
        referer: String = "OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77",
    ) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val url =
                StringBuilder("qqmap://map/routeplan?type=drive&fromcoord=${fromcoord}&tocoord=$latitude,$longitude&referer=$referer")
            if (!from.isNullOrBlank()) {
                url.append("$from:$from")
            }
            if (!to.isNullOrBlank()) {
                url.append("&to=$to")
            }
            intent.setData(Uri.parse(url.toString()))

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "请安装腾讯地图软件", Toast.LENGTH_SHORT).show()
        }
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