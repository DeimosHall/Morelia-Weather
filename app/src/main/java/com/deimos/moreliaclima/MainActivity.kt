package com.deimos.moreliaclima

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
import okhttp3.Dispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference
        val temperatureReference = database.child("Temperature").ref

        getTemperature()

        temperatureReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temperature = snapshot.getValue()
                binding.text.text = temperature.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DB-Errors", error.toString())
            }
        })
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
                        binding.APITemperature.text = "Temperature: ${city?.main?.temp}"
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