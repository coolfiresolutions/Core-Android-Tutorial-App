package com.coolfire.pizzanow

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

const val WORKSPACE_NAME="pizzanow.roninservices.com"
const val CLIENT_ID="b080fb60-c303-11e6-8640-e7c2b032a20c"
const val CLIENT_SECRET="POSTMANTEST"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        //Set up listener for login success or failure
        CoolfireCoreApplication.currentUser.observe(this@MainActivity, Observer {
            val loginResult = it ?: return@Observer


            if (CoolfireCoreApplication.currentUser.value?.status.toString() === "Success")
            {
                //Logged In
                //show home page
                val toast = Toast.makeText(CoolfireCoreApplication.ctx,"Display Name: " + CoolfireCoreApplication.currentUser.value?.displayName.toString(),
                    Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP,0,0)
                toast.show()
                navController.navigate(R.id.nav_home)
            }
            else if (CoolfireCoreApplication.currentUser.value?.status.toString() === "Error"){
                //Not logged in
                //Show error
                val toast = Toast.makeText(CoolfireCoreApplication.ctx, "Error: " + CoolfireCoreApplication.currentUser.value?.errorMessage.toString(),
                    Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP,0,0)

                toast.show()
            }
            //DBW T4
            else if (CoolfireCoreApplication.currentUser.value?.status.toString() === "Network"){
                // Connect to the Network
                val toast = Toast.makeText(CoolfireCoreApplication.ctx,"Network: " + CoolfireCoreApplication.currentUser.value?.netDesc.toString(),
                    Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP,0,0)
                toast.show()
            }
            else if (CoolfireCoreApplication.currentUser.value?.status.toString() === "mySessions"){
                // Connect to the Network
                val toast = Toast.makeText(CoolfireCoreApplication.ctx,CoolfireCoreApplication.currentUser.value?.errorMessage.toString(),
                    Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP,0,0)
                toast.show()
            }
            else if (CoolfireCoreApplication.currentUser.value?.status.toString() === "SessionTypeCreated"){
                // Connect to the Network
                val toast = Toast.makeText(CoolfireCoreApplication.ctx,"Created Session Type: " + CoolfireCoreApplication.currentUser.value?.errorMessage.toString(),
                    Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP,0,0)
                toast.show()
            }
            else if (CoolfireCoreApplication.currentUser.value?.status.toString() === "SessionCreated"){
                // Connect to the Network
                val toast = Toast.makeText(CoolfireCoreApplication.ctx,CoolfireCoreApplication.currentUser.value?.errorMessage.toString(),
                    Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP,0,0)
                toast.show()
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
