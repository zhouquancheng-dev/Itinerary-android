package com.example.home.ui

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.util.permissionUtil.AllowPermissionUseCase
import com.example.home.vm.HomeViewModel

@Composable
fun HomeScreen(hvm: HomeViewModel) {
    val context = LocalContext.current
    val locationInfo by hvm.locationInfo.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        AllowPermissionUseCase.requestPermission(context as FragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION) {
            hvm.getLocation()
        }
    }

    val longitude = locationInfo?.longitude.toString()
    val latitude = locationInfo?.latitude.toString()
    val address = locationInfo?.address

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text(text = "经度: $longitude")
        Text(text = "纬度: $latitude")
        Text(text = "全地址: $address")
    }
}