package com.example.home.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.util.ext.startActivity
import com.example.common.util.permissionUtil.AllowPermissionUseCase
import com.example.common.util.permissionUtil.ext.Constant.ACCESS_FINE_LOCATION
import com.example.home.R
import com.example.home.activity.QWeatherActivity
import com.example.home.utils.getSky
import com.example.home.vm.HomeViewModel
import com.example.ui.components.noRippleClickable

@Composable
fun HomeScreen(hvm: HomeViewModel) {
    val context = LocalContext.current
//    val density = LocalDensity.current
    val locationInfo by hvm.locationInfo.collectAsStateWithLifecycle()
    var shouldLocationPermission by remember { mutableStateOf(shouldLocationPermission(context)) }

    val address = "${locationInfo?.city.orEmpty()}${locationInfo?.district.orEmpty()}${locationInfo?.street.orEmpty()}"

    val realtimeWeather by hvm.realtimeResponse.collectAsStateWithLifecycle()

    LaunchedEffect(shouldLocationPermission) {
        if (shouldLocationPermission && locationInfo == null) {
            hvm.getLocation()
        }
    }

    val requestPermission: () -> Unit = {
        AllowPermissionUseCase.requestPermission(context as FragmentActivity, ACCESS_FINE_LOCATION) {
            shouldLocationPermission = true
            hvm.getLocation()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .drawWithCache {
//                    val pxValue = with(density) { 350.dp.toPx() }
//                    val brush = Brush.verticalGradient(
//                        colorStops = homeBgBrush.toTypedArray(),
//                        startY = 0f,
//                        endY = pxValue
//                    )
//                    onDrawBehind {
//                        drawRect(
//                            brush = brush,
//                            size = Size(size.width, pxValue),
//                            alpha = 1f
//                        )
//                    }
//                }
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 25.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text = "欢迎回来！",
                        fontSize = 25.sp
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .noRippleClickable { requestPermission() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_orientation),
                            contentDescription = "location",
                            modifier = Modifier.size(15.dp),
                            colorFilter = ColorFilter.tint(ColorFF6979F8)
                        )
                        Text(
                            text = if (shouldLocationPermission) address.ifEmpty { "定位中" } else "点击授权您的定位权限",
                            color = ColorFF6979F8,
                            fontSize = 16.sp,
                            maxLines = 2,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                realtimeWeather?.let { realtime ->
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.Bottom)
                            .noRippleClickable {
                                val extra = bundleOf("q_weather_url" to realtime.fxLink)
                                context.startActivity<QWeatherActivity>(extras = extra)
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                    ) {
                        Image(
                            painter = painterResource(getSky(realtime.now?.icon ?: "999").iconFill),
                            contentDescription = "weatherIcon",
                            modifier = Modifier.size(55.dp),
                            colorFilter = ColorFilter.tint(ColorFFFE8E4D)
                        )
                        Text(
                            text = "${realtime.now?.temp}°",
                            modifier = Modifier.align(Alignment.CenterVertically),
                            color = ColorFFFE8E4D,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun shouldLocationPermission(context: Context, permission: String = ACCESS_FINE_LOCATION): Boolean {
    return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, permission)
}