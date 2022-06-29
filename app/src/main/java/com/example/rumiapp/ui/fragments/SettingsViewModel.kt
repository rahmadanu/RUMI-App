package com.example.rumiapp.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is settings fragment"
    }

    val text: LiveData<String> = _text
}