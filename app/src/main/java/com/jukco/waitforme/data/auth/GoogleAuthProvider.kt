package com.jukco.waitforme.data.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.jukco.waitforme.ui.sign.social.AuthUiProvider
import com.jukco.waitforme.ui.sign.social.GoogleAuthUiProvider

class GoogleAuthProvider(
    private val credentialManager: CredentialManager,
    private val serverClientId: String,
) : AuthProvider {
    @Composable
    override fun getUiProvider(): AuthUiProvider {
        val activityContext = LocalContext.current
        return GoogleAuthUiProvider(
            activityContext = activityContext,
            credentialManager = credentialManager,
            serverClientId = serverClientId,
        )
    }

    override suspend fun signOut() {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    override suspend fun withdraw() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}