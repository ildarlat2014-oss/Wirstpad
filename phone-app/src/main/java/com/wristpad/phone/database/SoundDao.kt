package com.wristpad.phone.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SoundDao {
    @Insert(onConflict = OnConflictStrategy.INSERT)
    suspend fun insert(sound: SoundEntity)

    @Insert(onConflict = OnConflictStrategy.DELETE)
    suspend fun delete(sound: SoundEntity)

    @Query("SELECT * FROM sounds ORDER BY category, title")
    suspend fun getAllSounds(): List<SoundEntity>

    @Query("SELECT * FROM sounds WHERE category = :category ORDER BY title")
    suspend fun getSoundsByCategory(category: String): List<SoundEntity>

    @Query("SELECT DISTINCT category FROM sounds")
    suspend fun getAllCategories(): List<String>

    @Query("DELETE FROM sounds")
    suspend fun clearAll()
}