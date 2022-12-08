package com.deimos.moreliaclima

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.deimos.moreliaclima.databinding.FragmentSettingsBinding
import com.deimos.moreliaclima.device.SharedPreferences

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharedPreferences(context)

        // Init value
        if (prefs.getSharedTemperatureUnit() == "celcius") {
            binding.rbtnCelcius.isChecked = true
        } else {
            binding.rbtnFahrenheit.isChecked = true
        }

        binding.rbtnCelcius.setOnCheckedChangeListener { button, pressed ->
            when (pressed) {
                true -> prefs.setSharedTemperatureUnit("celcius")
                false -> prefs.setSharedTemperatureUnit("fahrenheit")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment = SettingsFragment()
    }
}