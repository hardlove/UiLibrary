//package com.hongwen.location.model
//
//import android.text.TextUtils
//import java.util.*
//import java.util.regex.Pattern
//
///**
// * Created by chenlu at 2023/7/22 18:02
// */
//interface IModel {
//    fun getName(): String
//    fun getPingYin(): String
//
//    /***
//     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
//     * @return
//     */
//    fun getSection(): String {
//        return if (TextUtils.isEmpty(getPingYin())) {
//            "#"
//        } else {
//            val c: String = getPingYin().substring(0, 1)
//            val p = Pattern.compile("[a-zA-Z]")
//            val m = p.matcher(c)
//            if (m.matches()) {
//                c.uppercase(Locale.getDefault())
//            } else if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热")) {
//                getPingYin()
//            } else {
//                "#"
//            }
//        }
//    }
//}