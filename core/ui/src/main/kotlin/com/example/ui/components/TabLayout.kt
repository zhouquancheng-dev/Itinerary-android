package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.PrimaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JetItineraryTheme

@Composable
fun CustomScrollableTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    edgePadding: Dp = 8.dp,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    indicatorColor: Color = TabRowDefaults.primaryContentColor,
    selectedContentColor: Color = TabRowDefaults.primaryContentColor,
    unselectedContentColor: Color = LocalContentColor.current
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = containerColor,
        contentColor = selectedContentColor,
        edgePadding = edgePadding,
        indicator = {
            PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex, false),
                width = 32.dp,
                height = 5.dp,
                color = indicatorColor,
                shape = RoundedCornerShape(8.dp)
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(index) },
                modifier = Modifier.semantics { contentDescription = tabs[selectedTabIndex] },
                selectedContentColor = selectedContentColor,
                unselectedContentColor = unselectedContentColor,
                text = {
                    Text(
                        text = title,
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontSize = 16.sp
                    )
                }
            )
        }
    }
}

@Composable
fun CustomScrollableTabColumn(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    containerColor: Color = Color.White
) {
    val scrollState = rememberScrollState()
    val tabHeight = 48.dp
    val tabIndicatorWidth = 5.dp
    val tabIndicatorHeight = 20.dp

    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
            .background(color = containerColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            Surface(
                onClick = { onTabSelected(index) },
                modifier = Modifier.padding(10.dp)
                    .semantics { contentDescription = tabs[selectedTabIndex] },
                shape = RoundedCornerShape(50.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (index == selectedTabIndex) {
                        Canvas(
                            modifier = Modifier
                                .width(tabIndicatorWidth)
                                .height(tabHeight)
                                .padding(top = (tabHeight - tabIndicatorHeight) / 2)
                        ) {
                            drawRoundRect(
                                color = Color(0xFFD5FD88),
                                size = Size(tabIndicatorWidth.toPx(), tabIndicatorHeight.toPx()),
                                cornerRadius = CornerRadius(tabIndicatorWidth.toPx() / 2)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(tabIndicatorWidth))
                    }

                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = if (index == selectedTabIndex) textColor else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTabLayout(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    containerColor: Color = Color.White
) {
    val tabHeight = 48.dp

    SecondaryScrollableTabRow(selectedTabIndex,
        modifier = modifier.height(tabHeight),
        containerColor = containerColor,
        contentColor = TabRowDefaults.secondaryContentColor,
        edgePadding = 8.dp,
        indicator = {
            // 自定义 indicator 待实现
        },
        divider = @Composable { HorizontalDivider() },
        tabs = {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { onTabSelected(index) },
                    selectedContentColor = Color.Transparent,
                    unselectedContentColor = Color.Transparent,
                    text = {
                        Text(
                            text = title,
                            modifier = Modifier.padding(vertical = 8.dp),
                            fontSize = 16.sp,
                            color = if (index == selectedTabIndex) textColor else Color.Gray
                        )
                    }
                )
            }
        })
}

@PreviewLightDark
@Composable
private fun ScrollableTabPreview() {
    JetItineraryTheme {
        CustomScrollableTabRow(
            tabs = listOf("TAB1", "TAB2", "TAB3"),
            selectedTabIndex = 1,
            onTabSelected = {},
            containerColor = Color.White
        )
    }
}