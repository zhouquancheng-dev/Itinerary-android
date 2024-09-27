package com.zqc.itinerary

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.metrics.performance.JankStats
import com.aleyn.annotation.Route
import com.example.common.data.Router.ROUTER_MAIN_ACTIVITY
import com.example.common.di.network.NetworkMonitor
import com.example.common.di.timezone.TimeZoneMonitor
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.theme.LocalTimeZone
import com.zqc.itinerary.ui.ItineraryApp
import com.zqc.itinerary.ui.rememberAppState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@Route(path = ROUTER_MAIN_ACTIVITY)
class MainActivity : FragmentActivity() {

    /**
     * Lazily inject [JankStats], which is used to track jank throughout the app.
     */
    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberAppState(
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor
            )
            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalTimeZone provides currentTimeZone
            ) {
                JetItineraryTheme {
                    ItineraryApp(appState)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }
}