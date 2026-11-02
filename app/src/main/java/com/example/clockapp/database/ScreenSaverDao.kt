package com.example.clockapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.clockapp.entity.ScreenSaverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenSaverDao {

    @Query("SELECT * FROM screen_savers WHERE isActive = 1 LIMIT 1")
    fun observeActive(): Flow<ScreenSaverEntity?>

    @Query("SELECT * FROM screen_savers ORDER BY id")
    fun observeAll(): Flow<List<ScreenSaverEntity>>

    @Query("SELECT COUNT(*) FROM screen_savers")
    suspend fun count(): Int

    @Upsert
    suspend fun upsert(entity: ScreenSaverEntity): Long

    @Delete
    suspend fun delete(entity: ScreenSaverEntity)

    @Query("UPDATE screen_savers SET isActive = (id = :id)")
    suspend fun setActive(id: Long)
}