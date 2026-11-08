package com.wristpad.phone.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sounds")
data class SoundEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "emoji")
    val emoji: String = "",

    @ColumnInfo(name = "category")
    val category: String = "",

    @ColumnInfo(name = "file_path")
    val filePath: String = "",

    @ColumnInfo(name = "volume")
    val volume: Float = 1.0f,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)