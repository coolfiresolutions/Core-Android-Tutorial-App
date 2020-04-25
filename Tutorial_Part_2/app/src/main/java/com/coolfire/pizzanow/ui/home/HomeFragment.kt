package com.coolfire.pizzanow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.coolfire.pizzanow.CoolfireCoreApplication
import com.coolfire.pizzanow.R


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val resultsText = "Display Name = " + CoolfireCoreApplication.currentUser.value?.displayName + "\n\n" +
                "User ID = " + CoolfireCoreApplication.currentUser.value?.userId + "\n\n" +
                "Password = " + CoolfireCoreApplication.currentUser.value?.password + "\n\n" +
                "Access Token = " + CoolfireCoreApplication.currentUser.value?.accessToken + "\n\n" +
                "Refresh Token = " + CoolfireCoreApplication.currentUser.value?.refreshToken + "\n\n" +
                "Token Life Time = " + CoolfireCoreApplication.currentUser.value?.tokenLifeTime + "\n\n" +
                "Device ID = " + CoolfireCoreApplication.currentUser.value?.deviceId

        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = resultsText
        })

        return root
    }
}
