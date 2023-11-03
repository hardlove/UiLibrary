package com.hongwen.network

import com.google.gson.Gson

/**
 * 广告控制类
 */
class AdvertModel {
    var id = 0
    var aid: String? = null //	String	广告id
    var advert_location: String? =
        null //	open开屏、banner主页横幅、finish完成页、cancel取消付款、quit退出、float浮标、button小图标、Lock视频解锁广告、active激活广告、chapin1普通插屏、neirong1内容横幅、apptab（tab页）、flow（信息流）
    var advert_title: String? = null //	广告标题
    var update_time: Long = 0 //	long	修改时间
    var is_open = 0 //	int	是否开启
    var browser_open = 0 //	int	打开方式 0-应用内，1-外部浏览器
    var width = 0 //	广告宽
    var height = 0 //	广告高度
    var advert_type =
        0 //	int	广告类型	0-html；1:应用市场apk下载;2-apk（文件）下载；3-微信小程序；4-展示图； 5-广州图霸；6-广点通sdk,7-推啊；8-淘宝商品推广,9-穿山甲
    var advert_param_0: String? = null //	String	参数1	0/1/2/3/4/8：为图片路径 6：广点通id 7：图片路径 5：图片路径，9：应用id
    var advert_param_1: String? =
        null //	String	参数2	0-页面url;2-apk的url；3：path；1-apk包名;5-图片展示回调；6-channel 7-html的url 8：appKey,9-广告位id
    var advert_param_2: String? =
        null //	String	参数3	2-文件md5；3-userName（小程序id）；1-应用市场包名列表；8：secret 5：html的url；
    var advert_param_3: String? = null //	String	参数4	8:pid   5：html打开的回调,;3-应用id
    var advert_param_4: String? = null //	String	参数5	8:AdzoneId
    var advert_param_5: String? = null //	String	参数6	8:FavoritesId
    var advert_param_6: String? = null //	String	参数7	8:unionId
    var advert_param_7: String? = null //	String	参数8	8:subPid


    override fun toString(): String {
        return Gson().toJson(this)
    }
}