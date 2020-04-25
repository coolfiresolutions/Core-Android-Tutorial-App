package com.coolfire.pizzanow.ui.home

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
import com.coolfire.pizzanow.data.model.LoggedInUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.Request.Builder
import org.json.JSONObject
import org.json.JSONStringer


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

        val textView: TextView = root.findViewById(R.id.text_home)

        val setNetworkButton: Button =root.findViewById(R.id.button_SetNetwork)
        //Setup listener for setNetworkButton
        setNetworkButton.setOnClickListener{
            setNetworkAsync("PizzaNow Store #1","PizzaNow Store #1 Network")
        }

        val getMySessionsButton: Button =root.findViewById(R.id.button_GetMySessions)
        //Setup listener for getMySessionsButton
        getMySessionsButton.setOnClickListener{
            getMySessionsAsync()
        }

        val createSessionTypesButton: Button =root.findViewById(R.id.button_CreateSessionType)
        //Setup listener for createSessionTypesButton
        createSessionTypesButton.setOnClickListener{
            createSessionTypesAsync("Test Session","This is a test session type")
        }

        val createSessionButton: Button =root.findViewById(R.id.button_CreateSession)
        //Setup listener for createSessionButton
        createSessionButton.setOnClickListener{
            createSessionAsync("My Test session", "Test Session from Tutorial", "Test Session")
        }

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = ""
        })

        return root
    }

    //DBW T4
    fun setNetworkAsync(netName: String, netDesc: String){
        GlobalScope.launch {
            setNetwork(netName, netDesc)
        }
    }

    fun getMySessionsAsync(){
        GlobalScope.launch {
            getMySessions()
        }
    }

    fun createSessionTypesAsync(name: String, description: String){
        GlobalScope.launch {
            createSessionTypes(name, description)
        }
    }

    fun createSessionAsync(name: String, description: String, sessionType: String){
        GlobalScope.launch {
            createSession(name, description,sessionType)
        }
    }

    fun setNetwork(netName: String, netDesc: String){
        try{

            val nwiJSON = JSONStringer()
                .`object`()
                    .key("nwi")
                    .`object`()
                    .key("name")
                    .value(netName)
                    .endObject()
                .endObject()
                .toString()

            val formBody1: RequestBody = FormBody.Builder()
                .add("query",
                    "query checkNetwork (\$nwi: NetworkWhereInput){networks(where: \$nwi){id}}")
                .add("variables", nwiJSON)
                .build()

            val request1: Request = Builder()
                .url("https://" + WORKSPACE_NAME + "/ronin/gql")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization", CoolfireCoreApplication.currentUser.value!!.accessToken)
                .post(formBody1)
                .build()

            val call1: Call = CoolfireCoreApplication.client.newCall(request1)
            val response1: okhttp3.Response = call1.execute()
            val json1 = JSONObject(response1.body!!.string())

            var netId = ""

            val json1a = json1.getJSONObject("data")
            val netArray = json1a.getJSONArray("networks")

            if (netArray.length() == 0)
            {
                // alert the user or make the network
                val cniJSON = JSONStringer()
                    .`object`()
                        .key("cni")
                        .`object`()
                            .key("name")
                            .value(netName)
                            .key("description")
                            .value(netDesc)
                            .key("users")
                            .array()
                            .endArray()
                        .endObject()
                    .endObject()
                    .toString()

                val formBody2: RequestBody = FormBody.Builder()
                    .add("query",
                        "mutation makeNetwork (\$cni: CreateNetworkInput!){createNetwork(input: \$cni){entity{id}}}")
                    .add("variables", cniJSON)
                    .build()

                val request2: Request = Request.Builder()
                    .url("https://" + WORKSPACE_NAME + "/ronin/gql")
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization",CoolfireCoreApplication.currentUser.value!!.accessToken)
                    .post(formBody2)
                    .build()

                val call2: Call = CoolfireCoreApplication.client.newCall(request2)
                val response2: okhttp3.Response = call2.execute()
                val json2 = JSONObject(response2.body!!.string())

                // Check for errors object when creating the network. Just as a best practice.
                if(json2.has("errors"))
                {
                    //Report the error
                    val json2a = json2.getJSONArray("errors")
                    val json2b = json2a.getJSONObject(0)
                    val errMsg = json2b.getString("message")
                    CoolfireCoreApplication.updateCurrentUser(
                        LoggedInUser(
                            CoolfireCoreApplication.currentUser.value!!.userId,
                            CoolfireCoreApplication.currentUser.value!!.displayName,
                            CoolfireCoreApplication.currentUser.value!!.accessToken,
                            CoolfireCoreApplication.currentUser.value!!.refreshToken,
                            CoolfireCoreApplication.currentUser.value!!.password,
                            CoolfireCoreApplication.currentUser.value!!.deviceId,
                            CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                            errMsg,
                            "Error",
                            "",
                            "",
                            ""
                        )
                    )
                }
                else{
                    // Parse the result
                    val json2a = json2.getJSONObject("data")
                    val json2b = json2a.getJSONObject("entity")
                    netId = json2b.getString("id")
                    CoolfireCoreApplication.updateCurrentUser(
                        LoggedInUser(
                            CoolfireCoreApplication.currentUser.value!!.userId,
                            CoolfireCoreApplication.currentUser.value!!.displayName,
                            CoolfireCoreApplication.currentUser.value!!.accessToken,
                            CoolfireCoreApplication.currentUser.value!!.refreshToken,
                            CoolfireCoreApplication.currentUser.value!!.password,
                            CoolfireCoreApplication.currentUser.value!!.deviceId,
                            CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                            "",
                            "Network",
                            netId,
                            netDesc,
                            ""
                        )
                    )
                }

            }
            else{
                val netArrayObj = netArray.getJSONObject(0)
                netId = netArrayObj.getString("id")
                CoolfireCoreApplication.updateCurrentUser(
                    LoggedInUser(
                        CoolfireCoreApplication.currentUser.value!!.userId,
                        CoolfireCoreApplication.currentUser.value!!.displayName,
                        CoolfireCoreApplication.currentUser.value!!.accessToken,
                        CoolfireCoreApplication.currentUser.value!!.refreshToken,
                        CoolfireCoreApplication.currentUser.value!!.password,
                        CoolfireCoreApplication.currentUser.value!!.deviceId,
                        CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                        "",
                        "Network",
                        netId,
                        netDesc,
                        ""
                    )
                )
            }
        }catch(e:Exception){
            CoolfireCoreApplication.updateCurrentUser(
                LoggedInUser(
                    CoolfireCoreApplication.currentUser.value!!.userId,
                    CoolfireCoreApplication.currentUser.value!!.displayName,
                    CoolfireCoreApplication.currentUser.value!!.accessToken,
                    CoolfireCoreApplication.currentUser.value!!.refreshToken,
                    CoolfireCoreApplication.currentUser.value!!.password,
                    CoolfireCoreApplication.currentUser.value!!.deviceId,
                    CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                    e.message.toString(),
                    "Error",
                    "",
                    "",
                    ""
                )
            )
        }
    }

    fun getMySessions(){
        try{
            //Who am I?
            var myID: String = ""
            if (CoolfireCoreApplication.currentUser.value?.me == "") {
                val formBody1: RequestBody = FormBody.Builder()
                    .add("query", "query{me{id}}")
                    .build()

                val request1: Request = Builder()
                    .url("https://" + WORKSPACE_NAME + "/ronin/gql")
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization", CoolfireCoreApplication.currentUser.value!!.accessToken)
                    .post(formBody1)
                    .build()

                val call1: Call = CoolfireCoreApplication.client.newCall(request1)
                val response1: okhttp3.Response = call1.execute()
                val json1 = JSONObject(response1.body!!.string())
                val json1a = json1.getJSONObject("data")
                val json1b = json1a.getJSONObject("me")
                myID = json1b.getString("id")
            }
            else{
                myID = CoolfireCoreApplication.currentUser.value!!.me
            }

            val formBody2: RequestBody = FormBody.Builder()
                .add("query",
                    "query {sessions (where:{network:\"" + CoolfireCoreApplication.currentUser.value!!.netId + "\" AND: {users__contains:\"" + myID + "\"}}){name,displayName,id}}")
                .build()

            val request2: Request = Builder()
                .url("https://" + WORKSPACE_NAME + "/ronin/gql")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization", CoolfireCoreApplication.currentUser.value!!.accessToken)
                .post(formBody2)
                .build()

            val call2: Call = CoolfireCoreApplication.client.newCall(request2)
            val response1: okhttp3.Response = call2.execute()
            val json2 = JSONObject(response1.body!!.string())
            val json2a = json2.getJSONObject("data")
            val sessionsArray = json2a.getJSONArray("sessions")
            val numSessions = sessionsArray.length()

            // you have sessions
            CoolfireCoreApplication.updateCurrentUser(
                LoggedInUser(
                    CoolfireCoreApplication.currentUser.value!!.userId,
                    CoolfireCoreApplication.currentUser.value!!.displayName,
                    CoolfireCoreApplication.currentUser.value!!.accessToken,
                    CoolfireCoreApplication.currentUser.value!!.refreshToken,
                    CoolfireCoreApplication.currentUser.value!!.password,
                    CoolfireCoreApplication.currentUser.value!!.deviceId,
                    CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                    "You have " + numSessions.toString() + " sessions",
                    "mySessions",
                    CoolfireCoreApplication.currentUser.value!!.netId,
                    CoolfireCoreApplication.currentUser.value!!.netDesc,
                    myID
                )
            )

        }catch(e:Exception){
            CoolfireCoreApplication.updateCurrentUser(
                LoggedInUser(
                    CoolfireCoreApplication.currentUser.value!!.userId,
                    CoolfireCoreApplication.currentUser.value!!.displayName,
                    CoolfireCoreApplication.currentUser.value!!.accessToken,
                    CoolfireCoreApplication.currentUser.value!!.refreshToken,
                    CoolfireCoreApplication.currentUser.value!!.password,
                    CoolfireCoreApplication.currentUser.value!!.deviceId,
                    CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                    e.message.toString(),
                    "Error",
                    CoolfireCoreApplication.currentUser.value!!.netId,
                    CoolfireCoreApplication.currentUser.value!!.netDesc,
                    CoolfireCoreApplication.currentUser.value!!.me
                )
            )
        }
    }

    fun createSessionTypes(name: String, description: String) {
        try {
            val cstiJSON = JSONStringer()
                .`object`()
                .key("csti")
                    .`object`()
                        .key("name")
                        .value(name)
                        .key("description")
                        .value(description)
                        .key("iconUrl")
                        .value("")
                        .key("severity")
                        .value(1)
                        .key("color")
                        .value("#FF0000")
                        .key("tasks")
                        .array()
                            .`object`()
                                .key("name")
                                .value("First Task")
                                .key("description")
                                .value("This is the first task")
                            .endObject()
                            .`object`()
                                .key("name")
                                .value("Last Task")
                                .key("description")
                                .value("This is the last task")
                            .endObject()
                        .endArray()
                    .endObject()
                .endObject()
                .toString()


            val formBody1: RequestBody = FormBody.Builder()
                .add(
                    "query",
                    "mutation createSessionType(\$csti: CreateSessionTypeInput!) {createSessionType(input: \$csti) {entity {id}}}"
                )
                .add("variables", cstiJSON)
                .build()

            val request1: Request = Builder()
                .url("https://" + WORKSPACE_NAME + "/ronin/gql")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", CoolfireCoreApplication.currentUser.value!!.accessToken)
                .post(formBody1)
                .build()

            val call1: Call = CoolfireCoreApplication.client.newCall(request1)
            val response1: okhttp3.Response = call1.execute()
            val json1 = JSONObject(response1.body!!.string())

            // check if there was an error
            // Check for errors object when creating the network. Just as a best practice.
            if (json1.has("errors")) {
                val json1a = json1.getJSONArray("errors")
                val json1b = json1a.getJSONObject(0)
                val errMsg = json1b.getString("message")
                CoolfireCoreApplication.updateCurrentUser(
                    LoggedInUser(
                        CoolfireCoreApplication.currentUser.value!!.userId,
                        CoolfireCoreApplication.currentUser.value!!.displayName,
                        CoolfireCoreApplication.currentUser.value!!.accessToken,
                        CoolfireCoreApplication.currentUser.value!!.refreshToken,
                        CoolfireCoreApplication.currentUser.value!!.password,
                        CoolfireCoreApplication.currentUser.value!!.deviceId,
                        CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                        errMsg,
                        "Error",
                        CoolfireCoreApplication.currentUser.value!!.netId,
                        CoolfireCoreApplication.currentUser.value!!.netDesc,
                        CoolfireCoreApplication.currentUser.value!!.me
                    )
                )
            } else {
                CoolfireCoreApplication.updateCurrentUser(
                    LoggedInUser(
                        CoolfireCoreApplication.currentUser.value!!.userId,
                        CoolfireCoreApplication.currentUser.value!!.displayName,
                        CoolfireCoreApplication.currentUser.value!!.accessToken,
                        CoolfireCoreApplication.currentUser.value!!.refreshToken,
                        CoolfireCoreApplication.currentUser.value!!.password,
                        CoolfireCoreApplication.currentUser.value!!.deviceId,
                        CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                        name,
                        "SessionTypeCreated",
                        CoolfireCoreApplication.currentUser.value!!.netId,
                        CoolfireCoreApplication.currentUser.value!!.netDesc,
                        CoolfireCoreApplication.currentUser.value!!.me
                    )
                )
            }

        } catch (e: Exception) {
            CoolfireCoreApplication.updateCurrentUser(
                LoggedInUser(
                    CoolfireCoreApplication.currentUser.value!!.userId,
                    CoolfireCoreApplication.currentUser.value!!.displayName,
                    CoolfireCoreApplication.currentUser.value!!.accessToken,
                    CoolfireCoreApplication.currentUser.value!!.refreshToken,
                    CoolfireCoreApplication.currentUser.value!!.password,
                    CoolfireCoreApplication.currentUser.value!!.deviceId,
                    CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                    e.message.toString(),
                    "Error",
                    CoolfireCoreApplication.currentUser.value!!.netId,
                    CoolfireCoreApplication.currentUser.value!!.netDesc,
                    CoolfireCoreApplication.currentUser.value!!.me
                )
            )
        }
    }


    fun createSession(name: String, description: String, sessionType: String){
        try{
            val formBody1: RequestBody = FormBody.Builder()
                .add("query", "query{sessionTypes(where:{name:\"" + sessionType + "\"}){id}}")
                .build()

            val request1: Request = Builder()
                .url("https://" + WORKSPACE_NAME + "/ronin/gql")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization", CoolfireCoreApplication.currentUser.value!!.accessToken)
                .post(formBody1)
                .build()

            val call1: Call = CoolfireCoreApplication.client.newCall(request1)
            val response1: okhttp3.Response = call1.execute()
            val json1 = JSONObject(response1.body!!.string())
            val json1a = json1.getJSONObject("data")
            val json1b = json1a.getJSONArray("sessionTypes")
            val sessionTypeObj = json1b.getJSONObject(0)
            val sessionTypeID = sessionTypeObj.getString("id")

            val csiJSON = JSONStringer()
                .`object`()
                .key("csi")
                .`object`()
                .key("name")
                .value(name)
                .key("description")
                .value(description)
                .key("network")
                .value(CoolfireCoreApplication.currentUser.value!!.netId)
                .key("sessType")
                .value(sessionTypeID)
                .endObject()
                .endObject()
                .toString()

            val formBody2: RequestBody = FormBody.Builder()
                .add("query",
                    "mutation createSession (\$csi: CreateSessionInput!){createSession(input: \$csi){entity{id}}}")
                .add("variables", csiJSON)
                .build()

            val request2: Request = Builder()
                .url("https://" + WORKSPACE_NAME + "/ronin/gql")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization", CoolfireCoreApplication.currentUser.value!!.accessToken)
                .post(formBody2)
                .build()

            val call2: Call = CoolfireCoreApplication.client.newCall(request2)
            val response2: okhttp3.Response = call2.execute()
            val json2 = JSONObject(response2.body!!.string())

            if (json2.has("errors")) {
                val json2a = json2.getJSONArray("errors")
                val json2b = json2a.getJSONObject(0)
                val errMsg = json2b.getString("message")
                CoolfireCoreApplication.updateCurrentUser(
                    LoggedInUser(
                        CoolfireCoreApplication.currentUser.value!!.userId,
                        CoolfireCoreApplication.currentUser.value!!.displayName,
                        CoolfireCoreApplication.currentUser.value!!.accessToken,
                        CoolfireCoreApplication.currentUser.value!!.refreshToken,
                        CoolfireCoreApplication.currentUser.value!!.password,
                        CoolfireCoreApplication.currentUser.value!!.deviceId,
                        CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                        errMsg,
                        "Error",
                        CoolfireCoreApplication.currentUser.value!!.netId,
                        CoolfireCoreApplication.currentUser.value!!.netDesc,
                        CoolfireCoreApplication.currentUser.value!!.me
                    )
                )
            } else {
                CoolfireCoreApplication.updateCurrentUser(
                    LoggedInUser(
                        CoolfireCoreApplication.currentUser.value!!.userId,
                        CoolfireCoreApplication.currentUser.value!!.displayName,
                        CoolfireCoreApplication.currentUser.value!!.accessToken,
                        CoolfireCoreApplication.currentUser.value!!.refreshToken,
                        CoolfireCoreApplication.currentUser.value!!.password,
                        CoolfireCoreApplication.currentUser.value!!.deviceId,
                        CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                        "Session " + name + "of SessionType " + sessionType + " created.",
                        "SessionCreated",
                        CoolfireCoreApplication.currentUser.value!!.netId,
                        CoolfireCoreApplication.currentUser.value!!.netDesc,
                        CoolfireCoreApplication.currentUser.value!!.me
                    )
                )
            }

        }catch(e:Exception){
            CoolfireCoreApplication.updateCurrentUser(
                LoggedInUser(
                    CoolfireCoreApplication.currentUser.value!!.userId,
                    CoolfireCoreApplication.currentUser.value!!.displayName,
                    CoolfireCoreApplication.currentUser.value!!.accessToken,
                    CoolfireCoreApplication.currentUser.value!!.refreshToken,
                    CoolfireCoreApplication.currentUser.value!!.password,
                    CoolfireCoreApplication.currentUser.value!!.deviceId,
                    CoolfireCoreApplication.currentUser.value!!.tokenLifeTime,
                    e.message.toString(),
                    "Error",
                    CoolfireCoreApplication.currentUser.value!!.netId,
                    CoolfireCoreApplication.currentUser.value!!.netDesc,
                    CoolfireCoreApplication.currentUser.value!!.me
                )
            )
        }
    }
}
