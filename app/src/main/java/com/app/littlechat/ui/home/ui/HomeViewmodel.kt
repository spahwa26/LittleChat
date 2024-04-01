package com.app.littlechat.ui.home.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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


    init {
        getFriendList()
        getGroups()
    }

    private fun getFriendList(){
        _friendsUiState.value= FriendsUiState.Loading
        viewModelScope.launch {
            repository.getFriends {
                when(it)
                {
                    is CustomResult.Success->{
                        _friendsUiState.value= FriendsUiState.Success(it.data)
                    }
                    is CustomResult.Error->{
                        _friendsUiState.value= FriendsUiState.Error(it.exception.message)
                    }

                }
            }
        }
    }

    private fun getGroups() {
        _groupsUiState.value = GroupsUiState.Loading
        viewModelScope.launch {
            repository.getGroups {
                when (it) {
                    is CustomResult.Success -> {
                        _groupsUiState.value = GroupsUiState.Success(it.data)
                    }

                    is CustomResult.Error -> {
                        _groupsUiState.value = GroupsUiState.Error(it.exception.message)
                    }

                }
            }
        }
    }

    sealed class GroupsUiState {
        data object Loading : GroupsUiState()
        data class Success(val friendList: List<GroupDetails>) : GroupsUiState()
        data class Error(val e: String?) : GroupsUiState()
    }

    sealed class FriendsUiState {
        data object Loading : FriendsUiState()
        data class Success(val friendList: List<User>) : FriendsUiState()
        data class Error(val e: String?) : FriendsUiState()
    }

}