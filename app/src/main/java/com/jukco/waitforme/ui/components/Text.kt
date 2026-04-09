package com.jukco.waitforme.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.jukco.waitforme.R
import com.jukco.waitforme.ui.theme.ErrorRed
import com.jukco.waitforme.ui.theme.MainBlack
import com.jukco.waitforme.ui.theme.NotoSansKR

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ){
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = ErrorRed,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = message,
            color = ErrorRed,
        )
    }
}

@Composable
fun Title(
    @DrawableRes imageSrc: Int,
    text: AnnotatedString,
    action: () -> Unit = {},
    @DrawableRes actionIconSrc: Int? = null,
    @StringRes actionDescription: Int? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Image(painter = painterResource(imageSrc), contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MainBlack,
                lineHeight = 18.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                letterSpacing = (-0.05).em,
            ),
        )
        Spacer(modifier = Modifier.weight(1f))
        if (actionIconSrc != null) {
            Image(
                painter = painterResource(actionIconSrc),
                contentDescription = if (actionDescription != null) stringResource(actionDescription) else null,
                Modifier.clickable { action() },
            )
        }
    }
}