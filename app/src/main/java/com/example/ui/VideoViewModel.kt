package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.TikWmVideoData
import com.example.data.local.DownloadHistoryItem
import com.example.data.repository.VideoRepository
import com.example.util.DownloadUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val videoData: TikWmVideoData) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class VideoViewModel(private val repository: VideoRepository) : ViewModel() {

    private val _inputUrl = MutableStateFlow("")
    val inputUrl: StateFlow<String> = _inputUrl.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val historyState: StateFlow<List<DownloadHistoryItem>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onUrlChange(newUrl: String) {
        _inputUrl.value = newUrl
    }

    fun clearInput() {
        _inputUrl.value = ""
    }

    fun searchVideo() {
        val url = _inputUrl.value.trim()
        if (url.isEmpty()) {
            _uiState.value = SearchUiState.Error("Please enter a valid video URL.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val response = repository.fetchVideoDetails(url)
                if (response.code == 0 && response.data != null) {
                    _uiState.value = SearchUiState.Success(response.data)
                    // Auto-add to search history upon successful parse
                    val videoData = response.data
                    val id = videoData.id ?: System.currentTimeMillis().toString()
                    val historyItem = DownloadHistoryItem(
                        id = id,
                        title = videoData.title ?: "No Description",
                        coverUrl = videoData.cover ?: "",
                        playUrl = videoData.play ?: "",
                        wmPlayUrl = videoData.wmplay,
                        authorName = videoData.author?.nickname ?: "Unknown User",
                        authorUsername = videoData.author?.unique_id ?: "unknown",
                        authorAvatar = videoData.author?.avatar ?: "",
                        musicUrl = videoData.music,
                        durationSeconds = videoData.duration ?: 0
                    )
                    repository.saveToHistory(historyItem)
                } else {
                    val errMsg = response.msg ?: "No video details returned. Make sure the TikTok link is public and valid."
                    _uiState.value = SearchUiState.Error(errMsg)
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.localizedMessage ?: "Network or connection failure. Please confirm internet parameters.")
            }
        }
    }

    fun downloadVideoNoWatermark(context: Context, videoData: TikWmVideoData) {
        val playUrl = videoData.play ?: return
        val id = videoData.id ?: System.currentTimeMillis().toString()
        val title = videoData.title ?: "video"
        val cleanTitle = title.take(50).replace("[\\\\/:*?\"<>|]".toRegex(), "_")
        val fileName = "Video_NoWatermark_${id}_${cleanTitle}.mp4"

        DownloadUtil.startDownload(context, playUrl, fileName, "video/mp4")
    }

    fun downloadVideoWatermark(context: Context, videoData: TikWmVideoData) {
        val wmPlayUrl = videoData.wmplay ?: return
        val id = videoData.id ?: System.currentTimeMillis().toString()
        val title = videoData.title ?: "video"
        val cleanTitle = title.take(50).replace("[\\\\/:*?\"<>|]".toRegex(), "_")
        val fileName = "Video_Watermark_${id}_${cleanTitle}.mp4"

        DownloadUtil.startDownload(context, wmPlayUrl, fileName, "video/mp4")
    }

    fun downloadAudioMp3(context: Context, videoData: TikWmVideoData) {
        val audioUrl = videoData.music ?: videoData.music_info?.play ?: return
        val id = videoData.id ?: System.currentTimeMillis().toString()
        val musicTitle = videoData.music_info?.title ?: "music"
        val cleanTitle = musicTitle.take(50).replace("[\\\\/:*?\"<>|]".toRegex(), "_")
        val fileName = "Video_Audio_${id}_${cleanTitle}.mp3"

        DownloadUtil.startDownload(context, audioUrl, fileName, "audio/mpeg")
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            repository.deleteFromHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun tryLoadUrlFromClipboard(context: Context) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
        val clipData = clipboardManager?.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val clipboardText = clipData.getItemAt(0).text?.toString() ?: ""
            if (clipboardText.startsWith("http://") || clipboardText.startsWith("https://")) {
                _inputUrl.value = clipboardText
            }
        }
    }
}

class VideoViewModelFactory(private val repository: VideoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
