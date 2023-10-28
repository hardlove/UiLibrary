package com.hongwen.network

/**
 * ==================================================
 * Author：CL
 * 日期:2023/10/27
 * 说明：错误码
 * ==================================================
 **/
data class Code(val code: Int, val msg: String) {
    companion object {
        fun of(code: Int, message: String): Code {
            return Code(code, message)
        }

        val OK = Code(1, "成功")
        val Net = Code(2, "网络出错")
    }

}
