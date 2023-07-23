package com.hongwen.location.loader

import android.content.Context
import com.hongwen.location.callback.OnPickerListener
import com.hongwen.location.db.room.AppRoomDatabase
import com.hongwen.location.db.room.LocationDao
import com.hongwen.location.db.room.StationDao
import com.hongwen.location.model.Location
import com.hongwen.location.model.Station
import com.hongwen.location.utils.JsonUtils

/**
 * Created by chenlu at 2023/7/22 18:30
 */
class StationDataLoader(var context: Context) :OnPickerListener.IModelLoader<Station>{
    private var dao: StationDao = AppRoomDatabase.getInstance(context).stationDao()
    override fun getAllItems(): List<Station> {
        val count = dao.getCount()
        if (count == 0) {
            JsonUtils.writeStationJsonToDb(context, "train_station.json")
        }
        return dao.queryAll()
    }

    override fun getHotItems(): List<Station> {
        val items = arrayListOf<Station>()
        items.add(Station("北京", "北京", null))
        items.add(Station("北京", "北京", null))
        items.add(Station("北京", "北京", null))
        items.add(Station("北京", "北京", null))
        items.add(Station("北京", "北京", null))
        return items
    }

}