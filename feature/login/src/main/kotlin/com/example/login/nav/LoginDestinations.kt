package com.example.login.nav

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable @Serializable data object LoginHome

@Immutable @Serializable data class VerifyCode(val phoneNumber: String)