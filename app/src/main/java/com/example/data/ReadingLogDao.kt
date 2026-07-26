package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingLogDao {
    @Query("SELECT * FROM reading_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ReadingLog>>

    @Query("SELECT * FROM reading_logs WHERE grade = :grade AND classNum = :classNum ORDER BY timestamp DESC")
    fun getLogsByClass(grade: Int, classNum: Int): Flow<List<ReadingLog>>

    @Query("SELECT * FROM reading_logs WHERE id = :id")
    suspend fun getLogById(id: Long): ReadingLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ReadingLog): Long

    @Update
    suspend fun updateLog(log: ReadingLog)

    @Delete
    suspend fun deleteLog(log: ReadingLog)

    @Query("DELETE FROM reading_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM reading_logs")
    suspend fun clearAll()
}
