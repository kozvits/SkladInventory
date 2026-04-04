package com.example.inventoryapp.di

import android.content.Context
import androidx.room.Room
import com.example.inventoryapp.data.db.AppDatabase
import com.example.inventoryapp.data.db.dao.InventoryGroupDao
import com.example.inventoryapp.data.db.dao.InventoryItemDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGroupDao(db: AppDatabase): InventoryGroupDao = db.inventoryGroupDao()

    @Provides
    fun provideItemDao(db: AppDatabase): InventoryItemDao = db.inventoryItemDao()
}
