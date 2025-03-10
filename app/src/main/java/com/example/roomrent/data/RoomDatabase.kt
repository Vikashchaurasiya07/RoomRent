package com.example.roomrent.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomEntity::class], version = 1, exportSchema = false)
abstract class RoomDatabaseApp : RoomDatabase() {
    abstract fun roomDao(): RoomDao
}
