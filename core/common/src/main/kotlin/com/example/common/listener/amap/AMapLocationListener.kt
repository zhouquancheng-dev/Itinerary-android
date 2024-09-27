package com.example.common.listener.amap

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AMapLocationListener @Inject constructor(
    @ApplicationContext private val context: Context
) : DefaultLifecycleObserver {
    private val tag = AMapLocationListener::class.simpleName

    // 声明AMapLocationClient类对象
    private var mLocationClient: AMapLocationClient? = null
    // 声明mLocationOption对象
    private var mLocationOption: AMapLocationClientOption? = null

    private lateinit var locationListener: LocationListener

    fun setLocationListener(locationListener: LocationListener) {
        this.locationListener = locationListener
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        destroyLocation()
    }

    // 初始化并启动定位
    fun startLocation() {
        Log.i(tag, "开始初始化定位")
        try {
            configurePrivacySettings()
            initLocationClient()
            setLocationOptions()
            mLocationClient?.startLocation()
            Log.i(tag, "启动定位")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun configurePrivacySettings() {
        AMapLocationClient.updatePrivacyShow(context, true, true)
        AMapLocationClient.updatePrivacyAgree(context, true)
    }

    private fun initLocationClient() {
        try {
            mLocationClient = AMapLocationClient(context).apply {
                setLocationListener(mAMapLocationListener)
            }
        } catch (e: Exception) {
            Log.e(tag, "initLocationClient: ", e)
        }
    }

    private fun setLocationOptions() {
        mLocationOption = AMapLocationClientOption().apply {
            // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            // 设置定位间隔,单位毫秒,默认为2000ms,最小1000ms
            interval = 1000
            // 获取一次定位结果
            isOnceLocation = true
            // 获取最近3s内精度最高的一次定位结果：
            // 设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果
            // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false
            isOnceLocationLatest = true
            // 设置是否返回地址信息（默认返回地址信息），如果isNeedAddress设置为false，
            // 则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息
            isNeedAddress = true
            // 设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒
            httpTimeOut = 20000
            // 缓存机制，当开启定位缓存功能，在高精度模式和低功耗模式下进行的网络定位结果均会生成本地缓存，
            // 不区分单次定位还是连续定位。GPS定位结果不会被缓存
            isLocationCacheEnable = false
        }
        mLocationClient?.setLocationOption(mLocationOption)
    }

    // 停止定位
    private fun stopLocation() {
        mLocationClient?.stopLocation()
        Log.i(tag, "停止定位")
    }

    /**
     * 销毁定位客户端，同时销毁本地定位服务；销毁定位客户端之后，若要重新开启定位需重新New一个AMapLocationClient对象。
     * 如果AMapLocationClient是在当前Activity实例化的，
     * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
     */
    private fun destroyLocation() {
        mLocationClient?.run {
            stopLocation()
            unRegisterLocationListener(mAMapLocationListener)
            onDestroy()
        }
        mLocationClient = null
        mLocationOption = null
        Log.i(tag, "销毁定位客户端")
    }

    /**
     * 定位监听器
     *
     * ```
     *     val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *     val date = Date(aMapLocation.time)
     *     Log.i("AMapLocationInfo", "定位时间: ${simpleDateFormat.format(date)}")
     *     Log.i("AMapLocationInfo", "定位来源: ${aMapLocation.locationType}")
     *     Log.i("AMapLocationInfo", "定位精度信息: ${aMapLocation.accuracy}")
     *     Log.i("AMapLocationInfo", "纬度 lat: ${aMapLocation.latitude} 经度 lng: ${aMapLocation.longitude}")
     *     Log.i("AMapLocationInfo", "全地址: ${aMapLocation.address}")
     *     Log.i("AMapLocationInfo", "国家信息: ${aMapLocation.country}")
     *     Log.i("AMapLocationInfo", "省信息: ${aMapLocation.province}")
     *     Log.i("AMapLocationInfo", "城市信息: ${aMapLocation.city}")
     *     Log.i("AMapLocationInfo", "城区信息: ${aMapLocation.district}")
     *     Log.i("AMapLocationInfo", "街道信息: ${aMapLocation.street}")
     *     Log.i("AMapLocationInfo", "街道门牌号信息: ${aMapLocation.streetNum}")
     *     Log.i("AMapLocationInfo", "城市编码: ${aMapLocation.cityCode}")
     *     Log.i("AMapLocationInfo", "地区编码: ${aMapLocation.adCode}")
     *     Log.i("AMapLocationInfo", "当前定位点的AOI信息: ${aMapLocation.aoiName}")
     *     Log.i("AMapLocationInfo", "当前室内定位的建筑物Id: ${aMapLocation.buildingId}")
     *     Log.i("AMapLocationInfo", "当前室内定位的楼层: ${aMapLocation.floor}")
     *     Log.i("AMapLocationInfo", "GPS当前的状态: ${aMapLocation.gpsAccuracyStatus}")
     *     ```
     */
    private val mAMapLocationListener = AMapLocationListener { aMapLocation ->
        aMapLocation?.let {
            if (it.errorCode == 0) {
                if (this::locationListener.isInitialized) {
                    locationListener.onSuccess(it)
                    stopLocation()
                }
                Log.i(tag, "定位成功: 经度: ${it.longitude}, 纬度: ${it.latitude}")
            } else {
                handleLocationError(it)
            }
        } ?: Log.i(tag, "定位失败: aMapLocation is null")
    }

    private fun handleLocationError(aMapLocation: AMapLocation) {
        Log.e(
            tag, "定位错误, 错误码: ${aMapLocation.errorCode}, 错误信息: ${aMapLocation.errorInfo}, " +
                    "详细信息: ${aMapLocation.locationDetail}"
        )
        if (this::locationListener.isInitialized) {
            locationListener.onFailure()
        }
    }

}