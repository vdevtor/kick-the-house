package com.vitorthemyth.persistence

import androidx.room.*
import com.vitorthemyth.persistence.models.TimeEntity

@Dao
interface TeamDao {


    // Time queries
    @Query("SELECT * FROM times")
    suspend fun getAllTimes(): List<TimeEntity>

    @Query("SELECT * FROM times WHERE id = :timeId")
    suspend fun getTimeById(timeId: Int): TimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTime(time: TimeEntity)

    @Update
    suspend fun updateTime(time: TimeEntity)

    @Delete
    suspend fun deleteTime(time: TimeEntity)

    @Query("DELETE FROM times")
    suspend fun deleteAllTimes()

}