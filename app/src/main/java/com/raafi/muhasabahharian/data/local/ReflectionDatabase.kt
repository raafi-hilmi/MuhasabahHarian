package com.raafi.muhasabahharian.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raafi.muhasabahharian.data.local.dao.MuhasabahDao
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity

@Database(entities = [MuhasabahEntity::class], version = 1, exportSchema = false)
abstract class ReflectionDatabase : RoomDatabase() {
    abstract fun muhasabahDao(): MuhasabahDao
}