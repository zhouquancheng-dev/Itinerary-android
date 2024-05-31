package com.zqc.itinerary.ui.welcome

import android.content.res.Configuration
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zqc.itinerary.R
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.theme.MyLightBlueTextColor
import kotlinx.coroutines.launch

/**
 * 欢迎页
 */
@ExperimentalFoundationApi
@Composable
fun WelcomeScreen(
    onNextClick: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0) { 3 }

    val scope = rememberCoroutineScope()
    val dispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backPressedDispatcher = dispatcherOwner?.onBackPressedDispatcher

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.safeDrawingPadding(),
        userScrollEnabled = false
    ) { index ->
        when (index) {
            0 -> {
                WelcomePage(
                    pagerState = pagerState,
                    image = painterResource(R.drawable.bg_welcome_first),
                    imageDescription = stringResource(R.string.welcomeFirst_contentDescription),
                    contentText = stringArrayResource(R.array.welcomeFirstPage_arrayText),
                    nextButtonText = stringResource(R.string.next_text),
                    cancelButtonText = stringResource(R.string.cancel_text),
                    onNextClick = {
                        scope.launch {
                            pagerState.scrollToPage(1)
                        }
                    },
                    onCancelClick = {
                        backPressedDispatcher?.onBackPressed()
                    }
                )
            }

            1 -> {
                WelcomePage(
                    pagerState = pagerState,
                    image = painterResource(R.drawable.bg_welcome_second),
                    imageDescription = stringResource(R.string.welcomeSecond_contentDescription),
                    contentText = stringArrayResource(R.array.welcomeSecondPage_arrayText),
                    nextButtonText = stringResource(R.string.next_text),
                    cancelButtonText = stringResource(R.string.back_text),
                    onNextClick = {
                        scope.launch {
                            pagerState.scrollToPage(2)
                        }
                    },
                    onCancelClick = {
                        scope.launch {
                            pagerState.scrollToPage(0)
                        }
                    }
                )
            }

            2 -> {
                WelcomePage(
                    pagerState = pagerState,
                    image = painterResource(R.drawable.bg_welcome_third),
                    imageDescription = stringResource(R.string.welcomeThird_contentDescription),
                    contentText = stringArrayResource(R.array.welcomeThirdPage_arrayText),
                    nextButtonText = stringResource(R.string.enterApp_text),
                    cancelButtonText = stringResource(R.string.back_text),
                    onNextClick = {
                        onNextClick()
                    },
                    onCancelClick = {
                        scope.launch {
                            pagerState.scrollToPage(1)
                        }
                    }
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun WelcomePage(
    pagerState: PagerState,
    image: Painter,
    imageDescription: String,
    contentText: Array<String>,
    nextButtonText: String,
    cancelButtonText: String,
    onNextClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val aboveText =
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 23.sp)) {
                append(contentText[0])
            }
            withStyle(style = SpanStyle(color = MyLightBlueTextColor, fontSize = 23.sp)) {
                append(contentText[1])
            }
            withStyle(style = SpanStyle(fontSize = 23.sp)) {
                append(contentText[2])
            }
        }
    val belowText =
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 23.sp)) {
                append(contentText[3])
            }
            withStyle(style = SpanStyle(color = MyLightBlueTextColor, fontSize = 23.sp)) {
                append(contentText[4])
            }
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = image,
            contentDescription = imageDescription,
            modifier = Modifier.padding(top = 85.dp)
        )

        Text(
            text = aboveText,
            modifier = Modifier.padding(top = 40.dp, start = 77.dp, end = 114.dp)
        )

        Text(
            text = belowText,
            modifier = Modifier.padding(top = 11.dp, start = 139.dp, end = 52.dp)
        )

//        HorizontalPagerIndicator(
//            pagerState = pagerState,
//            pageCount = pagerState.pageCount,
//            modifier = Modifier.padding(top = 45.dp)
//        )

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .padding(top = 18.dp, start = 32.dp, end = 32.dp)
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MyLightBlueTextColor)
        ) {
            Text(text = nextButtonText, fontSize = 17.sp, color = Color.White)
        }

        TextButton(
            onClick = onCancelClick, modifier = Modifier.padding(top = 25.dp)
        ) {
            Text(
                text = cancelButtonText,
                color = MyLightBlueTextColor,
                fontSize = 17.sp
            )
        }
    }
}

@ExperimentalFoundationApi
@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    locale = "zh-rCN",
    showBackground = true,
    device = "spec:width=1440px,height=3200px,dpi=560"
)
@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "zh-rCN",
    showBackground = true,
    device = "spec:width=1440px,height=3200px,dpi=560"

)
@Composable
fun WelcomeScreenPreview() {
    JetItineraryTheme {
        WelcomeScreen {  }
    }
}