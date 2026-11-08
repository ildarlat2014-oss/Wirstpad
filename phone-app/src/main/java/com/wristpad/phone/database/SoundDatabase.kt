package com.wristpad.phone.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLite

@Database(entities = [SoundEntity.class], version = 1, exportSchema = false)
abstract class SoundDatabase : RoomDatabase() {

    abstract fun soundDao(): SoundDao

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        // Database opened - could trigger initial sync here
    }
}