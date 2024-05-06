package com.app.littlechat.ui.home.ui.findfriends

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.data.network.FindFriendsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class FindFriendsViewmodel @Inject constructor(private val repository: FindFriendsRepository) :
    ViewModel() {

    private val _friendsUiState: MutableState<FriendsUiState?> = mutableStateOf(null)
    val friendsUiState: State<FriendsUiState?> = _friendsUiState

    val searchText = mutableStateOf("")

    private var timer: Timer? = null

    fun updateText(it: String) {
        searchText.value = it
        cancelTimer()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                getFriendList()
            }

        }, 600)
    }

    private fun cancelTimer() {
        if (timer != null)
            timer?.cancel()
    }

    private fun getFriendList() {
        if (searchText.value.isBlank()) {
            _friendsUiState.value = FriendsUiState.Success(emptyList())
            return
        }
        _friendsUiState.value = FriendsUiState.Loading
        viewModelScope.launch {
            repository.searchFriends(searchText.value) {
                when (it) {
                    is CustomResult.Success -> {
                        _friendsUiState.value = FriendsUiState.Success(it.data)
                    }

                    is CustomResult.Error -> {
                        _friendsUiState.value = FriendsUiState.Error(it.exception.message)
                    }

                }
            }
        }
    }

    sealed class FriendsUiState {
        data object Loading : FriendsUiState()
        data class Success(val friendList: List<User>) : FriendsUiState()
        data class Error(val e: String?) : FriendsUiState()
    }

}