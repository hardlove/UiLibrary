package com.hongwen.network

import retrofit2.Response

data class DataResponse<out T>(val code: Int, val data: T, val msg: String) {
    fun isOk(): Boolean {
        return code == 0 || code == 200
    }
}

inline fun <T, R> T.transform(action: (T) -> R): R {
    return action.invoke(this)
}

fun <T> Response<DataResponse<T>>.transformResult(
    success: (result: Result.Success<T>) -> Unit,
    fail: (result: Result.Error) -> Unit = {},
) {
    if (this.isSuccessful) {
        val dataResponse = this.body()
        if (dataResponse != null) {
            if (dataResponse.isOk()) {
                success.invoke(Result.Success(dataResponse.data))
            } else {
                fail.invoke(Result.Error(Code.of(dataResponse.code, dataResponse.msg)))
            }
        } else {
            fail.invoke(Result.Error(Code.of(0, "请求响应为空")))
        }
    }
    fail.invoke(Result.Error(Code.of(this.code(), this.message())))
}

fun <T> Response<DataResponse<T>>.transform(
    success: (result: T) -> Unit,
    fail: (result: Code) -> Unit = {},
) {
    if (this.isSuccessful) {
        val dataResponse = this.body()
        if (dataResponse != null) {
            if (dataResponse.isOk()) {
                success.invoke(dataResponse.data)
            } else {
                fail.invoke(Code.of(dataResponse.code, dataResponse.msg))
            }
        } else {
            fail.invoke(Code.of(0, "请求响应为空"))
        }
    }
    fail.invoke(Code.of(this.code(), this.message()))


}

fun <T> Response<DataResponse<T>>.transformToResult(): Result<T> {
    if (this.isSuccessful) {
        val dataResponse = this.body()
        return if (dataResponse != null) {
            if (dataResponse.isOk()) {
                Result.Success(dataResponse.data)
            } else {
                Result.Error(Code.of(dataResponse.code, dataResponse.msg))
            }
        } else {
            Result.Error(Code.of(0, "请求响应为空"))
        }
    }
    return Result.Error(Code.of(this.code(), this.message()))
}


