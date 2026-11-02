package com.example.clockapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.clockapp.entity.ReminderEntity
import com.example.clockapp.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ReminderDao {

    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    @Query("SELECT * FROM reminders ORDER BY time DESC")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders ORDER BY id DESC")
    suspend fun getAll(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ReminderEntity?

    @Update
    suspend fun update(reminders: ReminderEntity): Int

    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean): Int

    @Delete
    suspend fun delete(reminder: ReminderEntity): Int

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("UPDATE reminders SET isConfirmed = :bool WHERE id = :id")
    fun setConfirmed(id: Int, bool: Boolean) {
    }

    @Query("SELECT * FROM reminders WHERE date = :date")
    fun getByDate(date: LocalDate): Flow<List<ReminderEntity>>

}