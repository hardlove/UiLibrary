//package com.hongwen.network
//
//import com.google.gson.Gson
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.withContext
//
//const val URL = "http://browser.51star.top:8080"
//
//
//fun main(args: Array<String>) {
//    runBlocking {
//        val apiService = ServiceFactory.getService(url = URL, clazz = ApiService::class.java)
//
//        // 1
//        apiService.getAdvList("huawei", "10201", "com.nanjingwx.train").transformResult(success = {
//            Thread.sleep(1000 * 5)
//            println("11111 success:" + Gson().toJson(it.data))
//            Thread.sleep(1000 * 2)
//
//        }, fail = {
//            println("1111 fail:" + it.exception.msg)
//            Thread.sleep(1000 * 2)
//        })
//
//
//        // 2
//        apiService.getAdvList("huawei", "10201", "com.nanjingwx.train").transform(success = {
//            println("2222 success:" + Gson().toJson(it))
//            Thread.sleep(1000 * 2)
//        }, fail = {
//            println("2222 fail:" + it.msg)
//            Thread.sleep(1000 * 2)
//        })
//        // 3
//        apiService.getAdvList("huawei", "10201", "com.nanjingwx.train")
//            .transform()
//            .let {
//                return@let withContext(Dispatchers.Main) {
////                    it.onSuccess {
////
////                    }
////                        .onError {
////
////                        }
//                    it
//                }
//            }
//            .executeOnThread(Dispatchers.Default) {
//                println("3333 executeOnThread:" + "thread:" + Thread.currentThread())
//
//            }
//            .onError(Dispatchers.IO) {
//                println("3333 onError:" + Gson().toJson(it) + "thread:" + Thread.currentThread())
//            }
//            .onSuccess(Dispatchers.Unconfined) {
//                println("3333 onSuccess:" + Gson().toJson(it) + "thread:" + Thread.currentThread())
//            }
//
//        val result = apiService.getAdvList("huawei", "10201", "com.nanjingwx.train")
//            .transform()
//        when (result) {
//            is Result.Success -> {
//                println("3333 is Success:" + Gson().toJson(result.data) + "thread:" + Thread.currentThread())
//            }
//
//            is Result.Error -> {
//                println("3333 is Error:" + Gson().toJson(result) + "thread:" + Thread.currentThread())
//            }
//
//        }
//
//        result.transform {
//            println("============finish")
//        }
//
//
//    }
//}