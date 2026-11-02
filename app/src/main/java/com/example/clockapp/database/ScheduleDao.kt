package com.example.clockapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.clockapp.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
data class ScheduleDateCount(
    val date: LocalDate,
    val count: Int
)

data class AlarmYearBounds(
    val minYear: Int?,
    val maxYear: Int?,
)
@Dao
interface ScheduleDao {

    @Insert
    suspend fun insert(schedule: ScheduleEntity): Long

    @Query("SELECT * FROM schedules ORDER BY timeStart DESC")
    fun observeAll(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules ORDER BY id DESC")
    suspend fun getAll(): List<ScheduleEntity>

    @Query("SELECT * FROM schedules WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ScheduleEntity?

    //query for schedule by date start
    @Query(
        """
    SELECT * FROM schedules
    WHERE (dateStart IS NULL OR dateStart <= :date)
      AND (dateEnd IS NULL OR dateEnd >= :date)
      AND instr(',' || daysOfWeek || ',', ',' || :dayOfWeek || ',') > 0
      ORDER BY timeStart  ASC 
    """
    )
    fun getByDate(date: LocalDate, dayOfWeek: String): Flow<List<ScheduleEntity>>

    //query for schedule by date end


    //query for schedule count by dates start
    @Query(
        """
        WITH RECURSIVE calendar(day) AS (
            SELECT :startDate
            WHERE :startDate <= :endDate

            UNION ALL

            SELECT date(day, '+1 day')
            FROM calendar
            WHERE day < :endDate
        )
        SELECT
            c.day AS date,
            COUNT(s.rowid) AS count
        FROM calendar AS c
        LEFT JOIN schedules AS s
            ON (s.dateStart IS NULL OR s.dateStart <= c.day)
            AND (s.dateEnd IS NULL OR s.dateEnd >= c.day)
            AND instr(
                ',' || s.daysOfWeek || ',',
                ',' ||
                CASE strftime('%w', c.day)
                    WHEN '0' THEN 'SUNDAY'
                    WHEN '1' THEN 'MONDAY'
                    WHEN '2' THEN 'TUESDAY'
                    WHEN '3' THEN 'WEDNESDAY'
                    WHEN '4' THEN 'THURSDAY'
                    WHEN '5' THEN 'FRIDAY'
                    WHEN '6' THEN 'SATURDAY'
                END
                || ','
            ) > 0
        GROUP BY c.day
        ORDER BY c.day
        """
    )
    fun getCountsByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<ScheduleDateCount>>

    //query for schedule count by dates end


    //query for schedule min-max year start
    @Query(
        """
        SELECT
            MIN(CAST(strftime('%Y', `date`) AS INTEGER)) AS minYear,
            MAX(CAST(strftime('%Y', `date`) AS INTEGER)) AS maxYear
        FROM alarms
        """
    )
    fun observeYearBounds(): Flow<AlarmYearBounds>

    //query for schedule min-max year end
    @Update
    suspend fun update(schedule: ScheduleEntity): Int

    @Query("UPDATE schedules SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean): Int

    @Delete
    suspend fun delete(schedule: ScheduleEntity): Int

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteById(id: Int): Int
}