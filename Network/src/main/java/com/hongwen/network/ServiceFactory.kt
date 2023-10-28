package com.hongwen.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ==================================================
 * Author：CL
 * 日期:2023/10/28
 * 说明：ApiService工厂构造器
 * ==================================================
 **/
object ServiceFactory {
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                chain.proceed(chain.request())
            }
            .build()
    }

    private val services by lazy { mutableMapOf<String, Any>() }
    private fun createRetrofit(client: OkHttpClient = okHttpClient, url: String): Retrofit {
        return Retrofit
            .Builder()
            .client(client)
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun <T : Any> getServiceKey(url: String, clazz: Class<T>) =
        "${url}_${clazz.canonicalName}"

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getService(client: OkHttpClient = okHttpClient, url: String, clazz: Class<T>): T {
        val key = getServiceKey(url, clazz)
        var service = services[key]
        if (service == null) {
            service = createRetrofit(client, url).create(clazz).also { services[key] = it }
        }
        return service as T
    }

}

