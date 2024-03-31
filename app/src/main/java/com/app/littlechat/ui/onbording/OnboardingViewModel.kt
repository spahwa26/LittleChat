package com.app.littlechat.ui.onbording

import android.support.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.R
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.network.OnboardingRepository
import com.app.littlechat.utility.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val repository: OnboardingRepository) :
    ViewModel() {

    private val _uiState: MutableState<OnboardingState?> = mutableStateOf(null)
    val uiState: State<OnboardingState?> = _uiState

    fun loginUser(email: String, password: String) {
        if (email.isBlank()) {
            _uiState.value = OnboardingState.LocalError(R.string.enter_email)
            return
        }
        if (password.isBlank()) {
            _uiState.value = OnboardingState.LocalError(R.string.enter_password)
            return
        }

        if (!email.isValidEmail()) {
            _uiState.value = OnboardingState.LocalError(R.string.enter_valid_email)
            return
        }
        _uiState.value = OnboardingState.Loading
        viewModelScope.launch {
            repository.firebaseSignInWithEmailPassword(email, password) {
                when (it) {
                    is CustomResult.Success -> {
                        _uiState.value = OnboardingState.Success
                    }

                    is CustomResult.Error -> {
                        checkError(it)
                    }
                }
            }
        }
    }

    private fun checkError(it: CustomResult.Error) {
        when (it.errorCode) {
            VERIFICATION_EMAIL -> _uiState.value =
                OnboardingState.EmailSent(it.exception.message ?: "")

            GOTO_PROFILE -> _uiState.value =
                OnboardingState.GotoProfile

            else -> _uiState.value =
                OnboardingState.Error(it.exception.message ?: "")
        }
    }

    fun signupUser(email: String, password: String, confirmPassword: String) {
        if (email.isBlank()) {
            _uiState.value = OnboardingState.LocalError(R.string.enter_email)
            return
        }
        if (password.isBlank()) {
            _uiState.value = OnboardingState.LocalError(R.string.enter_password)
            return
        }
        if (confirmPassword.isBlank()) {
            _uiState.value = OnboardingState.LocalError(R.string.enter_confirm_password)
            return
        }

        if (!email.isValidEmail()) {
            _uiState.value = OnboardingState.LocalError(R.string.enter_valid_email)
            return
        }

        if (password != confirmPassword) {
            _uiState.value = OnboardingState.LocalError(R.string.password_not_match)
            return
        }
        _uiState.value = OnboardingState.Loading
        viewModelScope.launch {
            repository.signUp(email, password) {
                when (it) {
                    is CustomResult.Success -> {
                        _uiState.value = OnboardingState.Success
                    }

                    is CustomResult.Error -> {
                        checkError(it)
                    }
                }
            }
        }
    }

    fun setIdle() {
        _uiState.value= OnboardingState.Idle
    }

    sealed class OnboardingState {
        data object Idle : OnboardingState()
        data object Loading : OnboardingState()
        data object Success : OnboardingState()
        data class Error(val e: String) : OnboardingState()
        data class LocalError(@StringRes val e: Int) : OnboardingState()
        data object GotoProfile : OnboardingState()
        data class EmailSent(val email: String) : OnboardingState()
    }

    companion object {
        const val FAILURE_ERROR = 0
        const val GOTO_PROFILE = 1
        const val VERIFICATION_EMAIL = 2
        const val AUTH_FAIL = 3
    }

    // Success
//    startActivity(
//    Intent(
//    activity,
//    HomeScreen::class.java
//    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//    )

    //PROFILE
//    startActivity(
//    Intent(activity, Profile::class.java).putExtra("uid", userId)
//    .putExtra("email", email).putExtra("name", name)
//    )


//    CommonUtilities.showAlert(
//    this,
//    "A verification email has been sent to " + user.email + ", please verify the email then login.",
//    false,
//    true
//    )

}