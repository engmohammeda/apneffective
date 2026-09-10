package com.engmohammeda.apndoctor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ApnEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apnDao(): ApnDao
}
