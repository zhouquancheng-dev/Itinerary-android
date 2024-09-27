package com.example.home.vm

import androidx.lifecycle.ViewModel
import com.amap.api.location.AMapLocation
import com.example.common.listener.amap.AMapLocationListener
import com.example.common.listener.amap.LocationListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val aMapLocationListener: AMapLocationListener
) : ViewModel() {

    private val _locationInfo = MutableStateFlow<AMapLocation?>(null)
    val locationInfo = _locationInfo.asStateFlow()

    fun getLocation() {
        aMapLocationListener.setLocationListener(object : LocationListener {
            override fun onSuccess(aMapLocation: AMapLocation) {
                _locationInfo.value = aMapLocation
            }

            override fun onFailure() {

            }
        })
        aMapLocationListener.startLocation()
    }

}