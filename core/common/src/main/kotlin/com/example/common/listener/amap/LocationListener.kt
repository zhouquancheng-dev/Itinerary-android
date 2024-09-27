package com.example.common.listener.amap

import com.amap.api.location.AMapLocation

interface LocationListener {
    fun onSuccess(aMapLocation: AMapLocation)
    fun onFailure()
}