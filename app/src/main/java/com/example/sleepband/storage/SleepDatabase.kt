package com.example.sleepband.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sleepband.storage.dao.SleepSessionDao
import com.example.sleepband.storage.entity.SleepSession

@Database(entities = [SleepSession::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {
    abstract fun sleepSessionDao(): SleepSessionDao
}
