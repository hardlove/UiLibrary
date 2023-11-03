package com.hongwen.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/LR_WaterMark/AppData/GetAllAdList")
    suspend fun getAdvList(
        @Query("channel") channel: String,
        @Query("versionCode") versionCode: String,
        @Query("appName") appName: String,
    ): Response<DataResponse<List<AdvertModel>>>
}