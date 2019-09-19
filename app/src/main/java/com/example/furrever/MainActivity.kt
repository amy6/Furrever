package com.example.furrever

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workManager = WorkManager.getInstance(this)

        getAllDogs()
//        getRandomDog()
    }

    private fun getAllDogs() {
        val  url = "https://dog.ceo/api/breed/hound/images"

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val getAllDogsWorkRequest =
            OneTimeWorkRequestBuilder<GetAllDogsWorker>()
                .setInputData(workDataOf(ALL_DOGS_API_URL to url))
                .setConstraints(constraints)
                .addTag("GetAllDogsWorker")
                .build()

        Log.d("DOG", "Enqueuing get all dogoos worker")
        workManager.enqueue(getAllDogsWorkRequest)

        workManager.getWorkInfoByIdLiveData(getAllDogsWorkRequest.id)
            .observe(this, Observer {
                Log.d("DOG", "Observing all doggos worker")
                if (it != null) {
                    Log.d("DOG", "Get all doggos worker completed successfully")
                }
            })
    }

    private fun getRandomDog() {
        val url = "https://dog.ceo/api/breeds/image/random"

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val getRandomDogWorkRequest =
            PeriodicWorkRequestBuilder<GetRandomDogWorker>(24, TimeUnit.HOURS)
                .setInputData(workDataOf(RANDOM_DOG_API_URL to url))
                .setConstraints(constraints)
                .addTag("GetRandomDogWorker")
                .build()

        Log.d("DOG", "Enqueuing get random doggo worker")
        workManager.enqueue(getRandomDogWorkRequest)

        workManager.getWorkInfoByIdLiveData(getRandomDogWorkRequest.id)
            .observe(this, Observer {
                Log.d("DOG", "Observing random doggo worker")
                if (it != null) {
                    val imageUrl = it.outputData.getString(RANDOM_DOG_IMAGE_URL)
                    Log.d("DOG", "Get random doggo worker completed successfully")
                    sendNotification(imageUrl)
                }
            })

    }

    private fun sendNotification(imageUrl: String?) {

        val sendNotificationWorkRequest =
            OneTimeWorkRequestBuilder<RandomDogNotificationWorker>()
                .setInputData(workDataOf(RANDOM_DOG_IMAGE_URL to imageUrl))
                .build()

        Log.d("DOG", "Enqueuing notification worker")
        workManager.enqueue(sendNotificationWorkRequest)

    }

    companion object {
        const val ALL_DOGS_API_URL = "all_dogs_api_url"
        const val RANDOM_DOG_API_URL = "random_dog_api_url"
        const val RANDOM_DOG_IMAGE_URL = "random_dog_image_url"
    }
}
