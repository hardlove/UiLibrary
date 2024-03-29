package com.hongwen.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ==================================================
 * Author：CL
 * 日期:2023/10/28
 * 说明：ApiService工厂构造器
 * ==================================================
 **/
object ServiceFactory {
    private const val DEFAULT_TIMEOUT = 30000
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .addNetworkInterceptor { chain ->
                chain.proceed(chain.request())
            }
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY // 设置日志级别
//            })
            .addNetworkInterceptor { chain ->
                chain.proceed(chain.request())
            }.build()
    }

    private val services by lazy { mutableMapOf<String, Any>() }
    private fun createRetrofit(client: OkHttpClient = okHttpClient, url: String): Retrofit {
        return Retrofit.Builder().client(client).baseUrl(url)
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private fun <T : Any> getServiceKey(url: String, clazz: Class<T>) =
        "${url}_${clazz.canonicalName}"

    @JvmStatic
    fun <T : Any> getService(client: OkHttpClient = okHttpClient, url: String, clazz: Class<T>): T {
        val key = getServiceKey(url, clazz)
        @Suppress("UNCHECKED_CAST") var service = services[key] as? T
        if (service == null) {
            service = createRetrofit(client, url).create(clazz).also { services[key] = it }
        }
        return service as T
    }

    @JvmStatic
    fun <T : Any> getService(builder: Retrofit.Builder, url: String, clazz: Class<T>): T {
        val key = getServiceKey(url, clazz)
        @Suppress("UNCHECKED_CAST") var service = services[key] as? T
        if (service == null) {
            service = builder.baseUrl(url).build().create(clazz).also { services[key] = it }
        }
        return service as T
    }

}

