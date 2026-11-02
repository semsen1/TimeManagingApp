package com.example.clockapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.entity.TimerType
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {

    @Query("SELECT * FROM timers WHERE type = :type ORDER BY createdAt DESC")
    fun observeByType(type: TimerType): Flow<List<TimerEntity>>

    @Query("SELECT * FROM timers WHERE id = :id")
    fun observeById(id: Long): Flow<TimerEntity?>

    @Query("SELECT * FROM timers WHERE id = :id")
    suspend fun getById(id: Long): TimerEntity?

    @Query("SELECT * FROM timers WHERE status = 'RUNNING'")
    fun observeRunning(): Flow<List<TimerEntity>>

    @Query("SELECT * FROM timers WHERE status = 'RUNNING'")
    suspend fun getRunning(): List<TimerEntity>

    @Insert
    suspend fun insert(timer: TimerEntity): Long

    @Update
    suspend fun update(timer: TimerEntity)

    @Delete
    suspend fun delete(timer: TimerEntity)

    @Query("DELETE FROM timers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM timers WHERE status = :status ORDER BY createdAt")
    fun observeByStatus(status: TimerStatus): Flow<List<TimerEntity>>

    @Query("SELECT * FROM timers WHERE status = :status")
    suspend fun getByStatus(status: TimerStatus): List<TimerEntity>

    @Query("SELECT * FROM timers")
    fun observeAll(): Flow<List<TimerEntity>>

    @Query("SELECT * FROM timers")
    suspend fun getAll(): List<TimerEntity>

    @Query("UPDATE timers SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query(
        """
    UPDATE timers
    SET accumulatedMs = MIN(targetDurationMs, accumulatedMs + MAX(0, :now - startedAtMs)),
        startedAtMs = NULL,
        status = 'PAUSED'
    WHERE type = 'TIMER' AND status = 'RUNNING' AND startedAtMs IS NOT NULL
    """
    )
    suspend fun pauseTimers(now: Long)

    @Query(
        """
    UPDATE timers
    SET accumulatedMs = accumulatedMs + MAX(0, :now - startedAtMs),
        startedAtMs = NULL,
        status = 'PAUSED'
    WHERE type = 'STOPWATCH' AND status = 'RUNNING' AND startedAtMs IS NOT NULL
    """
    )
    suspend fun pauseStopwatches(now: Long)

    @Query(
        """
    UPDATE timers 
    SET title = :title, targetDurationMs = :durationMs, deleteOnFinish = :deleteOnFinish 
    WHERE id = :id
    """
    )
    suspend fun updateSettings(id: Long, title: String, durationMs: Long, deleteOnFinish: Boolean)

}