package com.hongwen.network

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response

object ApiServiceImpl :ApiService by ServiceFactory.getService(url = ApiService.URL, clazz = ApiService::class.java) {

}
fun main(args: Array<String>) {
    runBlocking {
        val apiService = ApiServiceImpl

        flow { emit(apiService.getAdvList("huawei", "10201", "com.nanjingwx.train")) }
            .onStart {  }
            .onEach {  }
            .onCompletion {  }
            .catch {

            }

        // 1
        apiService.getAdvList("huawei", "10201", "com.nanjingwx.train")
            .transform {
                Response.success(it)
            }
            .transformResult(success = {
                Thread.sleep(1000 * 5)
                println("11111 success:" + Gson().toJson(it.data))
                Thread.sleep(1000 * 2)

            }, fail = {
                println("1111 fail:" + it.exception.msg)
                Thread.sleep(1000 * 2)
            })


        // 2
        apiService.getAdvList("huawei", "10201", "com.nanjingwx.train")
            .transform {
                Response.success(it)
            }
            .transform(success = {
                println("2222 success:" + Gson().toJson(it))
                Thread.sleep(1000 * 2)
            }, fail = {
                println("2222 fail:" + it.msg)
                Thread.sleep(1000 * 2)
            })
        // 3
        apiService.getAdvList("huawei", "10201", "com.nanjingwx.train")
            .transform {
                Response.success(it)
            }
            .transform()
            .let {
                return@let withContext(Dispatchers.IO) {
//                    it.onSuccess {
//
//                    }
//                        .onError {
//
//                        }
                    it
                }
            }
            .executeOnThread(Dispatchers.Default) {
                println("3333 executeOnThread:" + "thread:" + Thread.currentThread())

            }
            .onError(Dispatchers.IO) {
                println("3333 onError:" + Gson().toJson(it) + "thread:" + Thread.currentThread())
            }
            .onSuccess(Dispatchers.Unconfined) {
                println("3333 onSuccess:" + Gson().toJson(it) + "thread:" + Thread.currentThread())
            }

        val result = runCatching {
            apiService.getAdvList("huawei", "10201", "com.nanjingwx.train")
        }
        val orElse = result.getOrElse {
            "dfasfds"
        }
        println(" =====>" + orElse)
    }


}
