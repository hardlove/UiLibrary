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
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item : Station)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items : List<Station>)


    @Query("select * from train_station")
    fun queryAll(): MutableList<Station>

    //@Query("select * from train_station where name like '% '||:keyword|| '%' or pinyin like '%'||:keyword|| '%'")
    //fun search(keyword: String): MutableList<Station>

    @Query("select * from train_station where name like '% '||:keyword|| '%'")
    fun search(keyword: String): MutableList<Station>


    @Query("SELECT COUNT(*) FROM china_city")
    fun getCount(): Int
}