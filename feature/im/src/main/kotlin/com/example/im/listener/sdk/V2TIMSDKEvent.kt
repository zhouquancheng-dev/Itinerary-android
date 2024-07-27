package com.example.im.listener.sdk

import com.tencent.imsdk.v2.V2TIMUserFullInfo

data class Connecting(val message: String)

data class ConnectSuccess(val message: String)

data class ConnectFailed(val code: Int, val error: String?)

data class KickedOffline(val message: String)

data class UserSigExpired(val message: String)

data class SelfInfoUpdated(val info: V2TIMUserFullInfo?)