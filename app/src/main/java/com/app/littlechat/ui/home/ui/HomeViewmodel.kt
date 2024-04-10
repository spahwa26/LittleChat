package com.app.littlechat.ui.home.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.GroupDetails
import com.app.littlechat.data.model.User
import com.app.littlechat.data.network.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    private val _friendsUiState: MutableState<FriendsUiState?> = mutableStateOf(null)
    val friendsUiState: State<FriendsUiState?> = _friendsUiState


    private val _groupsUiState: MutableState<GroupsUiState?> = mutableStateOf(null)
    val groupsUiState: State<GroupsUiState?> = _groupsUiState


    val isRefreshing = mutableStateOf(false)

    val userData = mutableStateListOf<User>()

    val groupsData = mutableStateListOf<GroupDetails>()


    init {
        getFriendList()
        getGroups()
    }

    fun getFriendList(isPullRefresh: Boolean = false) {
        if (isPullRefresh)
            isRefreshing.value = true
        else
            _friendsUiState.value = FriendsUiState.Loading
        viewModelScope.launch {
            repository.getFriends {
                when (it) {
                    is CustomResult.Success -> {
                        userData.clear()
                        if (it.data.isNotEmpty()) {
                            userData.addAll(it.data)
                            _friendsUiState.value = FriendsUiState.Success
                        } else
                            _friendsUiState.value = FriendsUiState.NoData
                    }

                    is CustomResult.Error -> {
                        _friendsUiState.value = FriendsUiState.Error(it.exception.message)
                    }
                }
                if (isPullRefresh)
                    isRefreshing.value = false
            }
        }
    }

    fun getGroups(isPullRefresh: Boolean = false) {
        if (isPullRefresh)
            isRefreshing.value = true
        else
            _friendsUiState.value = FriendsUiState.Loading

        viewModelScope.launch {
            repository.getGroups {
                when (it) {
                    is CustomResult.Success -> {
                        groupsData.clear()
                        if (it.data.isNotEmpty()) {
                            groupsData.addAll(it.data)
                            _groupsUiState.value = GroupsUiState.Success
                        } else
                            _groupsUiState.value = GroupsUiState.NoData

                    }

                    is CustomResult.Error -> {
                        _groupsUiState.value = GroupsUiState.Error(it.exception.message)
                    }
                }
                if (isPullRefresh)
                    isRefreshing.value = false
            }
        }
    }

    sealed class GroupsUiState {
        data object Loading : GroupsUiState()
        data object Success : GroupsUiState()
        data class Error(val e: String?) : GroupsUiState()
        data object NoData : GroupsUiState()
    }

    sealed class FriendsUiState {
        data object Loading : FriendsUiState()
        data object Success : FriendsUiState()
        data class Error(val e: String?) : FriendsUiState()
        data object NoData : FriendsUiState()
    }


    override fun onCleared() {
        super.onCleared()
        repository.removeFriendsListeners()
        repository.removeGroupListeners()
    }

}