package com.novadownload.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DownloadState { QUEUED, ANALYZING, DOWNLOADING, MERGING, COMPLETED, PAUSED, FAILED, CANCELLED }

@Entity(tableName = "downloads")
data class DownloadItem(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val originalUrl: String,
    val platform: Platform,
    val title: String,
    val thumbnailUrl: String?,
    val selectedFormatId: String,
    val selectedExt: String,
    val fileName: String,
    val filePath: String? = null,
    val fileSize: Long? = null,
    val downloadedBytes: Long = 0,
    val progress: Int = 0,
    val speedBps: Long = 0,
    val state: DownloadState = DownloadState.QUEUED,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
