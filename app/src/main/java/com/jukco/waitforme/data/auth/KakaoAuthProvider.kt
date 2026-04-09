package com.jukco.waitforme.data.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.jukco.waitforme.ui.sign.social.AuthUiProvider
import com.jukco.waitforme.ui.sign.social.KakaoAuthUiProvider
import com.kakao.sdk.user.UserApiClient

class KakaoAuthProvider : AuthProvider {
    @Composable
    override fun getUiProvider(): AuthUiProvider {
        val activityContext = LocalContext.current
        return KakaoAuthUiProvider(activityContext)
    }

    override suspend fun signOut() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            } else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    override suspend fun withdraw() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e(TAG, "연결 끊기 실패", error)
            }
            else {
                Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }

    companion object {
        private const val TAG = "KakaoAuthProvider"
    }
}