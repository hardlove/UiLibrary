package com.hongwen.network

/*封装请求结果*/
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Code) : Result<Nothing>()
}

/*定义扩展函数,用户处理成功情况*/
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

/*定义扩展函数,用户处理失败情况*/
inline fun  <T> Result<T>.onError(action: (Code) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(exception)
    }
    return this
}
