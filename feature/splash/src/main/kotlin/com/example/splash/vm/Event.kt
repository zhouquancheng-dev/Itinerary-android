package com.example.splash.vm

sealed class Event {
    data object FinishAc : Event()
    data object StartWelcome : Event()
    data object StartMain : Event()
    data object StartLogin : Event()
}