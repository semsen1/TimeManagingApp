package com.example.clockapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.clockapp.entity.WidgetSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetSettingDao {

    @Query("SELECT * FROM widget_settings")
    fun observeAll(): Flow<List<WidgetSettingEntity>>

    @Query("SELECT * FROM widget_settings WHERE appWidgetId = :appWidgetId")
    suspend fun get(appWidgetId: Int): WidgetSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: WidgetSettingEntity)

    @Query("DELETE FROM widget_settings WHERE appWidgetId = :appWidgetId")
    suspend fun delete(appWidgetId: Int)

    @Query("SELECT timeZoneId FROM widget_settings WHERE appWidgetId = :appWidgetId")
    fun observeZone(appWidgetId: Int): Flow<String?>
}