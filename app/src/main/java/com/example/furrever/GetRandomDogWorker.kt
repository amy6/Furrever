package com.example.furrever

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.furrever.MainActivity.Companion.RANDOM_DOG_API_URL
import java.net.URL

class GetRandomDogWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {

        val apiUrl = inputData.getString(RANDOM_DOG_API_URL)

        Log.d("DOG", "Executing get random dog request")
        val response = URL(apiUrl).readText()
        Log.d("DOG", "Response : $response")

//        workDataOf(RANDOM_DOG_IMAGE_URL, response[0])

        return Result.success()
    }

}