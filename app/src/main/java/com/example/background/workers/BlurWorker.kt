package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.Constants

class BlurWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private val TAG by lazy { BlurWorker::class.java.simpleName }

    override fun doWork(): Result {

        val resoureURi = inputData.getString(Constants.KEY_IMAGE_URI)

        return try {

            if (TextUtils.isEmpty(resoureURi)) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val picture = BitmapFactory.decodeStream(
                applicationContext.contentResolver.openInputStream(
                    Uri.parse(resoureURi)
                )
            )
            val output = WorkerUtils.blurBitmap(picture, applicationContext)
            val outletUri = WorkerUtils.writeBitmapToFile(applicationContext, output)
            outputData = Data.Builder().putString(Constants.KEY_IMAGE_URI, outletUri.toString()).build()

            WorkerUtils.makeStatusNotification("Output is $outletUri", applicationContext)
            Result.SUCCESS
        } catch (ex: Exception) {
            Log.e(TAG, "Error applying blur", ex)
            Result.FAILURE
        }
    }
}