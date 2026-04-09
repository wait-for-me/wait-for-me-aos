package com.jukco.waitforme.ui.my_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jukco.waitforme.R
import com.jukco.waitforme.config.ApplicationClass
import com.jukco.waitforme.data.network.model.Provider
import com.jukco.waitforme.data.network.model.UserInfoRequest
import com.jukco.waitforme.data.network.model.UserInfoRes
import com.jukco.waitforme.data.auth.AuthProvider
import com.jukco.waitforme.data.repository.SignRepository
import com.jukco.waitforme.data.repository.TokenManager
import com.jukco.waitforme.data.repository.UserRepository
import com.jukco.waitforme.ui.util.ValidationChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MyInfoViewModel(
    private val userRepository: UserRepository,
    private val signRepository: SignRepository,
    private val googleAuthProvider: AuthProvider,
    private val kakaoAuthProvider: AuthProvider,
    private val naverAuthProvider: AuthProvider,
    private val tokenManager: TokenManager,
) : ViewModel() {
    private val _oldMyInfo = MutableStateFlow(UserInfoRes())
    var state by mutableStateOf<MyInfoState>(MyInfoState.Success)
        private set
    var myInfo by mutableStateOf(UserInfoDto())
        private set
    var isEdit by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<Int?>(null)
        private set
    var showLoadingDialog by mutableStateOf(false)
        private set
    var openGenderDialog by mutableStateOf(false)
        private set
    var openBirthDayPickerDialog by mutableStateOf(false)
        private set
    var openWithdrawalAlertDialog by mutableStateOf(false)
        private set

    init {
        getMyInfo()
    }

    fun onEvent(event: MyInfoEvent) {
        when (event) {
            is MyInfoEvent.InputName -> {
                myInfo = myInfo.copy(
                    name = event.name,
                    isValidNameFormat = checkName(event.name)
                )
            }
            is MyInfoEvent.SelectGender -> { myInfo = myInfo.copy(genderType = event.genderType) }
            is MyInfoEvent.InputProfileImage -> { myInfo = myInfo.copy(profileImage = event.profileUri) }
            is MyInfoEvent.InputPassword -> {
                myInfo = myInfo.copy(
                    password = event.password,
                    isValidPasswordFormat = checkPassword(event.password)
                )
                if (myInfo.isValidPasswordFormat) {
                    errorMessage = if (myInfo.password == myInfo.confirmPassword) null else R.string.error_password_confirm
                }
            }
            is MyInfoEvent.InputConfirmPassword -> {
                val passwordConfirmed = (myInfo.password == event.confirmPassword)

                myInfo = myInfo.copy(
                    confirmPassword = event.confirmPassword,
                    passwordSubmitted = (myInfo.isValidPasswordFormat == true) && passwordConfirmed
                )
                if (myInfo.isValidPasswordFormat) {
                    errorMessage = if (passwordConfirmed) null else R.string.error_password_confirm
                }
            }
            is MyInfoEvent.ShowGenderDialog -> { openGenderDialog = true }
            is MyInfoEvent.CloseGenderDialog -> {
                myInfo = myInfo.copy(genderType = _oldMyInfo.value.genderType)
                openGenderDialog = false
            }
            is MyInfoEvent.ConfirmGender -> { openGenderDialog = false }
            is MyInfoEvent.ShowBirthDayPickerDialog -> { openBirthDayPickerDialog = true }
            is MyInfoEvent.CloseBirthDayPickerDialog -> { openBirthDayPickerDialog = false }
            is MyInfoEvent.ConfirmBirthDayPickerDialog -> {
                myInfo = myInfo.copy(birthedAt = event.date)
                openBirthDayPickerDialog = false
            }
            is MyInfoEvent.Edit -> { isEdit = true }
            is MyInfoEvent.Save -> { save() }
            is MyInfoEvent.Cancel -> {
                reset()
                isEdit = false
            }
            is MyInfoEvent.SignOut -> { signOut(event.successEvent) }
            is MyInfoEvent.OnWithdrawalBtnClick -> { openWithdrawalAlertDialog = true }
            is MyInfoEvent.CancelWithdrawal -> { openWithdrawalAlertDialog = false }
            is MyInfoEvent.Withdraw -> {
                openWithdrawalAlertDialog = false
                withdraw(event.successEvent)
            }
        }
    }

    private fun checkName(name: String) =
        ValidationChecker.checkNameValidation(name).apply {
            errorMessage = second
        }.first

    private fun checkPassword(password: String) =
        ValidationChecker.checkPasswordValidation(password).apply {
            errorMessage = second
        }.first

    private fun reset() {
        myInfo = UserInfoDto(_oldMyInfo.value)
    }

    private fun getMyInfo() {
        viewModelScope.launch {
            showLoadingDialog = true
            delay(3000) // TODO : 서버와 연결 후에는 지울 것. 기다리는 최대 시간이 있어야 한다.

            try {
                val response = userRepository.getUserInfo()

                if (response.isSuccessful) {
                    val userInfoRes = response.body()!!

                    _oldMyInfo.value = userInfoRes
                    myInfo = UserInfoDto(userInfoRes)
                    return@launch
                }
                // TODO : 30X, 40X 처리
                // TODO : 로그인이 안 되어 있다면 로그인페이지로 이동
                state = MyInfoState.Fail
            } catch (e: IOException) {
                // TODO : 네트워크 오류 처리
                state = MyInfoState.Fail
            } finally {
                showLoadingDialog = false
            }
        }
    }

    private fun save() {
        viewModelScope.launch {

            showLoadingDialog = true
            if (myInfo.equalsTo(_oldMyInfo.value)) {
                isEdit = false
                showLoadingDialog = false
                return@launch
            }

            // 1. 이름 변경 있는지 체크 (변경이 있으면 2번, 3번)
            if (myInfo.name != _oldMyInfo.value.name) {
                // 2. 이름 규칙 지켰는지 체크
                if (myInfo.isValidNameFormat) {
                    // 3. 이름 중복 체크 (이름 중복 확인을 해달라는 오류 메시지)
                    if (checkUniqueName() == false) {
                        errorMessage = R.string.error_nickname_duplicate
                        showLoadingDialog = false
                        return@launch
                    } else {
                        errorMessage = null
                    }
                } else {
                    showLoadingDialog = false
                    return@launch
                }
            }

            // 4. 비밀번호 변경하려는 지 체크 (변경이 있으면 5번, 6번)
            if (myInfo.password.isNotBlank() || myInfo.confirmPassword.isNotBlank()) {
                // 5. 비밀번호 규칙 체크
                // 6. 비밀번호 확인 일치 체크
                if (myInfo.isValidPasswordFormat == false || myInfo.passwordSubmitted == false) {
                    return@launch
                }
            }

            val userInfoReq = myInfo.toUserInfoRequest()
            isEdit = !editMyInfo(userInfoReq)
            showLoadingDialog = false
        }

    }

    private fun signOut(successEvent: () -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    tokenManager.clearToken(_oldMyInfo.value.provider)
                }
            } catch (e: IOException) {
                // TODO : 오류 처리
            }

            when (_oldMyInfo.value.provider) {
                Provider.GOOGLE -> {
                    googleAuthProvider.signOut()
                }
                Provider.NAVER -> {
                    naverAuthProvider.signOut()
                }
                Provider.KAKAO -> {
                    kakaoAuthProvider.signOut()
                }
                Provider.LOCAL -> {}
            }

            successEvent()
        }
    }

    private fun withdraw(successEvent: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = userRepository.withdraw("")

                if (response.isSuccessful) {
                    if (response.body() == true) {
                        withContext(Dispatchers.IO) {
                            tokenManager.removeAll()
                        }
                        when (_oldMyInfo.value.provider) {
                            Provider.GOOGLE -> {
                                googleAuthProvider.withdraw()
                            }
                            Provider.NAVER -> {
                                naverAuthProvider.withdraw()
                            }
                            Provider.KAKAO -> {
                                kakaoAuthProvider.withdraw()
                            }
                            Provider.LOCAL -> {}
                        }
                        successEvent()
                    }
                    // TODO: 실패 시 오류처리
                }
            } catch (e: IOException) {
                // TODO : 네트워크 및 datastore 오류 처리
            } catch (e: HttpException) {
                // TODO : 30X, 40X 오류 처리
            }
        }

    }

    private suspend fun checkUniqueName(): Boolean {
        return viewModelScope.async {
            try {
                delay(3000) // TODO : 서버와 연결 후에는 지울 것. 기다리는 최대 시간이 있어야 한다.
                val response = signRepository.checkUniqueName(myInfo.name)
                response.body() ?: false
            } catch (e: IOException) {
                // TODO : 50X 오류 처리
                false
            } catch (e: HttpException) {
                // TODO : 30X, 40X 오류 처리
                false
            }
        }.await()
    }

    private suspend fun editMyInfo(userInfoReq: UserInfoRequest): Boolean {
        return viewModelScope.async {
            try {
                delay(3000) // TODO : 서버와 연결 후에는 지울 것. 기다리는 최대 시간이 있어야 한다.
                val response = userRepository.editUserInfo(userInfoReq)

                val userInfoRes = response.body()!!
                _oldMyInfo.value = userInfoRes
                myInfo = UserInfoDto(userInfoRes)
                true
            } catch (e: IOException) {
                // TODO : 네트워크 오류 처리
                false
            } catch (e: HttpException) {
                // TODO : 30X, 40X 오류 처리
                false
            }
        }.await()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ApplicationClass)
                val userRepository = application.container.userRepository
                val signRepository = application.container.signRepository
                val googleAuthProvider = application.container.googleAuthProvider
                val kakaoAuthProvider = application.container.kakaoAuthProvider
                val naverAuthProvider = application.container.naverAuthProvider
                val tokenManager = application.container.tokenManager

                MyInfoViewModel(
                    userRepository,
                    signRepository,
                    googleAuthProvider,
                    kakaoAuthProvider,
                    naverAuthProvider,
                    tokenManager,
                )
            }
        }
    }
}