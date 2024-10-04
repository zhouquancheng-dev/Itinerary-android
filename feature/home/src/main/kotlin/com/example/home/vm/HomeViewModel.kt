package com.example.home.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amap.api.location.AMapLocation
import com.example.common.listener.amap.AMapLocationListener
import com.example.common.listener.amap.LocationListener
import com.example.model.SUCCESS_CODE_STR
import com.example.model.weather.RealtimeResponse
import com.example.network.ItineraryNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val aMapLocationListener: AMapLocationListener,
    private val networkApi: ItineraryNetwork
) : ViewModel() {

    private val _locationInfo = MutableStateFlow<AMapLocation?>(null)
    val locationInfo = _locationInfo.asStateFlow()

    private val _realtimeWeather = MutableStateFlow<RealtimeResponse?>(null)
    val realtimeResponse = _realtimeWeather.asStateFlow()

    fun getLocation() {
        aMapLocationListener.setLocationListener(object : LocationListener {
            override fun onSuccess(aMapLocation: AMapLocation) {
                _locationInfo.value = aMapLocation
                realtimeWeather("${aMapLocation.longitude},${aMapLocation.latitude}")
            }

            override fun onFailure() {
                // 定位失败
            }
        })
        aMapLocationListener.startLocation()
    }

    private fun realtimeWeather(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = networkApi.realtimeWeather(location)
                if (response.code == SUCCESS_CODE_STR) {
                    _realtimeWeather.value = response
                }
            } catch (e: Exception) {
                Log.e("realtimeWeather", "Error fetching weather data", e)
            }
        }
    }
}