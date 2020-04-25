package com.coolfire.pizzanow.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.coolfire.pizzanow.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject

import androidx.appcompat.app.AppCompatActivity
import com.coolfire.pizzanow.data.model.LoggedInUser
import kotlinx.coroutines.launch


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


        val loginButton: Button =root.findViewById(R.id.loginButton)
        val userNameTextView:TextView=root.findViewById(R.id.username)
        val passwordTextView:TextView=root.findViewById(R.id.password)
        val loginTextView:TextView=root.findViewById(R.id.text_login)

        //Setup listener for loginButton
        loginButton.setOnClickListener{
            attemptLogin(userNameTextView.text.toString(), passwordTextView.text.toString())
        }

        return root
    }

    fun attemptLogin(username: String, password: String){
        GlobalScope.launch {
            fetchBearer(username, password)
        }
    }

    fun fetchBearer(username:String,password:String){
        // 1) Create the HttpClient

        // 2) Populate the body of the request
        // 3) We are providing a username and password,and asking for a bearer token
        val formBody: RequestBody = FormBody.Builder()
            .add("grant_type", "password")
            .add("username", username)
            .add("password", password)
            .add("client_id", CLIENT_ID)
            .add("client_secret", CLIENT_SECRET)
            .build()

        // 4) Build the request, combining the body,the URL
        val request: Request = Request.Builder()
            .url("https://" + WORKSPACE_NAME + "/ronin/oauth2/token")
            .post(formBody)
            .build()

        try{
        // 5) Setup the call and execute it
            val call: Call = CoolfireCoreApplication.client.newCall(request)
            val response: Response = call.execute()

        // 6) Extract the response body as a JSON object
            var json = JSONObject(response.body!!.string())

        // 7) Create the authorization by concatenating "Bearer" and the access_token from the JSON
            val bToken = "Bearer " + json.getString("access_token")

        // 8) Build a second request
            val request2: Request = Request.Builder()
                .url("https://unidev.roninservices.com/ronin/api/v1/userprofile")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization",bToken)
                .build()

        //9 Setup the second call and execute it
            val call2: Call = CoolfireCoreApplication.client.newCall(request2)
            val response2: Response = call2.execute()

        // 10) Extract the response body as a JSON object
            var json2 = JSONObject(response2.body!!.string())

        // 11) Create and return the display Name from the JSON values of firstName and lastName
            CoolfireCoreApplication.updateCurrentUser(
                LoggedInUser(
                    username,
                    json2.getString("firstName") + " " + json2.getString("lastName"),
                    json.getString("access_token"),
                    json.getString("refresh_token"),
                    password,
                    "MyDevice",
                    json.getString("expires_in"),
                    "",
                    "Success"
                )
            )

        }catch(e:Exception){
        // 12) If there is an exception, return the message instead of the displayName
             CoolfireCoreApplication.updateCurrentUser(
                LoggedInUser(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    e.localizedMessage.toString(),
                    "Error"
                )
            )
        }
    }

}

