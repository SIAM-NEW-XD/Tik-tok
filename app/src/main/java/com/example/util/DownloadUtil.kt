package com.example.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object DownloadUtil {
    fun startDownload(
        context: Context,
        url: String,
        fileName: String,
        mimeType: String = "video/mp4"
    ) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri).apply {
                setTitle(fileName)
                setDescription("Downloading file via Video Downloader...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
                setMimeType(mimeType)
            }
            downloadManager.enqueue(request)
            Toast.makeText(context, "Download started: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to start download: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
