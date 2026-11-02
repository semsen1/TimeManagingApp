package com.example.clockapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.clockapp.entity.WallpaperEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM wallpaper_blocks ORDER BY zIndex")
    fun observeAll(): Flow<List<WallpaperEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaults(blocks: List<WallpaperEntity>)

    @Upsert
    suspend fun resetAll(blocks: List<WallpaperEntity>)

    @Query("UPDATE wallpaper_blocks SET isVisible = :visible WHERE id = :id")
    suspend fun setVisible(id: String, visible: Boolean)

    @Query("UPDATE wallpaper_blocks SET x = :x, y = :y, width = :width, height = :height WHERE id = :id")
    suspend fun updateLayout(id: String, x: Float, y: Float, width: Float, height: Float)

    @Query("UPDATE wallpaper_blocks SET timeZone = :zoneId WHERE id = :id AND type = 'CLOCK'")
    suspend fun setTimeZone(id: String, zoneId: String?)
}