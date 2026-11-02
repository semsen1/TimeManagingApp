package com.example.clockapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.clockapp.entity.SettingEntity
import com.example.clockapp.entity.SettingType
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SettingDao {

    @Query("SELECT * FROM settings")
    abstract fun observeAll(): Flow<List<SettingEntity>>

    @Query("SELECT * FROM settings WHERE id = :id")
    abstract fun observeById(id: String): Flow<SettingEntity?>

    @Query("SELECT * FROM settings WHERE id = :id")
    abstract suspend fun getById(id: String): SettingEntity?

    @Query("UPDATE settings SET value = :value WHERE id = :id")
    abstract suspend fun setValue(id: String, value: String?)

    @Query("UPDATE settings SET value = NULL WHERE id = :id")
    abstract suspend fun resetValue(id: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertIfAbsent(items: List<SettingEntity>)

    @Query("UPDATE settings SET label = :label, type = :type, defaultValue = :defaultValue WHERE id = :id")
    protected abstract suspend fun updateMeta(id: String, label: Int, type: SettingType, defaultValue: String)


    @Transaction
    open suspend fun syncDefaults(defaults: List<SettingEntity> = SettingEntity.defaults) {
        insertIfAbsent(defaults)
        defaults.forEach { updateMeta(it.id, it.label, it.type, it.defaultValue) }
    }
}