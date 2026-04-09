package com.jukco.waitforme.data.mock

import androidx.compose.runtime.Composable
import com.jukco.waitforme.data.auth.AuthProvider
import com.jukco.waitforme.ui.sign.social.AuthUiProvider
import com.jukco.waitforme.ui.sign.social.MockAuthUiProvider

object MockAuthProvider : AuthProvider {
    @Composable
    override fun getUiProvider(): AuthUiProvider = MockAuthUiProvider

    override suspend fun signOut() = Unit

    override suspend fun withdraw() = Unit
}