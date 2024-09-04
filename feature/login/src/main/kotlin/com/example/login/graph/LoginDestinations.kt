package com.example.login.graph

import kotlinx.serialization.Serializable

@Serializable
data object LoginHome

@Serializable
data class VerifyCode(val phoneNumber: String)