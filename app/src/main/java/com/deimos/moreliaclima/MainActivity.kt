package com.deimos.moreliaclima

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.deimos.moreliaclima.databinding.ActivityMainBinding
import com.deimos.moreliaclima.device.Device
import com.deimos.moreliaclima.device.SharedPreferences
import com.deimos.moreliaclima.network.MyNetwork
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
    private lateinit var temperatureReference: DatabaseReference
    private lateinit var myNetwork: MyNetwork
    private lateinit var myDevice: Device
    private lateinit var videoPlayer: MediaPlayer
    private var currentVideoPosition: Int = 0
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.btnSettings.setOnClickListener {
            intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        currentVideoPosition = videoPlayer.currentPosition
        binding.videoBackground.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.videoBackground.start()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer.release()
    }

    private fun init() {
        myNetwork = MyNetwork(this)
        myDevice = Device(this)
        prefs = SharedPreferences(applicationContext)

        initVideoBackground()
        initDatabaseListener()
    }

    private fun initVideoBackground() {
        val uri = Uri.parse("android.resource://${packageName}/${R.raw.background}")
        binding.videoBackground.setVideoURI(uri)
        binding.videoBackground.start()
        setBackgroundDimmer()

        binding.videoBackground.setOnPreparedListener { mp ->
            videoPlayer = mp
            videoPlayer.isLooping = true

            if (currentVideoPosition != 0) {
                videoPlayer.seekTo(currentVideoPosition)
                videoPlayer.start()
            }
        }
    }

    private fun initDatabaseListener() {
        database = Firebase.database.reference
        temperatureReference = database.child("Temperature").ref

        temperatureReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temperature = snapshot.getValue()
                binding.text.text = convertTemperature(temperature.toString().toDouble())
                setAPITemperature(binding.APITemperature)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DB-Errors", error.toString())
            }
        })
    }

    // Enables or disables a background dimmer depending on the system dark mode
    private fun setBackgroundDimmer() {
        if (myDevice.isDarkModeEnabled()) {
            binding.backgroundDimmer.visibility = View.VISIBLE
        } else {
            binding.backgroundDimmer.visibility = View.GONE
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun setAPITemperature(textView: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getTemperature("weather?q=morelia,mx&units=metric&appid=0cd02a5207e3a5a5e77554ce9bf3a2dc")
            val city = call.body()
            runOnUiThread {
                if (call.isSuccessful) {
                    try {
                        Log.d("myAPI", "Temperature: ${city?.main?.temp}")
                        textView.text = convertTemperature(city?.main?.temp)
                    } catch (e: Exception) {
                        Log.d("myAPI", "Error: $e")
                    }
                } else {
                    Toast.makeText(applicationContext, "Couldn't connect to the weather host", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun convertTemperature(celciusTemperature: Double?): String {
        if (celciusTemperature != null) {
            if (prefs.getSharedTemperatureUnit() == "celcius") {
                return "$celciusTemperature° C"
            } else {
                val fahrenheitTemperature = String.format("%.2f", (celciusTemperature * 9 / 5) + 32)
                return "$fahrenheitTemperature° F"
            }
        } else {
            return "? ° C"
        }
    }
}