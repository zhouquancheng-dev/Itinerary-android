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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.data.Constants.LOGIN_DEEP_LINK
import com.example.common.util.ext.ClickExt.isFastClick
import com.example.common.data.DatastoreKey.IS_FIRST_TIME_LAUNCH
import com.example.common.util.ext.startDeepLink
import com.example.common.util.sp.DataStoreUtils.putBoolean
import com.example.ui.components.indicator.HorizontalPagerIndicator
import com.example.ui.components.VerticalSpacer
import com.example.ui.theme.JetItineraryTheme
import kotlinx.coroutines.launch

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

@Composable
private fun WelcomeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var snapshotCurrentPage by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { 3 }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            snapshotCurrentPage = page
        }
    }

    val navigateToMain: () -> Unit = {
        scope.launch {
            putBoolean(IS_FIRST_TIME_LAUNCH, false)
            context.startDeepLink(LOGIN_DEEP_LINK)
            (context as Activity).finish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        WelcomePager(pagerState)

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
            Text(
                if (snapshotCurrentPage != 2) stringResource(R.string.next) else stringResource(R.string.enter_app),
                fontSize = 17.sp
            )
        }

        TextButton(
            onClick = {
                backPressedDispatcher?.onBackPressed()
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = colorResource(R.color.welcome_color)
            )
        ) {
            Text(stringResource(R.string.cancel), fontSize = 17.sp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WelcomePager(pagerState: PagerState) {
    val defaultStyle = SpanStyle(color = MaterialTheme.colorScheme.onBackground)
    val textStyle = SpanStyle(color = colorResource(R.color.welcome_color))

    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> FirstPage(defaultStyle, textStyle)
            1 -> SecondPage(defaultStyle, textStyle)
            2 -> ThirdPage(defaultStyle, textStyle)
        }
    }
    VerticalSpacer(15.dp)
    HorizontalPagerIndicator(
        pagerState,
        pagerState.pageCount,
        activeColor = colorResource(R.color.welcome_color)
    )
}

@Composable
private fun FirstPage(defaultStyle: SpanStyle, textStyle: SpanStyle) {
    val theTextAbove = buildAnnotatedString {
            append("发布", defaultStyle)
            append("结伴同游", textStyle)
            append("消息", defaultStyle)
        }
    val theTextBelow = buildAnnotatedString {
        append("邀请", defaultStyle)
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

@Composable
private fun SecondPage(defaultStyle: SpanStyle, textStyle: SpanStyle) {
    val theTextAbove = buildAnnotatedString {
        append("定位", defaultStyle)
        append("附近", textStyle)
        append("好友", defaultStyle)
    }
    val theTextBelow = buildAnnotatedString {
        append("一起", defaultStyle)
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

@Composable
private fun ThirdPage(defaultStyle: SpanStyle, textStyle: SpanStyle) {
    val theTextAbove = buildAnnotatedString {
        append("加入兴趣", defaultStyle)
        append("社区", textStyle)
    }
    val theTextBelow = buildAnnotatedString {
        append("一起", defaultStyle)
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
