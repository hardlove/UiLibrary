package com.hongwen.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    companion object{
        const val URL = "http://browser.51star.top:8080"
    }
    @GET("/LR_WaterMark/AppData/GetAllAdList")
    suspend fun getAdvList(
        @Query("channel") channel: String,
        @Query("versionCode") versionCode: String,
        @Query("appName") appName: String,
    ): DataResponse<List<AdvertModel>>
}