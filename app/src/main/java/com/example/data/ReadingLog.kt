package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_logs")
data class ReadingLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val grade: Int,
    val classNum: Int,
    val studentName: String,
    val bookTitle: String,
    val author: String,
    val publisher: String,
    val summary: String,
    val thoughts: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String,
    val isSynced: Boolean = false
)
