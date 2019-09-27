package com.example.furrever

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.furrever.MainActivity.Companion.ALL_DOGS_API_URL
import com.example.furrever.MainActivity.Companion.RANDOM_DOG_API_URL
import java.net.URL

class GetAllDogsWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {

        val apiUrl = inputData.getString(ALL_DOGS_API_URL)

        Log.d("DOG", "Executing get all dogs request")
        val response = URL(apiUrl).readText()
        Log.d("DOG", "Response : $response")

        val output = workDataOf("RESPONSE" to response)

        return Result.success(output)
    }

}