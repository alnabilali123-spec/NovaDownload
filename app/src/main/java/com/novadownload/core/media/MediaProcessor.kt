package com.novadownload.core.media

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MediaProcessor(private val context: Context) {

    data class MergeRequest(
        val videoPath: File?,
        val audioPath: File?,
        val outputPath: File,
        val outputExt: String = "mp4"
    )

    suspend fun mergeVideoAudio(request: MergeRequest, onProgress: (Float) -> Unit = {}): Result<File> = withContext(Dispatchers.IO) {
        try {
            val cmd = when {
                request.videoPath != null && request.audioPath != null -> {
                    "-y -i \"${request.videoPath.absolutePath}\" -i \"${request.audioPath.absolutePath}\" -c:v copy -c:a aac -map 0:v:0 -map 1:a:0 -shortest \"${request.outputPath.absolutePath}\""
                }
                request.videoPath != null -> {
                    "-y -i \"${request.videoPath.absolutePath}\" -c copy \"${request.outputPath.absolutePath}\""
                }
                request.audioPath != null && request.outputExt == "mp3" -> {
                    "-y -i \"${request.audioPath.absolutePath}\" -vn -c:a libmp3lame -q:a 0 \"${request.outputPath.absolutePath}\""
                }
                else -> {
                    "-y -i \"${request.audioPath!!.absolutePath}\" -vn -c:a copy \"${request.outputPath.absolutePath}\""
                }
            }
            val session = FFmpegKit.execute(cmd)
            if (ReturnCode.isSuccess(session.returnCode)) {
                Result.success(request.outputPath)
            } else {
                Result.failure(Exception("FFmpeg failed: ${session.failStackTrace} - ${session.output}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun extractAudio(input: File, output: File, bitrate: String = "192k"): Result<File> = withContext(Dispatchers.IO) {
        val cmd = "-y -i \"${input.absolutePath}\" -vn -c:a libmp3lame -b:a $bitrate \"${output.absolutePath}\""
        val session = FFmpegKit.execute(cmd)
        if (ReturnCode.isSuccess(session.returnCode)) Result.success(output)
        else Result.failure(Exception(session.failStackTrace))
    }
}
