package com.jukco.waitforme.ui.sign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jukco.waitforme.R
import com.jukco.waitforme.config.ApplicationClass
import com.jukco.waitforme.data.network.model.LocalSignInRequest
import com.jukco.waitforme.data.network.model.PhoneNumCheckRequest
import com.jukco.waitforme.data.network.model.Provider
import com.jukco.waitforme.data.network.model.SocialSignInRequest
import com.jukco.waitforme.data.auth.AuthProvider
import com.jukco.waitforme.data.repository.SignRepository
import com.jukco.waitforme.data.repository.TokenManager
import com.jukco.waitforme.ui.sign.sign_in.SignInEvent
import com.jukco.waitforme.ui.sign.sign_in.SignInForm
import com.jukco.waitforme.ui.sign.sign_in.SocialService
import com.jukco.waitforme.ui.sign.sign_up.SignUpForm
import com.jukco.waitforme.ui.sign.sign_up.CustomerOwner
import com.jukco.waitforme.ui.sign.sign_up.SignUpEvent
import com.jukco.waitforme.ui.sign.sign_up.SignUpDto
import com.jukco.waitforme.ui.sign.sign_up.toLocalReq
import com.jukco.waitforme.ui.sign.sign_up.toSocialReq
import com.jukco.waitforme.ui.util.ValidationChecker
import com.jukco.waitforme.ui.util.convertSecToMinSecFormat
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SignViewModel(
    private val signRepository: SignRepository,
    private val googleAuthProvider: AuthProvider,
    private val kakaoAuthProvider: AuthProvider,
    private val naverAuthProvider: AuthProvider,
    private val tokenManager: TokenManager,
) : ViewModel() {
    private var _currentLimitSec = INPUT_LIMIT_SEC_MAX

    var signInform by mutableStateOf(SignInForm())
        private set
    var signUpDto by mutableStateOf(SignUpDto())
        private set
    var signUpForm by mutableStateOf(SignUpForm())
        private set
    var errorMessage by mutableStateOf<Int?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var currentLimitTime by mutableStateOf(_currentLimitSec.convertSecToMinSecFormat())
        private set
    var enabledReRequestAuthnNum by mutableStateOf(true)
        private set
    var authnNumInputTimer by mutableStateOf<Job>(makeAuthnNumInputTimer())
        private set

    fun onSignInEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.InputId -> {
                signInform = signInform.copy(id = event.id)
            }

            is SignInEvent.InputPassword -> {
                signInform = signInform.copy(password = event.password)
            }

            is SignInEvent.OnSignInClicked -> {
                onSignInClicked(event.success)
            }

            is SignInEvent.OnSocialSignInClicked -> {
                onSocialSignInClicked(event.user, event.success, event.register)
            }

            is SignInEvent.Reset -> {
                reset()
            }
        }
    }

    fun onSignUpEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.InputPhoneNumber -> {
                signUpForm = signUpForm.copy(phoneNumber = event.phoneNum)
            }

            is SignUpEvent.SubmitPhoneNumber -> {
                if (checkId(signUpForm.phoneNumber)) {
                    viewModelScope.launch {
                        if (requestAuthnNum(signUpForm.phoneNumber)) {
                            signUpForm = signUpForm.copy(phoneNumberSubmitted = true)
                            authnNumInputTimer.start()
                        } else {
                            // TODO : 인증번호 발송에 실패했다 안내
                        }
                    }
                }
            }

            is SignUpEvent.InputAuthnNum -> {
                signUpForm = signUpForm.copy(authenticationNum = event.number)
            }

            is SignUpEvent.ReRequestAuthnNum -> {
                viewModelScope.launch {
                    if (requestAuthnNum(signUpForm.phoneNumber)) {
                        signUpForm = signUpForm.copy(phoneNumberSubmitted = true)
                        resetAuthnNumInputTimer()
                        authnNumInputTimer.start()

                        enabledReRequestAuthnNum = false
                        delay(15000L)
                        enabledReRequestAuthnNum = true
                    } else {
                        // TODO : 인증번호 발송에 실패했다 안내
                    }
                }
            }

            is SignUpEvent.SubmitAuthnNum -> {
                viewModelScope.launch {
                    if (checkPhoneNumberValidity(signUpForm.phoneNumber, signUpForm.authenticationNum)) {
                        signUpForm = signUpForm.copy(authenticationNumSubmitted = true)
                        resetAuthnNumInputTimer()
                        enabledReRequestAuthnNum = false
                    } else {
                        //TODO : 인증실패 안내
                    }
                }
            }

            is SignUpEvent.InputPassword -> {
                signUpForm = signUpForm.copy(
                    password = event.password,
                    passwordVerification = checkPassword(event.password),
                )
            }

            is SignUpEvent.InputConfirmPassword -> {
                val passwordConfirmed = (signUpForm.password == event.confirmPassword)

                signUpForm = signUpForm.copy(
                    confirmPassword = event.confirmPassword,
                    passwordSubmitted = (signUpForm.passwordVerification == true) && passwordConfirmed,
                )
                errorMessage = if (passwordConfirmed) null else R.string.error_password_confirm
            }

            is SignUpEvent.SubmitCredentials -> {
                signUpDto = signUpDto.copy(phoneNumber = signUpForm.phoneNumber)
            }

            is SignUpEvent.InputName -> {
                signUpForm = signUpForm.copy(
                    name = event.name,
                    nameSubmitted = checkName(event.name),
                )
            }

            is SignUpEvent.CheckDuplicateName -> {
                checkDuplicateName()
            }

            is SignUpEvent.ChooseCustomerOrOwner -> {
                signUpDto = signUpDto.copy(isOwner = event.choice)
            }

            is SignUpEvent.SignUp -> {
                onSignUpClicked(event.success, { /*TODO : Fail */ })
            }
        }
    }

    fun getSocialSign(service: SocialService) =
        when (service) {
            is SocialService.Google -> {
                googleAuthProvider
            }

            is SocialService.Kakao -> {
                kakaoAuthProvider
            }

            is SocialService.Naver -> {
                naverAuthProvider
            }
        }

    private fun reset() {
        signInform = SignInForm()
        signUpDto = SignUpDto()
        signUpForm = SignUpForm()
        errorMessage = null
        resetAuthnNumInputTimer()
    }

    private fun onSignInClicked(success: () -> Unit) {
        if (checkId(signInform.id) and checkPassword(signInform.password)) {
            signInform = signInform.copy(hasError = false)
            localSignIn(signInform.id, signInform.password, success)
        } else {
            signInform = signInform.copy(hasError = true)
        }
    }

    private fun onSocialSignInClicked(user: SignUpDto?, success: () -> Unit, register: () -> Unit) {
        if (user != null) {
            signUpDto = user
            socialSignIn(user.provider, user.snsId, success, register)
        }
    }

    private fun onSignUpClicked(success: () -> Unit, fail: () -> Unit) {
        if (signUpDto.provider == Provider.LOCAL) {
            localSignUp(success, fail)
        } else {
            socialSignUp(success, fail)
        }
    }

    private fun localSignIn(id: String, password: String, success: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            delay(3000) // TODO : 서버와 연결 후에는 지울 것. 기다리는 최대 시간이 있어야 한다.

            try {
                val request = LocalSignInRequest(id, password)
                val response = signRepository.localSignIn(request)
                if (response.isSuccessful) {
                    withContext(Dispatchers.IO) {
                        tokenManager.saveToken(Provider.LOCAL, response.body()!!)
                    }
                    success()
                    return@launch
                }
                // TODO : 30X, 40X 오류 처리
            } catch (e: IOException) {
                // TODO : 50X 오류 처리
            } finally {
                isLoading = false
            }
        }
    }

    private fun socialSignIn(
        provider: Provider,
        snsId: String,
        success: () -> Unit,
        register: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            delay(3000) // TODO : 서버와 연결 후에는 지울 것. 기다리는 최대 시간이 있어야 한다.

            try {
                val request = SocialSignInRequest(provider, snsId)
                val response = signRepository.socialSignIn(request)
                if (response.isSuccessful) {
                    when (response.code()) {
                        200 -> {
                            withContext(Dispatchers.IO) {
                                tokenManager.saveToken(provider, response.body()!!)
                            }
                            success()
                        }

                        204 -> {
                            register()
                        }
                    }
                    return@launch
                }
                // TODO : 30X, 40X 오류 처리
            } catch (e: IOException) {
                // TODO : 50X 오류 처리
            } finally {
                isLoading = false
            }
        }
    }

    private fun localSignUp(success: () -> Unit, fail: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            delay(3000) // TODO : 서버와 연결 후에는 지울 것. 기다리는 최대 시간이 있어야 한다.

            try {
                val request = signUpDto.toLocalReq()
                val response = signRepository.localSignUp(request)
                if (response.isSuccessful) {
                    withContext(Dispatchers.IO) {
                        tokenManager.saveToken(Provider.LOCAL, response.body()!!)
                    }
                    success()
                    return@launch
                }
                // TODO : 30X, 40X 오류 처리
                fail()
            } catch (e: IOException) {
                // TODO : 50X 오류 처리
                fail()
            } finally {
                isLoading = false
            }
        }
    }

    private fun socialSignUp(success: () -> Unit, fail: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            delay(3000) // TODO : 서버와 연결 후에는 지울 것. 기다리는 최대 시간이 있어야 한다.

            try {
                val request = signUpDto.toSocialReq()
                val response = signRepository.socialSignUp(request)
                if (response.isSuccessful) {
                    withContext(Dispatchers.IO) {
                        tokenManager.saveToken(request.provider, response.body()!!)
                    }
                    success()
                    return@launch
                }
                // TODO : 30X, 40X 오류 처리
                fail()
            } catch (e: IOException) {
                // TODO : 50X 오류 처리
                fail()
            } finally {
                isLoading = false
            }
        }
    }

    private fun checkDuplicateName() {
        viewModelScope.launch {
            isLoading = true
            delay(3000) // TODO : 서버와 연결 후에는 지울 것. 기다리는 최대 시간이 있어야 한다.

            try {
                val response = signRepository.checkUniqueName(signUpForm.name)
                if (response.isSuccessful) {
                    response.body()?.also { isUnique ->
                        if (isUnique) {
                            errorMessage = null
                            signUpDto = signUpDto.copy(name = signUpForm.name)
                        } else {
                            errorMessage = R.string.error_nickname_duplicate
                            signUpForm = signUpForm.copy(
                                nameSubmitted = false,
                            )
                        }
                    }
                    return@launch
                }
                // TODO : 30X, 40X 오류 처리
            } catch (e: IOException) {
                // TODO : 50X 오류 처리
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun requestAuthnNum(phoneNumber: String): Boolean {
        return viewModelScope.async {
            try {
                val response = signRepository.requestAuthnNum(phoneNumber)
                if (response.isSuccessful) {
                    response.body()!!
                } else {
                    // TODO : 30X, 40X 오류 처리
                    false
                }
            } catch (e: IOException) {
                // TODO : 50X 오류 처리
                false
            }
        }.await()
    }

    private suspend fun checkPhoneNumberValidity(phoneNumber: String, authnNum: String): Boolean {
        return if (checkAuthnNum(authnNum) == false) {
            false
        } else {
            viewModelScope.async {
                try {
                    val request = PhoneNumCheckRequest(phoneNumber = phoneNumber, authnNum = authnNum)
                    val response = signRepository.checkPhoneNumberValidity(request)
                    if (response.isSuccessful) {
                        response.body()!!
                    } else {
                        // TODO : 30X, 40X 오류 처리
                        false
                    }
                } catch (e: IOException) {
                    // TODO : 50X 오류 처리
                    false
                }
            }.await()
        }
    }

    private fun checkId(id: String): Boolean =
        ValidationChecker.checkIdValidation(id).apply {
            errorMessage = second
        }.first

    private fun checkAuthnNum(authnNum: String): Boolean {
        val checked = authnNum.trim()

        return when {
            checked.isBlank() -> false
            checked.length != 6 -> false
            !checked.isDigitsOnly() -> false
            else -> true
        }

    }

    private fun checkPassword(password: String) =
        ValidationChecker.checkPasswordValidation(password).apply {
            errorMessage = second
        }.first

    private fun checkName(name: String) = ValidationChecker.checkNameValidation(name).apply {
        errorMessage = second
    }.first

    private fun makeAuthnNumInputTimer() = viewModelScope.launch (start = CoroutineStart.LAZY) {
        repeat(INPUT_LIMIT_SEC_MAX) {
            delay(1000L)
            _currentLimitSec--
            currentLimitTime = _currentLimitSec.convertSecToMinSecFormat()
        }
    }

    private fun resetAuthnNumInputTimer() {
        authnNumInputTimer.cancel()
        _currentLimitSec = INPUT_LIMIT_SEC_MAX
        authnNumInputTimer = makeAuthnNumInputTimer()
    }

    companion object {
        const val INPUT_LIMIT_SEC_MAX = 120
        val CUSTOMER_OWNER = arrayOf(
            CustomerOwner(R.string.customer_title, R.string.customer_description, false),
            CustomerOwner(R.string.owner_title, R.string.owner_description, true),
        )

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ApplicationClass)
                val signRepository = application.container.signRepository
                val googleAuthProvider = application.container.googleAuthProvider
                val kakaoAuthProvider = application.container.kakaoAuthProvider
                val naverAuthProvider = application.container.naverAuthProvider
                val tokenManager = application.container.tokenManager

                SignViewModel(
                    signRepository,
                    googleAuthProvider,
                    kakaoAuthProvider,
                    naverAuthProvider,
                    tokenManager
                )
            }
        }
    }
}
