package com.deimos.moreliaclima

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.deimos.moreliaclima.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference
        val temperatureReference = database.child("Temperature").ref

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
}