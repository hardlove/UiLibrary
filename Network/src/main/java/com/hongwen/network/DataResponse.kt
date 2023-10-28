package com.hongwen.network

import retrofit2.Response

data class DataResponse<out T>(val code: Int, val data: T, val msg: String) {
    fun isOk(): Boolean {
        return code == 0 || code == 200
    }
}

fun <T> Response<DataResponse<T>>.map(
    success: (result: Result.Success<T>) -> Unit,
    failed: (result: Result.Error) -> Unit,
) {
    if (this.isSuccessful) {
        val dataResponse = this.body()
        if (dataResponse != null) {
            if (dataResponse.code == 0 || dataResponse.code == 200) {
                success.invoke(Result.Success(dataResponse.data))
            } else {
                failed.invoke(Result.Error(Code.of(dataResponse.code, dataResponse.msg)))
            }
        } else {
            failed.invoke(Result.Error(Code.of(0, "请求响应为空")))
        }
    }
    failed.invoke(Result.Error(Code.of(this.code(), this.message())))
}

fun <T> Response<DataResponse<T>>.map(
    success: (result: T) -> Unit,
    failed: (result: Code) -> Unit,
) {
    if (this.isSuccessful) {
        val dataResponse = this.body()
        if (dataResponse != null) {
            if (dataResponse.code == 0 || dataResponse.code == 200) {
                success.invoke(dataResponse.data)
            } else {
                failed.invoke(Code.of(dataResponse.code, dataResponse.msg))
            }
        } else {
            failed.invoke(Code.of(0, "请求响应为空"))
        }
    }
    failed.invoke(Code.of(this.code(), this.message()))
}

fun <T> Response<DataResponse<T>>.toResult(): Result<T> {
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

