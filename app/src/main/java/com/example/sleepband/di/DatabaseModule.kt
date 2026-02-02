package com.example.sleepband.di

import android.content.Context
import androidx.room.Room
import com.example.sleepband.core.Constants
import com.example.sleepband.storage.SleepDatabase
import com.example.sleepband.storage.dao.SleepSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SleepDatabase {
        return Room.databaseBuilder(
            context,
            SleepDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideSleepSessionDao(database: SleepDatabase): SleepSessionDao {
        return database.sleepSessionDao()
    }
}
