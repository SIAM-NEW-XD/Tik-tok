package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadHistoryDao {
    @Query("SELECT * FROM download_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<DownloadHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: DownloadHistoryItem)

    @Query("DELETE FROM download_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM download_history")
    suspend fun clearAll()
}
