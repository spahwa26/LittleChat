package com.app.littlechat.ui.home.ui.friendrequests

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.data.network.ProfileRepository
import com.app.littlechat.utility.Constants.Companion.SHOW_NO_DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestViewmodel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _reqUiState: MutableState<RequestUiState?> = mutableStateOf(null)
    val reqUiState: State<RequestUiState?> = _reqUiState

    val userData = mutableStateListOf<User>()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    init {
        getRequestList()
    }

    fun getRequestList(isPullRefresh: Boolean = false) {
        if (isPullRefresh)
            _isRefreshing.value = true
        else
            _reqUiState.value = RequestUiState.Loading
        viewModelScope.launch {
            repository.getRequests {
                when (it) {
                    is CustomResult.Success -> {
                        userData.clear()
                        userData.addAll(it.data)
                        _reqUiState.value = RequestUiState.Success
                    }

                    is CustomResult.Error -> {
                        if (it.errorCode == SHOW_NO_DATA) {
                            userData.clear()
                            _reqUiState.value = RequestUiState.NoData
                        } else if (!it.exception.message.isNullOrBlank())
                            _reqUiState.value = RequestUiState.Error(it.exception.message)
                    }

                }
                if (isPullRefresh)
                    _isRefreshing.value = false
            }
        }
    }

    fun cancelRequest(id: String) {
        _reqUiState.value = RequestUiState.ActionLoading
        viewModelScope.launch {
            repository.cancelRequest(id) { result ->
                when (result) {
                    is CustomResult.Success -> {
                        _reqUiState.value = RequestUiState.Success
                        removeItem(id)
                    }

                    is CustomResult.Error -> _reqUiState.value =
                        RequestUiState.Error(result.exception.message)
                }
            }
        }
    }

    fun acceptRequest(user: User) {
        _reqUiState.value = RequestUiState.ActionLoading
        viewModelScope.launch {
            repository.acceptRequest(user) { result ->
                when (result) {
                    is CustomResult.Success -> {
                        _reqUiState.value = RequestUiState.Success
                        removeItem(user.id)
                    }

                    is CustomResult.Error -> _reqUiState.value =
                        RequestUiState.Error(result.exception.message)
                }
            }
        }
    }

    private fun removeItem(id: String) {
        userData.removeIf {
            it.id == id
        }
        if (userData.isEmpty())
            _reqUiState.value = RequestUiState.NoData
    }

    sealed class RequestUiState {
        data object NoData : RequestUiState()
        data object Success : RequestUiState()
        data object Loading : RequestUiState()
        data object ActionLoading : RequestUiState()
        data class Error(val msg: String?) : RequestUiState()
    }

}