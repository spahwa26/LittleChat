package com.app.littlechat.ui.home.ui.group

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.littlechat.data.model.CustomResult
import com.app.littlechat.data.model.User
import com.app.littlechat.data.network.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewmodel @Inject constructor(private val repository: HomeRepository) :
    ViewModel() {

    private val _groupsUiState: MutableState<GroupsUiState?> = mutableStateOf(null)
    val groupsUiState: State<GroupsUiState?> = _groupsUiState

    val groupName = mutableStateOf("")

    var usersList = mutableStateListOf<User>()

    val selectedMembers = mutableStateListOf<User>()


    init {
        getFriends()
    }

    fun updateItem(user: User, index: Int) {
        usersList.removeAt(index)
        usersList.add(index, user)
        if (user.isAdded) selectedMembers.add(user)
        else {
            selectedMembers.removeIf {
                it.id == user.id
            }
        }

    }

    private fun getFriends() {
        _groupsUiState.value = GroupsUiState.Loading
        viewModelScope.launch {
            repository.getFriends {
                when (it) {
                    is CustomResult.Success -> {
                        _groupsUiState.value = GroupsUiState.Success(it.data)
                        usersList.addAll(it.data)
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
        data class Success(val groupList: List<User>) : GroupsUiState()
        data class Error(val e: String?) : GroupsUiState()
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListeners()
    }

}