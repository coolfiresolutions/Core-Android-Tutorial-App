package com.coolfire.pizzanow.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.coolfire.pizzanow.*



class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
                val root = inflater.inflate(R.layout.fragment_login, container, false)
                val textView: TextView = root.findViewById(R.id.text_login)
                loginViewModel.text.observe(viewLifecycleOwner, Observer {
                    textView.text = it
                    }
                )

        return root
    }


}

