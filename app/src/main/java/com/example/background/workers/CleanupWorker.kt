package com.example.background.workers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.Constants
import java.io.File

class CleanupWorker constructor(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    private val TAG by lazy { CleanupWorker::class.java.simpleName }

    override fun doWork(): Result {
        return try {
            val outputDirectory = File(applicationContext.filesDir, Constants.OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null && entries.isNotEmpty()) {
                    for (e: File in entries) {
                        val name = e.name
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
                            val deleted = e.delete()
                            Log.e(TAG, "Deleted $name - $deleted")
                        }
                    }
                }
            }
            Result.SUCCESS
        } catch (ex: Exception) {
            Log.e(TAG, " Error cleaning up", ex)
            Result.FAILURE
        }
    }
}