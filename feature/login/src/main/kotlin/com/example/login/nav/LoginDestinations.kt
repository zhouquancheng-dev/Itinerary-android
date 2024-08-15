package com.example.login.nav

import kotlinx.serialization.Serializable

@Serializable data object LoginHome

@Serializable data class VerifyCode(val phoneNumber: String)