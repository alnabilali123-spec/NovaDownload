package com.novadownload.core.download

import android.content.Context
import androidx.work.*
import com.novadownload.data.db.DownloadDao
import com.novadownload.core.model.DownloadItem
import com.novadownload.core.model.DownloadState
import kotlinx.coroutines.flow.Flow

class DownloadManagerImpl(
    private val context: Context,
    private val dao: DownloadDao
) {
    fun getAllDownloads(): Flow<List<DownloadItem>> = dao.getAllFlow()

    suspend fun enqueue(item: DownloadItem) {
        dao.insert(item)
        val data = workDataOf(
            "downloadId" to item.id,
            "url" to item.originalUrl,
            "formatId" to item.selectedFormatId,
            "ext" to item.selectedExt,
            "fileName" to item.fileName
        )
        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(data)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .addTag("nova_download")
            .addTag("download_${item.id}")
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    suspend fun pause(id: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag("download_$id")
        dao.updateState(id, DownloadState.PAUSED)
    }

    suspend fun resume(id: String) {
        val item = dao.getById(id) ?: return
        dao.updateState(id, DownloadState.QUEUED)
        enqueue(item.copy(state = DownloadState.QUEUED))
    }

    suspend fun cancel(id: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag("download_$id")
        dao.updateState(id, DownloadState.CANCELLED)
    }

    suspend fun retry(id: String) {
        val item = dao.getById(id) ?: return
        dao.updateState(id, DownloadState.QUEUED)
        enqueue(item)
    }
}
