package com.hongwen.network

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.awaitResponse
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CoroutineCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }
        if (returnType !is ParameterizedType) {
//            throw IllegalStateException("Result return type must be parameterized as Result<DataResponse<?>> or Result<DataResponse<out ?>>")
            return null
        }
        var responseType = getParameterUpperBound(0, returnType)

        if (responseType !is ParameterizedType || getRawType(responseType) != Result::class.java) {
//            throw IllegalStateException("Response must be parameterized as Result<?> or Result<out ?>")
            return null
        }
        responseType = getParameterUpperBound(0,responseType)

        return CoroutineCallAdapter<DataResponse<Any?>>(
            responseType
        )
    }

    private class CoroutineCallAdapter<T>(
        private val type: Type,
    ) : CallAdapter<T, Call<T>> {

        override fun responseType(): Type {
            return type
        }

        override fun adapt(call: Call<T>): Call<T> {
            return call

        }
    }
}