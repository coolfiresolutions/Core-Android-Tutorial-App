package com.coolfire.pizzanow

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.coolfire.pizzanow.data.model.LoggedInUser
import okhttp3.OkHttpClient

class CoolfireCoreApplication:Application(){
    companion object{
        var ctx:Context?=null
        private var _currentUser = MutableLiveData<LoggedInUser>()
        var currentUser:LiveData<LoggedInUser> = _currentUser

        val client = OkHttpClient()

        fun updateCurrentUser(uCurrectUser:LoggedInUser){
            _currentUser.postValue(uCurrectUser)
        }
    }

    override fun onCreate(){
        super.onCreate()
        ctx=applicationContext
        _currentUser.value=LoggedInUser("","","","","","","","","","","","")
    }
}