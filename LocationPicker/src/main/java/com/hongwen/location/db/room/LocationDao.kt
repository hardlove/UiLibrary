package com.hongwen.location.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hongwen.location.model.Location
import com.hongwen.location.model.Station

/**
 * Created by chenlu at 2023/7/21 9:41
 */
@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Location)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Location>)

    @Query("select * from china_city")
    fun queryAll(): MutableList<Location>

    @Query("SELECT * FROM china_city WHERE name LIKE '%' || :keyword || '%' or pinyin LIKE '%' || :keyword || '%'")
    fun search(keyword: String): MutableList<Location>

    @Query("SELECT COUNT(*) FROM china_city")
    fun getCount(): Int
}