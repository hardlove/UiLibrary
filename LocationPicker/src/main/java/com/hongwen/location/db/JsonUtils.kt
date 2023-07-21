package com.hongwen.location.db

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hongwen.location.db.room.AppRoomDatabase
import com.hongwen.location.model.Location
import com.hongwen.location.model.Station
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by chenlu at 2023/7/21 10:07
 */
object JsonUtils {
    inline fun <reified T> jsonStringToList(context: Context, fileName: String): List<T> {
        val json = loadJsonFromAssets(context, fileName)

        val items = GsonBuilder().setPrettyPrinting().create()
            .fromJson<List<T>>(json, object : TypeToken<List<T>>() {}.type)

        Log.d("Carlos","获取到JSON数据："+GsonBuilder().setPrettyPrinting().create().toJson(items))
        return items
    }

    fun loadJsonFromAssets(context: Context, fileName: String): String {
        val inputStream = context.assets.open(fileName)
        val inputStreamReader = InputStreamReader(inputStream)
        val reader = BufferedReader(inputStreamReader)

        val sb = StringBuffer()
        var line: String? = reader.readLine()
        while (line != null) {
            sb.append(line)
            line = reader.readLine()

        }
        return sb.toString()
    }

    /**
     * 将文件 assets/train_station.json 写入到数据库
     */
     fun writeStationJsonToDb(context: Context, fileName: String) {
        val items = jsonStringToList<Station>(context, fileName)
        AppRoomDatabase.getInstance(context).stationDao().insert(items)
    }

    /**
     * 将文件 assets/china_city.json 写入到数据库
     */
    fun writeLocationJsonToDb(context: Context, fileName: String) {
        val items = jsonStringToList<Location>(context, fileName)
        AppRoomDatabase.getInstance(context).locationDao().insert(items)
    }

}