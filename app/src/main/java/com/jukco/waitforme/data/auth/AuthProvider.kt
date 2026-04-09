package com.jukco.waitforme.data.auth

import androidx.compose.runtime.Composable
import com.jukco.waitforme.ui.sign.social.AuthUiProvider

interface AuthProvider {
    @Composable
    fun getUiProvider(): AuthUiProvider

    suspend fun signOut()

    suspend fun withdraw()
}