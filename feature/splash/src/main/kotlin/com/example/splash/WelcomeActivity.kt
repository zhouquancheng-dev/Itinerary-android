package com.example.splash

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.util.ClickUtils.isFastClick
import com.example.common.util.DataStoreUtils
import com.example.common.util.startDeepLink
import com.example.splash.ds.DsKey.IS_FIRST_TIME_LAUNCH
import com.example.ui.components.HorizontalPagerIndicator
import com.example.ui.components.VerticalSpacer
import com.example.ui.theme.JetItineraryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetItineraryTheme {
                WelcomeScreen()
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WelcomeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val dispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backPressedDispatcher = dispatcherOwner?.onBackPressedDispatcher

    var snapshotCurrentPage by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { 3 }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            snapshotCurrentPage = page
        }
    }

    val navigateToMain: () -> Unit = {
        scope.launch {
            DataStoreUtils.putBoolean(IS_FIRST_TIME_LAUNCH, false)
            val isLogin = withContext(Dispatchers.IO) {
                DataStoreUtils.getBooleanFlow("IS_LOGIN").first()
            }
            if (!isLogin) {
                startDeepLink(context, "login://main")
            } else {
                startDeepLink(context, "app://main")
            }
            (context as? Activity)?.finish()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> {
                    FirstPage()
                }
                1 -> {
                    SecondPage()
                }
                2 -> {
                    ThirdPage()
                }
            }
        }

        VerticalSpacer(15.dp)
        HorizontalPagerIndicator(
            pagerState,
            pagerState.pageCount,
            activeColor = colorResource(R.color.welcome_color)
        )

        Button(
            onClick = {
                if (snapshotCurrentPage == 2) {
                    navigateToMain()
                } else {
                    scope.launch {
                        if (!isFastClick()) {
                            pagerState.animateScrollToPage(snapshotCurrentPage + 1)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 35.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.welcome_color),
                contentColor = Color.White
            )
        ) {
            Text(if (snapshotCurrentPage != 2) "下一步" else "进入应用", fontSize = 17.sp)
        }

        TextButton(
            onClick = {
                backPressedDispatcher?.onBackPressed()
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = colorResource(R.color.welcome_color)
            )
        ) {
            Text("取消", fontSize = 17.sp)
        }
    }
}

@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
private fun FirstPage() {
    val textStyle = SpanStyle(color = colorResource(R.color.welcome_color))
    val theTextAbove = buildAnnotatedString {
            append("发布")
            append("结伴同游", textStyle)
            append("消息")
        }
    val theTextBelow = buildAnnotatedString {
        append("邀请")
        append("游伴一起旅行", textStyle)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.art_onboarding_1),
            contentDescription = "onboarding first"
        )
        VerticalSpacer(40.dp)
        Text(
            text = theTextAbove,
            modifier = Modifier.padding(end = 75.dp),
            fontSize = 23.sp
        )

        VerticalSpacer(10.dp)
        Text(
            text = theTextBelow,
            modifier = Modifier.padding(start = 75.dp),
            fontSize = 23.sp
        )
    }
}

@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
private fun SecondPage() {
    val textStyle = SpanStyle(color = colorResource(R.color.welcome_color))
    val theTextAbove = buildAnnotatedString {
        append("定位")
        append("附近", textStyle)
        append("好友")
    }
    val theTextBelow = buildAnnotatedString {
        append("一起")
        append("聊天交流", textStyle)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.art_onboarding_2),
            contentDescription = "onboarding first"
        )
        VerticalSpacer(40.dp)
        Text(
            text = theTextAbove,
            modifier = Modifier.padding(end = 75.dp),
            fontSize = 23.sp
        )

        VerticalSpacer(10.dp)
        Text(
            text = theTextBelow,
            modifier = Modifier.padding(start = 75.dp),
            fontSize = 23.sp
        )
    }
}

@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
private fun ThirdPage() {
    val textStyle = SpanStyle(color = colorResource(R.color.welcome_color))
    val theTextAbove = buildAnnotatedString {
        append("加入兴趣")
        append("社区", textStyle)
    }
    val theTextBelow = buildAnnotatedString {
        append("一起")
        append("谈天说地", textStyle)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.art_onboarding_3),
            contentDescription = "onboarding first"
        )
        VerticalSpacer(40.dp)
        Text(
            text = theTextAbove,
            modifier = Modifier.padding(end = 75.dp),
            fontSize = 23.sp
        )

        VerticalSpacer(10.dp)
        Text(
            text = theTextBelow,
            modifier = Modifier.padding(start = 75.dp),
            fontSize = 23.sp
        )
    }
}

private fun AnnotatedString.Builder.append(text: String, style: SpanStyle) {
    withStyle(style = style) {
        append(text)
    }
}
