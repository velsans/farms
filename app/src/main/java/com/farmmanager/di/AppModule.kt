package com.farmmanager.di

import android.content.Context
import com.farmmanager.data.FarmDao
import com.farmmanager.data.FarmDatabase
import com.farmmanager.data.FarmRepository
import com.farmmanager.export.ExcelManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFarmDatabase(@ApplicationContext context: Context): FarmDatabase = FarmDatabase.get(context)

    @Provides
    fun provideFarmDao(database: FarmDatabase): FarmDao = database.farmDao()

    @Provides
    @Singleton
    fun provideFarmRepository(dao: FarmDao): FarmRepository = FarmRepository(dao)

    @Provides
    @Singleton
    fun provideExcelManager(): ExcelManager = ExcelManager()
}
