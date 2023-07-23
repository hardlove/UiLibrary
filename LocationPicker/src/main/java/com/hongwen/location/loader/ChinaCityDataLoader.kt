package com.hongwen.location.loader

import android.content.Context
import com.hongwen.location.callback.OnPickerListener
import com.hongwen.location.db.room.AppRoomDatabase
import com.hongwen.location.db.room.LocationDao
import com.hongwen.location.model.Location
import com.hongwen.location.utils.JsonUtils

/**
 * Created by chenlu at 2023/7/22 18:30
 */
class ChinaCityDataLoader(var context: Context) : OnPickerListener.IModelLoader<Location> {
    private var dao: LocationDao = AppRoomDatabase.getInstance(context).locationDao()
    override fun getAllItems(): List<Location> {
        val count = dao.getCount()
        if (count == 0) {
            JsonUtils.writeLocationJsonToDb(context, "china_city.json")
        }
        return dao.queryAll()
    }

    override fun getHotItems(): List<Location> {
        val items = arrayListOf<Location>()
        items.add(Location("北京", "北京", null, null))
        items.add(Location("北京", "北京", null, null))
        items.add(Location("北京", "北京", null, null))
        items.add(Location("北京", "北京", null, null))
        items.add(Location("北京", "北京", null, null))
        return items
    }

}