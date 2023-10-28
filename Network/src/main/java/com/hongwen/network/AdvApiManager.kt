package com.hongwen.network

import kotlinx.coroutines.Dispatchers

const val URL = "https://www.baidu.com"

object AdvApiManager :
    ApiService by ServiceFactory.getService(url = URL, clazz = ApiService::class.java) {

    suspend fun tet() {
        this.getData("", 3, 1).transformResult(success = {


        }, fail = {
        })


        this.getData("", 1, 2).transform(success = {


        })
        this.getData("", 1, 2)
            .transformToResult()
            .toThread(Dispatchers.Main)
            .onError {

            }
            .onSuccess {

            }
        val result: Result<UserInfo> = this.getData("", 1, 2).transformToResult()
        when (result) {
            is Result.Success -> {

            }

            is Result.Error -> {}

        }

        result.transform {

        }

        this.getData("", 1, 3).transform(success = {

        })
    }

}





