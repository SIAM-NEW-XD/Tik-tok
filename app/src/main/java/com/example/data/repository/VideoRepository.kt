package com.example.data.repository

import com.example.data.api.TikWmService
import com.example.data.api.TikWmResponse
import com.example.data.local.DownloadHistoryDao
import com.example.data.local.DownloadHistoryItem
import kotlinx.coroutines.flow.Flow

class VideoRepository(
    private val apiService: TikWmService,
    private val historyDao: DownloadHistoryDao
) {
    val allHistory: Flow<List<DownloadHistoryItem>> = historyDao.getAllHistory()

    suspend fun fetchVideoDetails(urlString: String): TikWmResponse {
        return apiService.getVideoDetails(urlString)
    }

    suspend fun saveToHistory(item: DownloadHistoryItem) {
        historyDao.insertItem(item)
    }

    suspend fun deleteFromHistory(id: String) {
        historyDao.deleteById(id)
    }

    suspend fun clearHistory() {
        historyDao.clearAll()
    }
}
