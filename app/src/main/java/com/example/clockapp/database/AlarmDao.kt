package com.example.clockapp.database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.clockapp.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert
    suspend fun insert(alarm: AlarmEntity): Long

    @Query("SELECT * FROM alarms ORDER BY time DESC")
    fun observeAll(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms ORDER BY id DESC")
    suspend fun getAll(): List<AlarmEntity>

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): AlarmEntity?

    @Update
    suspend fun update(alarm: AlarmEntity): Int

    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean): Int

    @Delete
    suspend fun delete(alarm: AlarmEntity): Int

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteById(id: Int): Int
}