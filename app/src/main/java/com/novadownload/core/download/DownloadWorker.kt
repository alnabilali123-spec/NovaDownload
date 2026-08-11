package com.novadownload.core.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.chaquo.python.Python
import com.novadownload.core.media.MediaProcessor
import com.novadownload.core.model.DownloadState
import com.novadownload.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile

class DownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val dao = AppDatabase.getInstance(ctx).downloadDao()
    private val client = OkHttpClient.Builder().build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val downloadId = inputData.getString("downloadId") ?: return@withContext Result.failure()
        val originalUrl = inputData.getString("url") ?: return@withContext Result.failure()
        val formatId = inputData.getString("formatId") ?: "best"
        val fileName = inputData.getString("fileName") ?: "video.mp4"

        try {
            dao.updateState(downloadId, DownloadState.ANALYZING)
            val python = Python.getInstance()
            val extractor = python.getModule("novadownload_extractor")
            val jsonStr = extractor.callAttr("get_direct_url", originalUrl, formatId).toString()
            val obj = org.json.JSONObject(jsonStr)
            val directUrl = obj.optString("url")
            val isSeparate = obj.optBoolean("is_separate", false)
            val audioUrl = obj.optString("audio_url", null).takeIf { it?.isNotBlank() == true }

            dao.updateState(downloadId, DownloadState.DOWNLOADING)

            val downloadDir = File(applicationContext.getExternalFilesDir(null), "NovaDownload")
            downloadDir.mkdirs()
            val tempVideo = File(downloadDir, "${downloadId}_video.tmp")
            val tempAudio = File(downloadDir, "${downloadId}_audio.tmp")
            val finalFile = File(downloadDir, fileName)

            if (directUrl.isNotBlank()) {
                downloadFileWithProgress(directUrl, tempVideo, downloadId)
            }
            if (isSeparate && audioUrl != null) {
                downloadFileWithProgress(audioUrl, tempAudio, downloadId)
            }

            if (isSeparate) {
                dao.updateState(downloadId, DownloadState.MERGING)
                val processor = MediaProcessor(applicationContext)
                val mergeResult = processor.mergeVideoAudio(
                    MediaProcessor.MergeRequest(
                        videoPath = tempVideo.takeIf { it.exists() },
                        audioPath = tempAudio.takeIf { it.exists() },
                        outputPath = finalFile
                    )
                )
                if (mergeResult.isFailure) {
                    dao.updateError(downloadId, mergeResult.exceptionOrNull()?.message ?: "Merge failed")
                    dao.updateState(downloadId, DownloadState.FAILED)
                    return@withContext Result.failure()
                }
                tempVideo.delete()
                tempAudio.delete()
            } else {
                if (tempVideo.exists()) tempVideo.renameTo(finalFile)
            }

            dao.updateFilePath(downloadId, finalFile.absolutePath)
            dao.updateState(downloadId, DownloadState.COMPLETED)
            dao.updateProgress(downloadId, 100, finalFile.length())

            Result.success()
        } catch (e: Exception) {
            dao.updateError(downloadId, e.message ?: "Download failed")
            dao.updateState(downloadId, DownloadState.FAILED)
            Result.retry()
        }
    }

    private suspend fun downloadFileWithProgress(url: String, dest: File, downloadId: String) {
        var downloaded = if (dest.exists()) dest.length() else 0L
        val requestBuilder = Request.Builder().url(url)
        if (downloaded > 0) {
            requestBuilder.addHeader("Range", "bytes=$downloaded-")
        }
        val response = client.newCall(requestBuilder.build()).execute()
        if (!response.isSuccessful && response.code != 206 && downloaded > 0) {
            dest.delete()
            downloaded = 0
            return downloadFileWithProgress(url, dest, downloadId)
        }
        val body = response.body ?: throw Exception("Empty body")
        val total = body.contentLength() + downloaded
        val input = body.byteStream()
        val raf = RandomAccessFile(dest, "rw")
        raf.seek(downloaded)
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            raf.write(buffer, 0, bytesRead)
            downloaded += bytesRead
            val progress = if (total > 0) ((downloaded * 100) / total).toInt() else 0
            setProgressAsync(workDataOf("progress" to progress, "downloaded" to downloaded))
            if (isStopped) {
                raf.close()
                input.close()
                throw Exception("Cancelled")
            }
        }
        raf.close()
        input.close()
    }
}
