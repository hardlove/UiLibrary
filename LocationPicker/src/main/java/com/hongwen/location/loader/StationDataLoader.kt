package com.hongwen.location.loader

import android.content.Context
import com.hongwen.location.callback.OnPickerListener
import com.hongwen.location.db.room.AppRoomDatabase
import com.hongwen.location.db.room.LocationDao
import com.hongwen.location.db.room.StationDao
import com.hongwen.location.model.Location
import com.hongwen.location.model.Station

/**
 * Created by chenlu at 2023/7/22 18:30
 */
class StationDataLoader(var context: Context) :OnPickerListener.IModelLoader<Station>{
    private var dao: StationDao = AppRoomDatabase.getInstance(context).stationDao()
    override fun getAllItems(): List<Station> {
        return dao.queryAll()
    }

    override fun getHotItems(): List<Station> {
        val items = arrayListOf<Station>()

        return items
    }

}