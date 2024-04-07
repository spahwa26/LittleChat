package com.app.littlechat.ui.home.ui.profile

import android.graphics.Bitmap
import android.support.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.R
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.data.network.ProfileRepository
import com.app.littlechat.ui.home.navigation.HomeArgs
import com.app.littlechat.utility.Constants.Companion.FRIEND_LIST
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewmodel @Inject constructor(
    private val repository: ProfileRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String? = savedStateHandle[HomeArgs.USER_ID_ARG]

    private val _profileUiState: MutableState<ProfileUiState?> = mutableStateOf(null)
    val profileUiState: State<ProfileUiState?> = _profileUiState

    val userData = mutableStateOf<User?>(null)

    val btnText = mutableStateOf<BtnCall?>(null)

    private var btnCall = BtnCall.NONE

    val imageUri = mutableStateOf<Bitmap?>(null)

    val name = mutableStateOf("")

    val phone = mutableStateOf("")

    init {
        getUserData()
        getButtonText()
    }

    fun getImageName()=userPreferences.profilePic

    private fun getUserData() {
        _profileUiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            userId?.let {
                repository.getProfileData(userId) {
                    when (it) {
                        is CustomResult.Success -> {
                            setIdle()
                            userData.value = it.data
                            name.value = it.data.name
                            phone.value = it.data.phone_number
                        }

                        is CustomResult.Error -> {
                            _profileUiState.value = ProfileUiState.Error(it.exception.message)
                        }

                    }
                }
            }
        }
    }

    private fun saveProfileData() {
        if (name.value.isBlank()) {
            _profileUiState.value = ProfileUiState.LocalMessage(R.string.enter_valid_name)
            return
        }
        if (phone.value.length < 10) {
            _profileUiState.value = ProfileUiState.LocalMessage(R.string.enter_valid_number)
            return
        }
        userData.value?.let { user ->
            _profileUiState.value=ProfileUiState.Loading
            user.name = name.value
            user.phone_number = phone.value
            repository.saveProfileChanges(user, imageUri.value != null) {
                when (it) {
                    is CustomResult.Success -> {
                        imageUri.value=null
                        _profileUiState.value =
                            ProfileUiState.LocalMessage(R.string.profile_updated)
                    }

                    is CustomResult.Error -> {
                        _profileUiState.value = ProfileUiState.Error(it.exception.message)
                    }
                }
            }
        }


    }

    fun handleButtonClick() {
        when (btnCall) {
            BtnCall.SEND_REQUEST -> sendRequest()
            BtnCall.CANCEL_REQUEST -> cancelRequest()
            BtnCall.ACCEPT_REQUEST -> acceptRequest()
            BtnCall.SEND_MESSAGE -> {
                _profileUiState.value = ProfileUiState.SendMessage
            }

            BtnCall.SAVE -> saveProfileData()

            BtnCall.NONE -> {}
        }
    }

    private fun sendRequest() {
        userData.value?.let { user ->
            _profileUiState.value = ProfileUiState.Loading
            viewModelScope.launch {
                repository.sendRequest(user) {
                    when (it) {
                        is CustomResult.Success -> {
                            setIdle()
                            btnText.value = it.data
                            btnCall = BtnCall.CANCEL_REQUEST
                        }

                        is CustomResult.Error -> {
                            _profileUiState.value = ProfileUiState.Error(it.exception.message)
                        }
                    }
                }
            }
        }
    }

    fun cancelRequest() {
        userData.value?.let { user ->
            _profileUiState.value = ProfileUiState.Loading
            viewModelScope.launch {
                repository.cancelRequest(user.id) {
                    when (it) {
                        is CustomResult.Success -> {
                            setIdle()
                            btnText.value = it.data
                            btnCall = it.data
                        }

                        is CustomResult.Error -> _profileUiState.value =
                            ProfileUiState.Error(it.exception.message)
                    }
                }
            }
        }
    }

    private fun acceptRequest() {
        userData.value?.let { user ->
            _profileUiState.value = ProfileUiState.Loading
            viewModelScope.launch {
                repository.acceptRequest(user) {
                    when (it) {
                        is CustomResult.Success -> {
                            setIdle()
                            btnText.value = it.data
                            btnCall = it.data
                        }

                        is CustomResult.Error -> _profileUiState.value =
                            ProfileUiState.Error(it.exception.message)

                    }
                }
            }

        }
    }


    private fun getButtonText() {
        if (userPreferences.id == userId) {
            btnText.value = BtnCall.SAVE
            btnCall = BtnCall.SAVE
        } else {
            repository.findUserInRequestList(userId ?: "", FRIEND_LIST) {
                when (it) {
                    is CustomResult.Success -> {
                        btnText.value = it.data
                        btnCall = it.data
                    }

                    is CustomResult.Error -> btnText.value = BtnCall.NONE
                }
            }
        }
    }

    fun setIdle() {
        _profileUiState.value = null
    }

    fun isMyProfile() = userPreferences.id == userId

    sealed class ProfileUiState {
        data object Loading : ProfileUiState()
        data object SendMessage : ProfileUiState()
        data class Error(val e: String?) : ProfileUiState()
        data class LocalMessage(@StringRes val msg: Int) : ProfileUiState()
    }

}

enum class BtnCall(@StringRes var callText: Int) {
    NONE(callText = R.string.empt), SEND_MESSAGE(R.string.send_message), SEND_REQUEST(R.string.send_request), CANCEL_REQUEST(
        R.string.cancel_request
    ),
    ACCEPT_REQUEST(R.string.accept_request), SAVE(R.string.save)
}