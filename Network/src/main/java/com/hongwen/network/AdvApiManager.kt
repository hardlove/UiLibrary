package com.hongwen.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

const val URL = "https://www.baidu.com"

object AdvApiManager :
    ApiService by ServiceFactory.getService(url = URL, clazz = ApiService::class.java) {

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val apiService = ServiceFactory.getService(url = URL, clazz = ApiService::class.java)

            // 1
            apiService.getData("", 3, 1).transformResult(success = {


            }, fail = {

            })


            // 2
            apiService.getData("", 1, 2).transform(success = {


            })


            // 3
            apiService.getData("", 1, 2)
                .transformToResult()
                .executeOnThread(Dispatchers.Main) {

                }
                .onError(Dispatchers.Main) {

                }
                .onSuccess(Dispatchers.Main) {

                }
            val result: Result<UserInfo> = apiService.getData("", 1, 2).transformToResult()
            when (result) {
                is Result.Success -> {

                }

                is Result.Error -> {}

            }

            result.transform {

            }


        }
    }

}





