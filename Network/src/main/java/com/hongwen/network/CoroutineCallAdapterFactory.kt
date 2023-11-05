package com.hongwen.network

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CoroutineCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (CallAdapter.Factory.getRawType(returnType) != Deferred::class.java) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException("Deferred return type must be parameterized as Deferred<DataResponse<Foo>> or Deferred<DataResponse<out Foo>>")
        }
        val responseType = CallAdapter.Factory.getParameterUpperBound(0, returnType)

        if (responseType !is ParameterizedType || CallAdapter.Factory.getRawType(responseType) != DataResponse::class.java) {
            throw IllegalStateException("Response must be parameterized as DataResponse<Foo> or DataResponse<out Foo>")
        }

        val dataType = CallAdapter.Factory.getParameterUpperBound(0, responseType)

        return CoroutineCallAdapter<DataResponse<Any?>>(
            dataType
        )
    }

    private class CoroutineCallAdapter<T>(
        private val dataType: Type,
    ) : CallAdapter<T, Deferred<T>> {

        override fun responseType(): Type {
            return dataType
        }

        override fun adapt(call: Call<T>): Deferred<T> {
            val deferred = CompletableDeferred<T>()
            call.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        response.body().let {
                            if (it != null) {
                                deferred.complete(it)
                            } else {
                                deferred.completeExceptionally(IllegalStateException("Response body is null"))
                            }
                        }
                    } else {
                        deferred.completeExceptionally(HttpException(response))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }
            })
            return deferred
        }
    }
}