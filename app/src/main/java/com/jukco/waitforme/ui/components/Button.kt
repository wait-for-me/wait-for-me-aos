package com.jukco.waitforme.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jukco.waitforme.data.mock.MockAuthProvider
import com.jukco.waitforme.data.auth.AuthProvider
import com.jukco.waitforme.ui.sign.sign_up.SignUpDto
import com.jukco.waitforme.ui.theme.WaitForMeTheme

@Composable
fun SocialSignIconButton(
    authProvider: AuthProvider,
    onSignInClicked: (SignUpDto?) -> Unit,
    buttonBorder: BorderStroke? = null,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    SocialSignContainer(
        authProvider = authProvider,
        onSignInResult = onSignInClicked,
        modifier = modifier,
    ) {
        Button(
            onClick = { this.onClick() },
            border = buttonBorder,
            colors = buttonColors,
            shape = CircleShape,
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialSignIconButton() {
    WaitForMeTheme {
        SocialSignIconButton(
            authProvider = MockAuthProvider,
            onSignInClicked = {},
        )
    }
}
