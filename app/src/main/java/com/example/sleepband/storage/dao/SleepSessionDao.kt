package com.example.sleepband.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sleepband.storage.entity.SleepSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepSessionDao {
    @Query("SELECT * FROM sleep_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SleepSession>>

    @Insert
    suspend fun insertSession(session: SleepSession)

    @Query("SELECT * FROM sleep_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): SleepSession?
}
