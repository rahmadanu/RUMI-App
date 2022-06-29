package com.example.rumiapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import com.example.rumiapp.R
import com.example.rumiapp.databinding.FragmentSettingsBinding

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener{

    private var binding: FragmentSettingsBinding? = null
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_preference)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}