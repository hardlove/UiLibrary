package com.hongwen.network

const val URL = "https://www.baidu.com"

object AdvApiManager :
    ApiService by ServiceFactory.getService(url = URL, clazz = ApiService::class.java) {

    suspend fun tet() {
        this.getData("", 3, 1).map(success = { result: Result.Success<String> ->


            }, failed = { result: Result.Error ->
            })


        this.getData("", 1, 2).map(success = { it: String ->


            }, failed = { it: Code ->

            })
        val result: Result<String> = this.getData("", 1, 2).toResult()
        when (result) {
            is Result.Success -> {

            }

            is Result.Error -> {}

        }

    }

}





