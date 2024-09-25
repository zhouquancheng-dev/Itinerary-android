package com.example.im.components

import androidx.compose.foundation.Image
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.example.im.R

@Composable
fun FloatingButton(
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick) {
        Image(
            painter = painterResource(R.drawable.contacts_product_24dp),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = Color.White)
        )
    }
}