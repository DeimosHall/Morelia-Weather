package com.deimos.moreliaclima

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.deimos.moreliaclima.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var videoPlayer: MediaPlayer
    private var currentVideoPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = Uri.parse("android.resource://${packageName}/${R.raw.background}")
        binding.videoBackground.setVideoURI(uri)
        binding.videoBackground.start()

        database = Firebase.database.reference
        val temperatureReference = database.child("Temperature").ref

        getTemperature()

        binding.videoBackground.setOnPreparedListener { mp ->
            videoPlayer = mp
            videoPlayer.isLooping = true

            if (currentVideoPosition != 0) {
                videoPlayer.seekTo(currentVideoPosition)
                videoPlayer.start()
            }
        }
        temperatureReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temperature = snapshot.getValue()
                binding.text.text = temperature.toString() + getString(R.string.celcius_degrees)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DB-Errors", error.toString())
            }
        })
    }

    override fun onPause() {
        super.onPause()
        currentVideoPosition = videoPlayer.currentPosition
        binding.videoBackground.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.videoBackground.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer.release()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getTemperature() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getTemperature("weather?q=morelia,mx&units=metric&appid=0cd02a5207e3a5a5e77554ce9bf3a2dc")
            val city = call.body()
            runOnUiThread {
                if (call.isSuccessful) {
                    try {
                        Log.d("myAPI", "Temperature: ${city?.main?.temp}")
                        binding.APITemperature.text = "${city?.main?.temp}${getString(R.string.celcius_degrees)}"
                    } catch (e: Exception) {
                        Log.d("myAPI", "Error: $e")
                    }
                } else {
                    Toast.makeText(applicationContext, "Couldn't connect to the weather host", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}