package com.hongwen.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/data/{category}/{count}/{page}")

    suspend fun getData(

        @Path("category") category: String,

        @Path("count") count: Int,

        @Path("page") page: Int

    ): Response<DataResponse<String>>
}