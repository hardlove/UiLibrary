package com.hongwen.location.model


/**
 * ==================================================
 * Author：CL
 * 日期:2023/7/14
 * 说明：定位状态
 * ==================================================
 **/
sealed class LocateState {
    object INIT : LocateState()
    object LOCATING : LocateState()
    object SUCCESS : LocateState()
    object FAILURE : LocateState()
}

