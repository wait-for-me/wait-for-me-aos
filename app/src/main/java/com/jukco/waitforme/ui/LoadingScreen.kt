package com.jukco.waitforme.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Loading....",
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxSize(),
    )
}