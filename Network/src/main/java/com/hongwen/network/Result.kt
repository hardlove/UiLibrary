package com.hongwen.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*封装请求结果*/
sealed class Result<out T> {


    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Code) : Result<Nothing>()
}

/*定义扩展函数,用户处理成功情况*/
suspend inline fun <T> Result<T>.onSuccess(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline block: suspend (T) -> Unit,
): Result<T> {
    if (this is Result.Success) {
        return withContext(dispatcher) {
            block(data)
            this@onSuccess
        }
    }
    return this
}

/*定义扩展函数,用户处理失败情况*/
suspend inline fun <T> Result<T>.onError(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline block: suspend (Code) -> Unit,
): Result<T> {
    if (this is Result.Error) {
        return withContext(dispatcher) {
            block(exception)
            this@onError
        }
    }
    return this
}

suspend inline fun <T> T.executeOnThread(
    dispatcher: CoroutineDispatcher,
    crossinline block: suspend () -> Unit,
): T {
    withContext(dispatcher) {
        block()
    }
    return this
}

fun <T> T.toIoThread(): T {

    return this

}