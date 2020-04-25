package com.coolfire.pizzanow.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class LoginViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to Pizza Now"
    }
    val text: LiveData<String> = _text

}