package com.coolfire.pizzanow.data.model

/**
 *Data class that captures user information for logged in users
 */
data class LoggedInUser(
    var userId:String="",
    var displayName:String="",
    var accessToken:String="",
    var refreshToken:String="",
    var password:String="",
    var deviceId:String="",
    var tokenLifeTime:String="",
    var errorMessage:String="",
    var status:String="",
    var netId:String="",
    var netDesc: String="",
    var me: String=""
)