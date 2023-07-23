package com.hongwen.location.model

/**
 * Created by chenlu at 2023/7/22 16:35
 * 数据类型
 */
sealed class LocationType {
    /*城市*/
   object ChinaCity:LocationType()
    /*火车站点*/
    object TrainStation:LocationType()
    /*自定义数据*/
    object CustomLocation:LocationType()
}