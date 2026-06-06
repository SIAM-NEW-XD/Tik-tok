package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_history")
data class DownloadHistoryItem(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String,
    val playUrl: String,
    val wmPlayUrl: String?,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String,
    val musicUrl: String?,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)
