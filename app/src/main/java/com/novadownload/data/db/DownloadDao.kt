package com.novadownload.data.db

import androidx.room.*
import com.novadownload.core.model.DownloadItem
import com.novadownload.core.model.DownloadState
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getById(id: String): DownloadItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DownloadItem)

    @Query("UPDATE downloads SET state = :state WHERE id = :id")
    suspend fun updateState(id: String, state: DownloadState)

    @Query("UPDATE downloads SET progress = :progress, downloadedBytes = :downloaded WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int, downloaded: Long)

    @Query("UPDATE downloads SET filePath = :path WHERE id = :id")
    suspend fun updateFilePath(id: String, path: String)

    @Query("UPDATE downloads SET errorMessage = :error WHERE id = :id")
    suspend fun updateError(id: String, error: String)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM downloads")
    suspend fun clearAll()
}
