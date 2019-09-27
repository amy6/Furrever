package com.example.furrever

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var numDogs: Int = 0
    private lateinit var listView : RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var workManager: WorkManager
    private lateinit var dogsAdapter: DogsAdapter
    private var dogsList = ArrayList<Dog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.recyclerView)
        setUpRecyclerView()
        progressBar = findViewById(R.id.progressBar)

        workManager = WorkManager.getInstance(this)

        dogsAdapter = DogsAdapter(dogsList, this)
        listView.adapter = dogsAdapter

        progressBar.visibility = View.VISIBLE
        listView.visibility = View.GONE

        getAllDogs()
//        getRandomDog()
    }

    private fun setUpRecyclerView() {
        listView.layoutManager = GridLayoutManager(this, 2)
        listView.setHasFixedSize(true)
        listView.setItemViewCacheSize(10)
    }

    private fun getAllDogs() {
        val  url = "https://api.thedogapi.com/v1/images/search?size=small&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=8"

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val getAllDogsWorkRequest =
            OneTimeWorkRequestBuilder<GetAllDogsWorker>()
                .setInputData(workDataOf(ALL_DOGS_API_URL to url))
                .setConstraints(constraints)
                .addTag("GetAllDogsWorker")
                .build()

        Log.d("DOG", "Enqueuing get all doggos worker")
        workManager.enqueue(getAllDogsWorkRequest)

        workManager.getWorkInfoByIdLiveData(getAllDogsWorkRequest.id)
            .observe(this, Observer { workInfo ->
                Log.d("DOG", "Observing all doggos worker")
                if (workInfo != null) {
                    Log.d("DOG", "Get all doggos worker completed successfully")
                    val jsonResponse = workInfo.outputData.getString("RESPONSE")
                    if (jsonResponse != null) {
                        Log.d("DOG", "Response : $jsonResponse")
                        parseResponse(jsonResponse)
                        progressBar.visibility = View.GONE
                        listView.visibility = View.VISIBLE
                    }
                }
            })
    }

    private fun parseResponse(jsonResponse: String) {
        val jsonArray = JSONArray(jsonResponse)
        for (i in 0 until jsonArray.length()) {
            val dogJsonObject = jsonArray[i] as JSONObject
            val id = dogJsonObject.getString("id")
            val imgUrl = dogJsonObject.getString("url")
            val dog = Dog(imgUrl)
            dogsList.add(dog)
            Log.d("DOG", "Img Url for dog with ID $id is $imgUrl")
        }
        dogsAdapter.setDogs(dogsList)
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
