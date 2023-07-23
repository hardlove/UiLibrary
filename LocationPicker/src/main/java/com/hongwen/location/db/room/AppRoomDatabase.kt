package com.hongwen.location.db.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hongwen.location.model.Location
import com.hongwen.location.model.Station


/**
 * Created by chenlu at 2023/7/21 9:44
 */
@Database(entities = [Station::class,Location::class], version = 2, exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {


    companion object {
        @Volatile
        private var instance: AppRoomDatabase? = null
        @JvmStatic
        fun getInstance(context: Context): AppRoomDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    AppRoomDatabase::class.java,
                    "location_picker_v1.db"
                ).fallbackToDestructiveMigration().build()
            }
        }


    }

    abstract fun stationDao(): StationDao
    abstract fun locationDao(): LocationDao

}